package com.comet.floatingtwitter.service

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.content.res.Resources
import android.graphics.BitmapFactory
import android.graphics.Color
import android.os.Build
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.Gravity
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.content.res.AppCompatResources
import androidx.core.app.NotificationCompat
import androidx.core.graphics.drawable.RoundedBitmapDrawableFactory
import com.comet.floatingtwitter.BuildConfig
import com.comet.floatingtwitter.R
import com.comet.floatingtwitter.model.Settings
import com.siddharthks.bubbles.DefaultFloatingBubbleTouchListener
import com.siddharthks.bubbles.FloatingBubbleConfig
import com.siddharthks.bubbles.FloatingBubbleService
import com.twitter.clientlib.ApiException
import com.twitter.clientlib.TwitterCredentialsOAuth2
import com.twitter.clientlib.api.TwitterApi
import okhttp3.OkHttpClient
import okhttp3.Request
import org.json.JSONObject
import java.net.HttpURLConnection
import java.net.URL

const val CHANNEL_ID: String = "NOTIFICATION_FLOATING";
const val DM_URL : String = "https://api.twitter.com/2/dm_events?dm_event.fields=id,text,event_type,dm_conversation_id,created_at,sender_id,attachments,participant_ids,referenced_tweets&event_types=MessageCreate&user.fields=created_at,description,id,location,name,pinned_tweet_id,public_metrics,url,username&expansions=sender_id,referenced_tweets.id,attachments.media_keys,participant_ids"

class FloatingService : FloatingBubbleService(), Runnable {

    private var thread: Thread? = null;
    private var isRunning: Boolean = true;
    private var setting: Settings? = null;
    private var id: String? = null;
    private var tweet: TwitterApi? = null;
    private var lastMentionId: String? = null;
    private var lastDmId: String? = null;
    private var counter: Int = 0;

