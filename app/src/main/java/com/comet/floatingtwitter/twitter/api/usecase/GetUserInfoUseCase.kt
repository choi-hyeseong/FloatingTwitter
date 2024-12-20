package com.comet.floatingtwitter.twitter.api.usecase

import com.comet.floatingtwitter.twitter.api.repository.TwitterRepository
import com.comet.floatingtwitter.twitter.oauth.model.OAuthToken
import com.twitter.clientlib.model.User

/**
 * @see TwitterRepository.getUserInfo
 */
class GetUserInfoUseCase(private val twitterRepository: TwitterRepository) {

    suspend operator fun invoke(token: OAuthToken) : Result<User> {
        return twitterRepository.getUserInfo(token)
    }
}