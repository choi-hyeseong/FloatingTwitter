package com.comet.floatingtwitter.twitter.oauth.model

/**
 * Twitter API 접근하기 위한 Token class
 * @property accessToken 액세스용 토큰
 * @property refreshToken 재발급용 토큰
 */
data class OAuthToken(val accessToken : String, val refreshToken : String)