package com.comet.floatingtwitter.twitter.oauth.usecase

import com.comet.floatingtwitter.twitter.oauth.model.OAuthToken
import com.comet.floatingtwitter.twitter.oauth.repository.TokenRepository

class SaveTokenUseCase(private val tokenRepository: TokenRepository) {

    /**
     * @see TokenRepository.saveToken
     */
    suspend operator fun invoke(token : OAuthToken) {
        tokenRepository.saveToken(token)
    }
}