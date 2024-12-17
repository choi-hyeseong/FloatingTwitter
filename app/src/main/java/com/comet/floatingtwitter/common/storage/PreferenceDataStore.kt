package com.comet.floatingtwitter.common.storage

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.preferencesDataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.forEach
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

/**
 * PreferenceDataStore로 구현한 스토리지.
 * 기존 LocalDataStorage를 사용하기 위해 global한 keyStore 사용
 *
 * @property context application Context
 * @property globalKeyMap String을 key로 하는 글로벌한 키맵. 키의 제네릭 정보가 묻히긴 하지만, 적절하게 get할때 복호화 하기
 */
class PreferenceDataStore(private val context : Context) : LocalDataStorage {

    //global key map
    private val globalKeyMap : MutableMap<Any, Preferences.Key<*>> = mutableMapOf()

    init {
        // 20:54 - globalKeyMap은 메모리상에 존재하므로 앱 종료후 다시 시작되면 초기화됨..
        // class init시 파일에 저장된 키값을 메모리에 로드하기
        runBlocking {
            val keySet = context.dataStore.data.map { it.asMap().keys }.firstOrNull() ?: return@runBlocking //키 - 값 리스트 불러오기. 없을경우 return
            val keyPairMap = keySet.map { it.name to it } //name - key 매핑
            globalKeyMap.putAll(keyPairMap) //맵에 집어넣기
        }
    }
    override suspend fun delete(key: String) {
        val preferenceKey = globalKeyMap[key] ?: return
        context.dataStore.edit { preference -> preference.remove(preferenceKey) }
    }

    override suspend fun putInt(key: String, value: Int) {
        val preferenceKey = intPreferencesKey(key) //key init
        context.dataStore.edit { preference -> preference[preferenceKey] = value } //update
        globalKeyMap[key] = preferenceKey //insert key
    }

    override suspend fun getInt(key: String, defaultValue: Int): Int {
        return getObject(key) ?: defaultValue
    }

    override suspend fun putString(key: String, value: String) {
        val preferenceKey = stringPreferencesKey(key) //key init
        context.dataStore.edit { preference -> preference[preferenceKey] = value } //update
        globalKeyMap[key] = preferenceKey //insert key
    }

    override suspend fun getString(key: String, defaultValue: String): String {
        return getObject(key) ?: defaultValue
    }

    override suspend fun hasKey(key : String) : Boolean {
        return true == context.dataStore.data.map { it.asMap().keys.map { key -> key.name } }.firstOrNull()?.contains(key)
    }

    // object 가져올때 map에 저장된 키값 가져옴. 캐스팅 실패거나 값이 없을경우 null 리턴
    private suspend fun <T> getObject(key : String) : T? {
        val preferenceKey = globalKeyMap[key] as Preferences.Key<T>?
        return if (preferenceKey == null) null
        else context.dataStore.data.map { preference -> preference[preferenceKey] }.firstOrNull()
    }
}

private val Context.dataStore : DataStore<Preferences> by preferencesDataStore(name = "twitter_storage")