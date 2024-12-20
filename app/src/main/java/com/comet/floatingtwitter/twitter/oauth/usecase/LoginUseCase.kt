package com.comet.floatingtwitter.twitter.oauth.usecase

import com.comet.floatingtwitter.twitter.oauth.repository.LoginRepository
import com.github.scribejava.core.model.OAuth2AccessToken

class LoginUseCase(private val loginRepository: LoginRepository) {

    /**
     * @see LoginRepository.login
     */
    suspend operator fun invoke(code: String): Result<OAuth2AccessToken> {
        return loginRepository.login(code)
    }
}