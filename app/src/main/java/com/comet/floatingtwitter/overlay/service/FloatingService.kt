package com.comet.floatingtwitter.overlay.service

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.Drawable
import android.os.Build
import android.util.Log
import android.view.Gravity
import android.widget.ImageView
import androidx.appcompat.content.res.AppCompatResources
import androidx.core.app.NotificationCompat
import com.comet.floatingtwitter.R
import com.comet.floatingtwitter.getClassName
import com.comet.floatingtwitter.twitter.api.model.EventType
import com.comet.floatingtwitter.twitter.api.usecase.GetAvatarResourceUseCase
import com.comet.floatingtwitter.twitter.api.usecase.GetUserInfoUseCase
import com.comet.floatingtwitter.twitter.api.usecase.StartAPIListeningUseCase
import com.comet.floatingtwitter.twitter.api.usecase.StopAPIListeningUseCase
import com.comet.floatingtwitter.twitter.oauth.model.OAuthToken
import com.comet.floatingtwitter.twitter.oauth.usecase.LoadTokenUseCase
import com.comet.floatingtwitter.twitter.setting.model.SettingData
import com.comet.floatingtwitter.twitter.setting.usecase.LoadSettingUseCase
import com.siddharthks.bubbles.FloatingBubbleConfig
import com.siddharthks.bubbles.FloatingBubbleService
import com.siddharthks.bubbles.FloatingBubbleTouchListener
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import javax.inject.Inject

const val CHANNEL_ID: String = "NOTIFICATION_FLOATING"
const val DM_URL: String = "https://api.twitter.com/2/dm_events?dm_event.fields=id,text,event_type,dm_conversation_id,created_at,sender_id,attachments,participant_ids,referenced_tweets&event_types=MessageCreate&user.fields=created_at,description,id,location,name,pinned_tweet_id,public_metrics,url,username&expansions=sender_id,referenced_tweets.id,attachments.media_keys,participant_ids"

@AndroidEntryPoint
class FloatingService : FloatingBubbleService() {

    // hilt inject
    @Inject
    lateinit var loadTokenUseCase: LoadTokenUseCase // authorization token load

    @Inject
    lateinit var loadSettingUseCase: LoadSettingUseCase // setting load

    @Inject
    lateinit var startAPIListeningUseCase: StartAPIListeningUseCase // api listening

    @Inject
    lateinit var stopAPIListeningUseCase: StopAPIListeningUseCase // api stop usecase

    @Inject
    lateinit var getAvatarResourceUseCase: GetAvatarResourceUseCase // get avatar resource

    @Inject
    lateinit var getUserInfoUseCase: GetUserInfoUseCase // get user info

    lateinit var settingData: SettingData // 추후 init될 설정 데이터. getConfig 보다 이전에 호출됨
    lateinit var oAuthToken: OAuthToken // 토큰 정보


    override fun getConfig(): FloatingBubbleConfig {
        return FloatingBubbleConfig.Builder()
            .bubbleIcon(AppCompatResources.getDrawable(this, R.drawable.ic_launcher))
            .removeBubbleIcon(AppCompatResources.getDrawable(this, R.drawable.trashcan))
            .bubbleIconDp(settingData.size)
            .paddingDp(4)
            .borderRadiusDp(4)
            .moveBubbleOnTouch(false)
            .physicsEnabled(false)
            .bubbleTouchListener(getTouchListener())
            .expandableColor(Color.WHITE)
            .triangleColor(Color.WHITE)
            .gravity(Gravity.END)
            .touchClickTime(250)
            .notificationBackgroundColor(Color.WHITE)
            .build()
    }

    override fun onGetIntent(intent: Intent): Boolean {
        return true
    }

    override fun onCreate() {
        super.onCreate()
        // 최초로 class init시. service 시작시 onStartCommand 호출
    }

