package com.comet.floatingtwitter

import android.app.Application
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class FloatingTwitterApp : Application() {

    override fun onCreate() {
        super.onCreate()
    }
}