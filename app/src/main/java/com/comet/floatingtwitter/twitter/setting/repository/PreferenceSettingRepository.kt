package com.comet.floatingtwitter.twitter.setting.repository

import com.comet.floatingtwitter.common.storage.PreferenceDataStore
import com.comet.floatingtwitter.twitter.setting.model.SettingData

class PreferenceSettingRepository(private val preferenceDataStore: PreferenceDataStore) : SettingRepository {

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
        val mentionColor = preferenceDataStore.getInt(MENTION_KEY, -1)
        val directMessageColor = preferenceDataStore.getInt(DM_KEY, -1)
        val bothNotifyColor = preferenceDataStore.getInt(BOTH_NOTIFY_KEY, -1)

        if (size == -1 || mentionColor == -1 || directMessageColor == -1 || bothNotifyColor == -1)
            return null

        return SettingData(size, mentionColor, directMessageColor, bothNotifyColor)
    }

    /**
     * 설정 저장하기
     */
    override suspend fun saveSetting(data: SettingData) {
        preferenceDataStore.putInt(SIZE_KEY, data.size)
        preferenceDataStore.putInt(MENTION_KEY, data.mentionColor)
        preferenceDataStore.putInt(DM_KEY, data.directMessageColor)
        preferenceDataStore.putInt(BOTH_NOTIFY_KEY, data.bothNotifyColor)
    }

}