package com.comet.floatingtwitter.util

import android.content.SharedPreferences

class PreferenceUtil private constructor(private val manager: SharedPreferences) {

    companion object {
        //singleton
        lateinit var INSTANCE : PreferenceUtil

        fun init(manager: SharedPreferences) {
            INSTANCE = PreferenceUtil(manager)
        }
    }

    fun getString(key: String) : String?{
       return manager.getString(key, null)
    }

    fun putString(key: String, value: String?) {
        value?.let {
            manager.edit().putString(key, value).apply()
        }
    }

    fun getInt(key: String) : Int {
        return manager.getInt(key, -1)
    }

    fun putInt(key: String, value: Int) {
        manager.edit().putInt(key, value).apply()
    }

}