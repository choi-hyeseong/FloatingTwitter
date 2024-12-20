package com.comet.floatingtwitter.twitter.oauth.model

import com.github.scribejava.core.model.OAuth2AccessToken

/**
 * Twitter API 접근하기 위한 Token class
 * @property accessToken 액세스용 토큰
 * @property refreshToken 재발급용 토큰
 */
data class OAuthToken(val accessToken: String, val refreshToken: String) {
    companion object {
        // DTO를 OAuth Model로 변경. 헤더에서 써야 하므로 Bearer 붙임
        // bearer를 여기서 붙이니까 일반 access token 사용시에 문제 생김. retrofit 요청할때만 넣기
        fun fromDTO(oAuth2AccessToken: OAuth2AccessToken) = OAuthToken(oAuth2AccessToken.accessToken, oAuth2AccessToken.refreshToken)
    }
}