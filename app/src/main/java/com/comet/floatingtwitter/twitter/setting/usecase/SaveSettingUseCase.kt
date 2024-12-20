package com.comet.floatingtwitter.twitter.setting.usecase

import com.comet.floatingtwitter.twitter.setting.model.SettingData
import com.comet.floatingtwitter.twitter.setting.repository.SettingRepository

class SaveSettingUseCase(private val settingRepository: SettingRepository) {

    /**
     * @see SettingRepository.saveSetting
     */
    suspend operator fun invoke(setting: SettingData) {
        settingRepository.saveSetting(setting)
    }
}