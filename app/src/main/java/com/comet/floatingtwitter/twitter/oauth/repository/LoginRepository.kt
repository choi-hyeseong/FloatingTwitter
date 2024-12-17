package com.comet.floatingtwitter.twitter.oauth.repository

import com.github.scribejava.core.model.OAuth2AccessToken


/**
 * 로그인 관련 레포지토리
 */
interface LoginRepository {

    /**
     * 로그인에 사용될 webview의 url 입니다.
     */
    fun getLoginURL() : String

    /**
     * 리다이렉트된 code를 이용하여 액세스 토큰을 발급받습니다.
     */
    suspend fun login(code : String) : OAuth2AccessToken
}