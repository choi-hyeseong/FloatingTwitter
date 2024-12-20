package com.comet.floatingtwitter.callback


interface ActivityCallback {

    fun switchMain()
    fun switchOAuth()
    fun switchSetting()

    /**
     * @throws IllegalStateException 이미 서비스가 실행중인경우 발생합니다.
     */
    fun startService()

    fun stopService()
}