    override fun onCreate() {
        super.onCreate()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val title = getString(R.string.app_name)
            val manager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            var channel = manager.getNotificationChannel(CHANNEL_ID)
            if (channel == null) {
                channel =
                    NotificationChannel(CHANNEL_ID, title, NotificationManager.IMPORTANCE_HIGH)
                manager.createNotificationChannel(channel)
            }
            val notification = NotificationCompat.Builder(this, CHANNEL_ID).build();
            startForeground(1, notification);

        }
    }

    override fun getConfig(): FloatingBubbleConfig {
        return FloatingBubbleConfig.Builder()
            .bubbleIcon(AppCompatResources.getDrawable(this, R.drawable.ic_launcher))
            .removeBubbleIcon(AppCompatResources.getDrawable(this, R.drawable.trashcan))
            .bubbleIconDp(setting?.size ?: 64)
            .paddingDp(4)
            .borderRadiusDp(4)
            .physicsEnabled(false)
            .expandableColor(Color.WHITE).triangleColor(Color.WHITE).gravity(Gravity.BOTTOM)
            .touchClickTime(250)
            .notificationBackgroundColor(Color.BLUE).build();
    }

    override fun onGetIntent(intent: Intent): Boolean {
        return true;
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        setting = intent?.getSerializableExtra("setting") as Settings
        Thread {
            //실질적 시작 로직 (초기화 이후)
            Thread.sleep(2000);
            executeReflection()
            loadData()
            thread = Thread(this).apply { start() }
        }.start()
        return super.onStartCommand(intent, flags, startId)
    }

    private fun loadData() {
        tweet = TwitterApi(
            TwitterCredentialsOAuth2(
                BuildConfig.CLIENT_ID,
                BuildConfig.CLIENT_SECRET,
                setting?.token,
                setting?.refresh
            )
        )
        try {
            val set = HashSet<String>();
            set.add("profile_image_url");
            val result = tweet!!.users().findMyUser().userFields(set).execute().data;

            id = result?.id;
            val url = result?.profileImageUrl;
            url?.let {
                val bigger = URL(url.toString().split("_normal")[0] + "_400x400.jpg")
                val connection = bigger.openConnection() as HttpURLConnection
                val input = connection.inputStream;
                val decode = BitmapFactory.decodeStream(input)
                handleUI {
                    updateBubbleIcon(
                        RoundedBitmapDrawableFactory.create(
                            Resources.getSystem(),
                            decode
                        ).apply { cornerRadius = 360f })
                }
            }
        } catch (e: ApiException) {
            handleUI {
                Toast.makeText(context, "OAuth2 토큰이 만료되었습니다. 토큰을 다시 등록해주세요.", Toast.LENGTH_LONG)
                    .show()
                stopSelf()
            }
        }

    }

    private fun executeReflection() {
        val field = Class.forName("com.siddharthks.bubbles.FloatingBubbleService")
            .getDeclaredField("touch");
        field.isAccessible = true
        val obj = field.get(this)
        val listener = Class.forName("com.siddharthks.bubbles.FloatingBubbleTouch")
            .getDeclaredField("listener")
        val defaultListener = object : DefaultFloatingBubbleTouchListener() {
            override fun onTap(expanded: Boolean) {
                val twit =
                    context.packageManager.getLaunchIntentForPackage("com.twitter.android")
                startActivity(twit)
                decreaseNotificationCounterBy(counter);
                counter = 0;
                setState(false)
            }

            override fun onRemove() {
                Log.d("dfd", "호출됨~")
                stopSelf()
            }
        }
        listener.isAccessible = true;
        listener.set(obj, defaultListener);
    }

    override fun onDestroy() {
        isRunning = false;
        thread?.interrupt();
        super.onDestroy()
    }

    override fun run() {
        while (isRunning || Thread.interrupted()) {
            try {
                var mention: Boolean = false;
                var dm: Boolean = false;

                val result = tweet?.tweets()?.usersIdMentions(id)?.execute()?.data;
                if (lastMentionId == null) {
                    //서비스 처음 시작된경우
                    result?.get(0).apply { lastMentionId = this?.id }
                } else {
                    result?.apply {
                        if (get(0).id != lastMentionId) {
                            for (i in 0 until size) {
                                if (get(i).id == lastMentionId) {
                                    handleUI() { increaseNotificationCounterBy(i + counter) } //작동안될리는 없긴한데.. 음.. 멘션 많이오면 안될수도..?
                                    counter += i
                                    break
                                }
                            }
                            mention = true;
                            lastMentionId = get(0).id;
                        }
                    }
                }
                val request = Request.Builder().url(URL(DM_URL)).header("Authorization", "Bearer ${setting?.token}").header("Accept", "/*/").header("Connection", "keep-alive").build()
                val response = OkHttpClient().newCall(request).execute();
                response.body?.let {
                    val jObj = JSONObject(response.body!!.string())
                    val array = jObj.getJSONArray("data")
                    if (lastDmId == null) {
                        lastDmId = array.getJSONObject(0).getString("id")
                    }
                    else {
                        if (array.getJSONObject(0).getString("id") != lastMentionId) {
                            for (i in 0 until array.length()) {
                                if (array.getJSONObject(i).getString("id") == lastMentionId) {
                                    handleUI() { increaseNotificationCounterBy(i + counter) }
                                    counter += i
                                    break
                                }
                            }
                            dm = true;
                            lastMentionId = array.getJSONObject(0).getString("id");
                        }
                    }
                }
                var color : Int? = Color.RED;
                when {
                    dm && mention -> color = setting?.twin;
                    dm -> color = setting?.dm
                    mention -> setting?.mention
                }
                bubbleView.findViewById<ImageView>(com.siddharthks.bubbles.R.id.notification_background).setColorFilter(color!!);
                Thread.sleep(60000)
            } catch (e: InterruptedException) {
                break
            }
        }
    }

    private fun handleUI(run: Runnable) {
        Handler(Looper.getMainLooper()).post(run);
    }


}
