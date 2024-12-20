package com.comet.floatingtwitter.twitter.oauth.usecase

import com.comet.floatingtwitter.twitter.oauth.repository.LoginRepository

class GetLoginURLUseCase(private val loginRepository: LoginRepository) {

    /**
     * @see LoginRepository.getLoginURL
     */
    operator fun invoke(): String {
        return loginRepository.getLoginURL()
    }
}