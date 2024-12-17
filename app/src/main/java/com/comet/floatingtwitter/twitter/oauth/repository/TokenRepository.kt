package com.comet.floatingtwitter.twitter.oauth.repository

import com.comet.floatingtwitter.twitter.oauth.model.OAuthToken

/**
 * 로컬 토큰 저장소
 */
interface TokenRepository {

    /**
     * 토큰 불러오기
     *
     * @return 저장되어 있지 않은경우 null
     */
    suspend fun loadToken() : OAuthToken?


    // 토큰 저장
    suspend fun saveToken(token : OAuthToken)
}