package com.comet.floatingtwitter.twitter.oauth.usecase

import com.comet.floatingtwitter.twitter.oauth.model.OAuthToken
import com.comet.floatingtwitter.twitter.oauth.repository.TokenRepository

class LoadTokenUseCase(private val tokenRepository: TokenRepository) {

    /**
     * @see TokenRepository.loadToken
     */
    suspend operator fun invoke() : OAuthToken? {
        return tokenRepository.loadToken()
    }
}