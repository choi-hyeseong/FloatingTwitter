package com.comet.floatingtwitter.twitter.oauth.view

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.comet.floatingtwitter.common.event.Event
import com.comet.floatingtwitter.getClassName
import com.comet.floatingtwitter.twitter.oauth.model.OAuthToken
import com.comet.floatingtwitter.twitter.oauth.usecase.LoginUseCase
import com.comet.floatingtwitter.twitter.oauth.usecase.SaveTokenUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * 로그인 및 토큰 저장 유스케이스
 */
@HiltViewModel
class OAuthViewModel @Inject constructor(private val loginUseCase: LoginUseCase, private val saveTokenUseCase: SaveTokenUseCase) : ViewModel() {


    // 결과 저장용
    val responseLiveData : MutableLiveData<Event<Boolean>> = MutableLiveData()

    fun login(code : String) {
        CoroutineScope(Dispatchers.IO).launch {
            val response = loginUseCase(code)
            if (!response.isSuccess)
                responseLiveData.postValue(Event(false))
            else {
                saveTokenUseCase(OAuthToken.fromDTO(response.getOrThrow()))
                responseLiveData.postValue(Event(true))
            }
        }
    }
}