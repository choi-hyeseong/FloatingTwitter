package com.comet.floatingtwitter.twitter.api.usecase

import com.comet.floatingtwitter.twitter.api.repository.TwitterRepository

/**
 * @see TwitterRepository.stopAPIListening
 */
class StopAPIListeningUseCase(private val twitterRepository: TwitterRepository) {

    suspend operator fun invoke() {
        twitterRepository.stopAPIListening()
    }
}