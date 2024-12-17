package com.comet.floatingtwitter.common.storage

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.preferencesDataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.map

/**
 * PreferenceDataStore로 구현한 스토리지
 * @property context application Context
 */
class PreferenceDataStore(private val context : Context)  {


    suspend fun delete(key: Preferences.Key<Any>) {
        context.dataStore.edit { preference -> preference.remove(key) }
    }

    suspend fun putInt(key: Preferences.Key<Int>, value: Int) {
        context.dataStore.edit { preference -> preference[key] = value }
    }

    suspend fun getInt(key: Preferences.Key<Int>, defaultValue: Int): Int {
        return context.dataStore.data.map { preference -> preference[key] }.firstOrNull()
    }

    suspend fun putString(key: String, value: String): String {
        TODO("Not yet implemented")
    }

    suspend fun getString(key: String, defaultValue: String): Boolean {
        TODO("Not yet implemented")
    }
}

private val Context.dataStore : DataStore<Preferences> by preferencesDataStore(name = "twitter_setting")