package com.comet.floatingtwitter.util

import android.content.SharedPreferences
import android.content.SharedPreferences.Editor


class PreferenceUtil private constructor(manager: SharedPreferences) {

    private lateinit var manager : SharedPreferences

    companion object {
        //singleton
        var INSTANCE : PreferenceUtil? = null;

        fun init(manager: SharedPreferences) {
            INSTANCE = PreferenceUtil(manager)
        }
    }

    fun getString(key: String) : String?{
       return manager.getString(key, null);
    }

    fun putString(key: String, value: String?) {
        value?.let {
            manager.edit().putString(key, value).apply()
        }
    }

}