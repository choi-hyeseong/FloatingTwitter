package com.comet.floatingtwitter.twitter.api.usecase

import com.comet.floatingtwitter.twitter.api.repository.TwitterRepository

/**
 * @see TwitterRepository.isRunning
 */
class IsServiceRunningUseCase(private val twitterRepository: TwitterRepository) {

    suspend operator fun invoke() : Boolean {
        return twitterRepository.isRunning()
    }
}