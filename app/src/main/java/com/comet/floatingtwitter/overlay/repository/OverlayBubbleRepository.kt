package com.comet.floatingtwitter.overlay.repository

import android.content.ComponentName
import android.content.Context
import android.content.ServiceConnection
import android.graphics.drawable.Drawable
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
        suspendCancellableCoroutine { continuation ->
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
        if (!isRunning())
            return
        service?.stopService()
        service = null
    }

    override suspend fun isRunning(): Boolean {
        return service != null && service!!.isRunning
    }

    override suspend fun increaseCounter(amount: Int) {
        if (isRunning())
            service?.increaseCounter(amount)
    }

    override suspend fun changeIcon(drawable: Drawable) {
        if (isRunning())
            service?.changeIcon(drawable)
    }

    override suspend fun changeBackgroundColor(color: Int) {
        if (isRunning())
            service?.changeBackgroundColor(color)
    }


}