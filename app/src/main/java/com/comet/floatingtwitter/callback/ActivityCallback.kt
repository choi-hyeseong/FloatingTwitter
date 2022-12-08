package com.comet.floatingtwitter.callback

import com.comet.floatingtwitter.model.Settings

interface ActivityCallback {

    fun switchSetting()

    fun startService(setting : Settings)

    fun stopService()
}