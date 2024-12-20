package com.comet.floatingtwitter.twitter.setting.usecase

import com.comet.floatingtwitter.twitter.setting.model.SettingData
import com.comet.floatingtwitter.twitter.setting.repository.SettingRepository

class LoadSettingUseCase(private val settingRepository: SettingRepository) {

    /**
     * @see SettingRepository.loadSetting
     */
    suspend operator fun invoke(): SettingData? {
        return settingRepository.loadSetting()
    }
}