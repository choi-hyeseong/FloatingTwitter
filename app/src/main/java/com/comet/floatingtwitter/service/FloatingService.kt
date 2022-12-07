package com.comet.floatingtwitter.service

import android.content.Intent
import android.graphics.Color
import android.view.Gravity
import com.comet.floatingtwitter.R
import com.siddharthks.bubbles.FloatingBubbleConfig
import com.siddharthks.bubbles.FloatingBubbleService



class FloatingService : FloatingBubbleService() {

    val thread: Thread? = null;
    var token: String? = null;

    override fun getConfig(): FloatingBubbleConfig {
        return FloatingBubbleConfig.Builder()
            .bubbleIcon(resources.getDrawable(R.drawable.ic_launcher_foreground))
            .removeBubbleIcon(resources.getDrawable(R.drawable.ic_launcher_background))
            .bubbleIconDp(64)
            .paddingDp(4)
            .borderRadiusDp(4)
            .expandableColor(Color.WHITE).triangleColor(Color.WHITE).gravity(Gravity.END)
            .notificationBackgroundColor(Color.BLUE).build();
    }

    override fun onGetIntent(intent: Intent): Boolean {
        return true;
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        //token = intent?.getStringExtra("token")
        return super.onStartCommand(intent, flags, startId)
    }

    fun execute() {

    }
}
