package com.comet.floatingtwitter.overlay.service

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.content.res.Resources
import android.graphics.BitmapFactory
import android.graphics.Color
import android.graphics.drawable.Drawable
import android.os.Binder
import android.os.Build
import android.os.Handler
import android.os.IBinder
import android.os.Looper
import android.view.Gravity
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.content.res.AppCompatResources
import androidx.core.app.NotificationCompat
import androidx.core.graphics.drawable.RoundedBitmapDrawableFactory
import com.comet.floatingtwitter.R
import com.comet.floatingtwitter.model.Settings
import com.comet.floatingtwitter.twitter.oauth.usecase.LoadTokenUseCase
import com.comet.floatingtwitter.twitter.setting.usecase.LoadSettingUseCase
import com.siddharthks.bubbles.FloatingBubbleConfig
import com.siddharthks.bubbles.FloatingBubbleService
import com.siddharthks.bubbles.FloatingBubbleTouchListener
import com.twitter.clientlib.ApiException
import com.twitter.clientlib.TwitterCredentialsOAuth2
import com.twitter.clientlib.api.TwitterApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import org.json.JSONObject
import java.net.HttpURLConnection
import java.net.URL

const val CHANNEL_ID: String = "NOTIFICATION_FLOATING"
const val DM_URL: String = "https://api.twitter.com/2/dm_events?dm_event.fields=id,text,event_type,dm_conversation_id,created_at,sender_id,attachments,participant_ids,referenced_tweets&event_types=MessageCreate&user.fields=created_at,description,id,location,name,pinned_tweet_id,public_metrics,url,username&expansions=sender_id,referenced_tweets.id,attachments.media_keys,participant_ids"

class FloatingService : FloatingBubbleService(), Runnable {

    // hilt inject
    lateinit var loadTokenUseCase: LoadTokenUseCase // authorization token load
    lateinit var loadSettingUseCase: LoadSettingUseCase // setting load

    private lateinit var thread: Thread
    private lateinit var setting: Settings
    private lateinit var id: String
    private lateinit var tweet: TwitterApi
    private var lastMentionId: String = String()
    private var lastDmId: String = String()
    private var counter: Int = 0

    override fun getConfig(): FloatingBubbleConfig {
        return FloatingBubbleConfig.Builder()
            .bubbleIcon(AppCompatResources.getDrawable(this, R.drawable.ic_launcher))
            .removeBubbleIcon(AppCompatResources.getDrawable(this, R.drawable.trashcan))
            .bubbleIconDp(setting.size)
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
    }

    private fun stopService() {
        // 바인딩 할지말지 고민해보기 - 액티비티에서 끄는 버튼도 있음.
        // onDestory랑 stopSelf 호출될때 넣을까 생각도 해봄. 어처피 서비스 종료되면 리스닝도 끝나야 하므로.
    }


    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        val id = super.onStartCommand(intent, flags, startId)
        startService() //서비스 시작
        return id
    }

    override fun onDestroy() {
        thread.interrupt()
        super.onDestroy()
    }


    private fun loadData() {
        Thread {
            try {
                tweet = TwitterApi(TwitterCredentialsOAuth2(
                    "BuildConfig.CLIENT_ID", "BuildConfig.CLIENT_SECRET", setting.token, setting.refresh).apply { isOAUth2AutoRefreshToken = true })
                try {
                    val set = HashSet<String>()
                    set.add("profile_image_url")
                    val result = tweet.users().findMyUser().userFields(set).execute().data
                    if (result != null) {
                        id = result.id
                        val url = result.profileImageUrl
                        url?.apply {
                            val bigger = URL(url.toString().split("_normal")[0] + "_400x400.jpg")
                            val connection = bigger.openConnection() as HttpURLConnection
                            val input = connection.inputStream
                            val decode = BitmapFactory.decodeStream(input)
                            handleUI {
                                updateBubbleIcon(RoundedBitmapDrawableFactory.create(
                                    Resources.getSystem(), decode)
                                    .apply { cornerRadius = 360f })
                            }
                        }
                    }
                }
                catch (e: ApiException) {
                    handleUI {
                        Toast.makeText(
                            context, "OAuth2 토큰이 만료되었습니다. 토큰을 다시 등록해주세요.", Toast.LENGTH_LONG).show()
                        stopSelf()
                    }
                }
            }
            catch (e: InterruptedException) {
                e.printStackTrace()
            }
        }.start()

    }


    override fun run() {
        while (!Thread.interrupted()) {
            try {
                var mention = false
                var dm = false
                Thread.sleep(7000)
                val result = tweet.tweets()?.usersIdMentions(id)?.execute()?.data
                if (lastMentionId.isEmpty()) {
                    //서비스 처음 시작된경우
                    result?.get(0).apply {
                        lastMentionId = this?.id!!
                    }
                }
                else {
                    //let 쓰면 객체 자체가 바뀜
                    result?.apply {
                        if (get(0).id != lastMentionId) {
                            var updates = false
                            for (i in 0 until size) {
                                if (get(i).id == lastMentionId) {
                                    handleUI { increaseNotificationCounterBy(i) } //작동안될리는 없긴한데.. 음.. 멘션 많이오면 안될수도..? <- 2년전에 나 뭐함?. 한번에 업데이트 하라고..
                                    counter += i
                                    updates = true
                                    break
                                }
                            }
                            mention = true
                            lastMentionId = get(0).id
                            if (!updates) increaseNotificationCounterBy(1)
                        }
                    }
                }
                val request = Request.Builder().url(URL(DM_URL)).header(
                    "Authorization", "Bearer ${setting.token}").header("Accept", "/*/").header(
                    "Connection", "keep-alive").build()
                val response = OkHttpClient().newCall(request).execute()
                response.body?.apply {
                    val jObj = JSONObject(response.body!!.string())
                    val array = jObj.getJSONArray("data")
                    if (lastDmId.isEmpty()) {
                        lastDmId = array.getJSONObject(0).getString("id")
                    }
                    else {
                        if (array.getJSONObject(0).getString("id") != lastDmId) {
                            var updates = false
                            for (i in 0 until array.length()) {
                                if (array.getJSONObject(i).getString("id") == lastDmId) {
                                    handleUI { increaseNotificationCounterBy(i) }
                                    counter += i
                                    updates = true
                                    break
                                }
                            }
                            dm = true
                            lastDmId = array.getJSONObject(0).getString("id")
                            if (!updates) increaseNotificationCounterBy(1)
                            //나 뭐해...
                        }
                    }
                }
                if (mention || dm) {
                    var color: Int? = Color.RED
                    when {
                        dm && mention -> color = setting.twin
                        dm -> color = setting.dm
                        mention -> setting.mention
                    }
                    bubbleView.findViewById<ImageView>(com.siddharthks.bubbles.R.id.notification_background)
                        .setColorFilter(
                            color!!)
                }
                Thread.sleep(53000)
            }
            catch (e: Exception) {
                if (e is InterruptedException) break
                else e.printStackTrace()
            }
        }
    }

    private fun handleUI(run: Runnable) {
        Handler(Looper.getMainLooper()).post(run)
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
