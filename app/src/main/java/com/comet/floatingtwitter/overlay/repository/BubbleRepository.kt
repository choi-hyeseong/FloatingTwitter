package com.comet.floatingtwitter.overlay.repository

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
     */
    suspend fun stopService()

    /**
     * 카운터를 증가시킵니다.
     */
    suspend fun increaseCounter(amount : Int)

    /**
     * 카운터를 감소시킵니다.
     */
    suspend fun decreaseCounter(amount : Int)

    /**
     * 카운터를 초기화 시킵니다.
     */
    suspend fun resetCounter()
}