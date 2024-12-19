package com.comet.floatingtwitter.overlay.repository

import android.graphics.drawable.Drawable

/**
 * 오버레이 버블 관리하는 레포지토리
 */
interface BubbleRepository {

    /**
     * 서비스를 시작합니다. 연결될때까지 중단(suspend)됩니다.
     */
    suspend fun startService()

    /**
     * 서비스를 중단합니다.
     * 서비스가 실행중일때만 사용가능
     */
    suspend fun stopService()

    /**
     * 서비스가 동작중인지 반환합니다.
     */
    suspend fun isRunning() : Boolean

    /**
     * 카운터를 증가시킵니다.
     * 서비스가 실행중일때만 사용가능
     */
    suspend fun increaseCounter(amount : Int)

    /**
     * 아이콘을 변경합니다.
     * 서비스가 실행중일때만 사용가능
     */
    suspend fun changeIcon(drawable : Drawable)

    /**
     * 아이콘 색을 변경합니다.
     * 서비스가 실행중일때만 사용가능
     */
    suspend fun changeBackgroundColor(color : Int)
}