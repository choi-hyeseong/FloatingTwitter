package com.comet.floatingtwitter.twitter.oauth.repository

import com.github.scribejava.core.model.OAuth2AccessToken
import com.github.scribejava.core.pkce.PKCE
import com.github.scribejava.core.pkce.PKCECodeChallengeMethod
import com.twitter.clientlib.auth.TwitterOAuth20Service

/**
 * 트위터 OAuth API를 이용한 로그인
 * @property oauthClient oauth 인증에 필요한 서비스 객체
 */
class OAuthLoginRepository(private val oauthClient : TwitterOAuth20Service) : LoginRepository {

    //인증에 필요한 param init
    companion object {

        private const val SECRET_STATE = "state"
        private val PKCE = PKCE().apply {
            codeChallenge = "challenge"
            codeChallengeMethod = PKCECodeChallengeMethod.PLAIN
            codeVerifier = "challenge"
        }

    }
    override fun getLoginURL(): String {
        return oauthClient.getAuthorizationUrl(PKCE, SECRET_STATE)
    }

    override suspend fun login(code: String): Result<OAuth2AccessToken> {
        return kotlin.runCatching { oauthClient.getAccessToken(PKCE, code) }
    }


}