package com.comet.floatingtwitter

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.comet.floatingtwitter.common.event.Event
import com.comet.floatingtwitter.overlay.type.ServiceRequirement
import com.comet.floatingtwitter.twitter.oauth.usecase.LoadTokenUseCase
import com.comet.floatingtwitter.twitter.setting.usecase.LoadSettingUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * 서비스 시작 및 중지 용도
 */
@HiltViewModel
class MainViewModel @Inject constructor(private val loadSettingUseCase: LoadSettingUseCase, private val loadTokenUseCase: LoadTokenUseCase) : ViewModel() {

    // 서비스 시작요청시 요구사항 확인
    val serviceRequirementLiveData : MutableLiveData<Event<ServiceRequirement>> = MutableLiveData()

    fun requestStartService() {
        CoroutineScope(Dispatchers.IO).launch {
            val isSettingValid = loadSettingUseCase() != null
            val isTokenValid = loadTokenUseCase() != null

            val result = when {
                !isSettingValid && !isTokenValid -> ServiceRequirement.BOTH
                !isSettingValid -> ServiceRequirement.SETTING
                !isTokenValid -> ServiceRequirement.TOKEN
                else -> ServiceRequirement.OK
            }
            serviceRequirementLiveData.postValue(Event((result)))
        }
    }


}