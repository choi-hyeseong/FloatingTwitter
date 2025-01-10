package com.comet.floatingtwitter.twitter.oauth.view

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.comet.floatingtwitter.twitter.oauth.usecase.GetLoginURLUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import javax.inject.Inject

// 로그인에 필요한 url 제공하는 뷰모델
@HiltViewModel
class OAuthFragmentViewModel @Inject constructor(private val getLoginURLUseCase: GetLoginURLUseCase) : ViewModel() {

    val urlLiveData: MutableLiveData<String> by lazy { MutableLiveData<String>().apply { value = loadURL() } }

    private fun loadURL() : String = runBlocking {
        withContext(Dispatchers.IO) {
            getLoginURLUseCase()
        }
    }
}