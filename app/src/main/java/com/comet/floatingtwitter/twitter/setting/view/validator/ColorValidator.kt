package com.comet.floatingtwitter.twitter.setting.view.validator

import android.graphics.Color

// simple color validator
class ColorValidator {

    fun validate(color : String) : Boolean {
        return kotlin.runCatching {
            Color.parseColor(color)
        }.isSuccess
    }
}