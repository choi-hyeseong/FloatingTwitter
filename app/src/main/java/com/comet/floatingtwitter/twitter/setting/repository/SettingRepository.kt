package com.comet.floatingtwitter.twitter.setting.repository

import com.comet.floatingtwitter.twitter.setting.model.SettingData

interface SettingRepository {

    /**
     * 설정을 불러옵니다.
     * @return 값이 저장되어 있지 않은경우 null
     */
    suspend fun loadSetting() : SettingData?

    /**
     * 설정 정보를 저장합니다.
     */
    suspend fun saveSetting(data : SettingData)

}