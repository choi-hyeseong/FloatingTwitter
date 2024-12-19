package com.comet.floatingtwitter.overlay.usecase

import com.comet.floatingtwitter.overlay.repository.BubbleRepository

/**
 * @See BubbleRepository.increaseCounter
 */
class IncreaseCounterUseCase(private val bubbleRepository: BubbleRepository) {

    suspend operator fun invoke(amount : Int) {
        bubbleRepository.increaseCounter(amount)
    }
}