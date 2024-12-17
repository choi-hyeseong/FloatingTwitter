package com.comet.floatingtwitter.overlay.repository

import android.content.ComponentName
import android.content.Context
import android.content.ServiceConnection
import android.os.IBinder
import com.comet.floatingtwitter.overlay.service.FloatingService
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

class OverlayBubbleRepository(private val context : Context) : BubbleRepository {

    private var service : FloatingService? = null

    override suspend fun startService() {
        // 일반 suspend - Exception으로 예외 처리가능, Cancellable - cancel 가능 (exception optional)
        suspendCancellableCoroutine<Unit> { continuation ->
            val callback : ServiceConnection = object : ServiceConnection {
                override fun onServiceConnected(p0: ComponentName?, p1: IBinder?) {
                    service = (p1 as FloatingService.FloatingServiceBinder).getService()
                    continuation.resume(Unit)
                }

                override fun onServiceDisconnected(p0: ComponentName?) {
                    service = null
                    continuation.cancel()
                }


            }

        }
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