package com.comet.floatingtwitter.twitter.setting.repository

import com.comet.floatingtwitter.common.storage.LocalDataStorage
import com.comet.floatingtwitter.common.storage.PreferenceDataStore
import com.comet.floatingtwitter.twitter.setting.model.SettingData

class PreferenceSettingRepository(private val preferenceDataStore: LocalDataStorage) : SettingRepository {

    companion object {
        private const val SIZE_KEY = "SETTING_SIZE"
        private const val MENTION_KEY = "SETTING_MENTION_COLOR"
        private const val DM_KEY = "SETTING_DM_KEY"
        private const val BOTH_NOTIFY_KEY = "SETTING_BOTH_NOTIFY"
    }

    /**
     * 설정 불러오기.
     *
     * @return 저장된 값이 없을경우 null 리턴
     */
    override suspend fun loadSetting(): SettingData? {
        val size = preferenceDataStore.getInt(SIZE_KEY, -1)
        val mentionColor = preferenceDataStore.getString(MENTION_KEY, "")
        val directMessageColor = preferenceDataStore.getString(DM_KEY,  "")
        val bothNotifyColor = preferenceDataStore.getString(BOTH_NOTIFY_KEY,  "")

        if (size == -1 || mentionColor.isEmpty() || directMessageColor.isEmpty() || bothNotifyColor.isEmpty())
            return null

        return SettingData(size, mentionColor, directMessageColor, bothNotifyColor)
    }

    /**
     * 설정 저장하기
     */
    override suspend fun saveSetting(data: SettingData) {
        preferenceDataStore.putInt(SIZE_KEY, data.size)
        preferenceDataStore.putString(MENTION_KEY, data.mentionColor)
        preferenceDataStore.putString(DM_KEY, data.directMessageColor)
        preferenceDataStore.putString(BOTH_NOTIFY_KEY, data.bothNotifyColor)
    }

}