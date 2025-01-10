package com.comet.floatingtwitter.twitter.setting.view

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.comet.floatingtwitter.common.event.Event
import com.comet.floatingtwitter.twitter.oauth.usecase.LoadTokenUseCase
import com.comet.floatingtwitter.twitter.setting.model.SettingData
import com.comet.floatingtwitter.twitter.setting.usecase.LoadSettingUseCase
import com.comet.floatingtwitter.twitter.setting.usecase.SaveSettingUseCase
import com.comet.floatingtwitter.twitter.setting.view.validator.ColorValidator
import com.comet.floatingtwitter.twitter.setting.view.validator.ValidateResult
import com.comet.floatingtwitter.twitter.setting.view.validator.ValidateStatus
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import javax.inject.Inject

/**
 * 설정 정보를 갖고 있는 뷰모델
 */
@HiltViewModel
class SettingViewModel @Inject constructor(private val loadTokenUseCase: LoadTokenUseCase, private val loadSettingUseCase: LoadSettingUseCase, private val saveSettingUseCase: SaveSettingUseCase, private val colorValidator: ColorValidator) : ViewModel() {

    // lazy로 지정되어 바로 로드가 아닌 참조시 로드
    // 토큰값이 '설정'되어 있는지만 확인하는 livedata
    val isTokenSetLiveData: MutableLiveData<Boolean> by lazy {
        MutableLiveData<Boolean>().apply {
            value = loadTokenIsValid()
        }
    }

    // 설정값 가져오는 livedata
    val settingDataLiveData: MutableLiveData<SettingData> by lazy {
        MutableLiveData<SettingData>().apply {
            val setting = loadSetting() ?: return@apply
            value = setting
        }
    }

    // for toast - 저장 성공여부만 1회성으로 나타냄
    val responseLiveData: MutableLiveData<Event<ValidateStatus>> = MutableLiveData()


    private fun loadTokenIsValid(): Boolean = runBlocking {
        // 토큰 저장되었는지 확인
        withContext(Dispatchers.IO) {
            loadTokenUseCase() != null
        }
    }

    private fun loadSetting(): SettingData? = runBlocking {
        withContext(Dispatchers.IO) {
            loadSettingUseCase()
        }
    }

    fun saveSetting(iconSize: Int, mentionColor: String, dmColor: String, bothColor: String) {
        val result: MutableList<String> = mutableListOf()
        if (!colorValidator.validate(mentionColor)) result.add("mention_input")
        if (!colorValidator.validate(dmColor)) result.add("dm_input")
        if (!colorValidator.validate(bothColor)) result.add("both_input")

        if (result.isEmpty()) {
            CoroutineScope(Dispatchers.IO).launch {
                val data = SettingData(iconSize, mentionColor, dmColor, bothColor)
                saveSettingUseCase(data)
                responseLiveData.postValue(Event(ValidateStatus(ValidateResult.OK, result)))
            }
        }
        else responseLiveData.postValue(Event(ValidateStatus(ValidateResult.ERROR, result)))
    }
}