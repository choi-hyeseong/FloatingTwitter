package com.comet.floatingtwitter.overlay.repository

import android.content.ComponentName
import android.content.Context
import android.content.ServiceConnection
import android.os.IBinder
import com.comet.floatingtwitter.overlay.service.FloatingService
import kotlinx.coroutines.suspendCancellableCoroutine

class OverlayBubbleRepository(private val context : Context) : BubbleRepository {

    private var service : FloatingService? = null

    override suspend fun startService() {
        // todo. 이때 알림창 정보도 여기다 넣을지, 하드코딩 할지 검토


    }

    override suspend fun stopService() {
        TODO("Not yet implemented")
    }

    override suspend fun increaseCounter(amount: Int) {
        TODO("Not yet implemented")
    }

    override suspend fun decreaseCounter(amount: Int) {
        TODO("Not yet implemented")
    }

    override suspend fun resetCounter() {
        TODO("Not yet implemented")
    }

}