    // OREO 이상 notification 수행
    private fun startNotification() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val title = getString(R.string.app_name)
            val manager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            var channel = manager.getNotificationChannel(CHANNEL_ID)
            if (channel == null) {
                channel = NotificationChannel(CHANNEL_ID, title, NotificationManager.IMPORTANCE_HIGH)
                manager.createNotificationChannel(channel)
            }
            val notification = NotificationCompat.Builder(this, CHANNEL_ID).build()
            startForeground(1, notification)
        }
    }

    fun increaseCounter(count: Int) {
        increaseNotificationCounterBy(count)
    }

    fun resetCounter() {
        resetNotificationCounter()
    }

    fun changeIcon(drawable: Drawable) {
        updateBubbleIcon(drawable)
    }

    fun changeBackgroundColor(color: Int) {
        bubbleView.findViewById<ImageView>(com.siddharthks.bubbles.R.id.notification_background)
            .setColorFilter(color)

    }

    private fun startService() {
        startNotification()
        CoroutineScope(Dispatchers.IO).launch {
            val userResult = getUserInfoUseCase(oAuthToken)
            if (userResult.isFailure) { // 유저 정보를 가져오지 못한경우
                stopService()
                Log.w(getClassName(), "can't get user info")
                Log.e(getClassName(), "${userResult.exceptionOrNull()}")
                return@launch
            }
            val user = userResult.getOrThrow() // 위에서 체크했으므로 throw X

            val avatarResult = getAvatarResourceUseCase(user, oAuthToken)
            if (avatarResult.isFailure) {
                stopService()
                Log.w(getClassName(), "can't get user avatar info")
                Log.e(getClassName(), "${userResult.exceptionOrNull()}")
                return@launch
            }
            val avatar = avatarResult.getOrThrow()
            withContext(Dispatchers.Main) {
                changeIcon(avatar) // 버블 아이콘 업데이트
            }

            startAPIListeningUseCase(user, oAuthToken) { event ->
                val amount = event.sumOf { it.amount } // 총 notify할 수량
                if (amount != 0) {
                    val firstEvent = event[0]
                    // color setup
                    val color = if (event.size == 2) settingData.bothNotifyColor
                    else if (firstEvent.type == EventType.DM) settingData.directMessageColor
                    else settingData.mentionColor

                    // main dispatcher에서 setup
                    CoroutineScope(Dispatchers.Main).launch {
                        increaseCounter(amount)
                        changeBackgroundColor(Color.parseColor(color))
                    }
                }
            }
        }
    }

    private fun stopService() {
        // 바인딩 할지말지 고민해보기 - 액티비티에서 끄는 버튼도 있음.
        // onDestory랑 stopSelf 호출될때 넣을까 생각도 해봄. 어처피 서비스 종료되면 리스닝도 끝나야 하므로.
        stopSelf()
        CoroutineScope(Dispatchers.IO).launch {
            stopAPIListeningUseCase()
        }
    }

    private fun beforeInit() {
        // onStartCommand 호출전 init하기
        runBlocking {
            // assertion 넣은 이유는 서비스 시작하기전 VM에서 값이 존재하는지 체크를 진행하기 때문.
            // assertion 안넣을거면 isSettingSaved 같은 유스케이스 만들어도 좋을듯
            settingData = loadSettingUseCase()!!
            oAuthToken = loadTokenUseCase()!!
        }
    }


    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        beforeInit()
        val id = super.onStartCommand(intent, flags, startId)
        startService() //서비스 시작
        return id
    }

    override fun onDestroy() {
        super.onDestroy()
    }

    private fun getTouchListener(): FloatingBubbleTouchListener {
        return object : FloatingBubbleTouchListener {
            override fun onDown(x: Float, y: Float) {
            }

            override fun onTap(expanded: Boolean) {
                resetCounter() // 카운터 리셋
                startActivity(context.packageManager.getLaunchIntentForPackage("com.twitter.android")) // 짹짹이 시작
                setState(false) // 안펼쳐지게
            }

            override fun onRemove() {
                stopService()

            }

            override fun onMove(x: Float, y: Float) {
            }

            override fun onUp(x: Float, y: Float) {
            }
        }

    }

}
