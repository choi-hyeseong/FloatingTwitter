package com.comet.floatingtwitter.twitter.api.usecase

import com.comet.floatingtwitter.twitter.api.model.EventData
import com.comet.floatingtwitter.twitter.api.repository.TwitterRepository
import com.comet.floatingtwitter.twitter.oauth.model.OAuthToken
import com.twitter.clientlib.model.User

/**
 * @see TwitterRepository.startAPIListening
 */
class StartAPIListeningUseCase(private val twitterRepository: TwitterRepository) {

    suspend operator fun invoke(user: User, token: OAuthToken, callback: (List<EventData>) -> Unit) {
        twitterRepository.startAPIListening(user, token, callback)
    }
}