package com.comet.floatingtwitter.overlay.usecase

import com.comet.floatingtwitter.overlay.repository.BubbleRepository

/**
 * @See BubbleRepository.stopService
 */
class StopServiceUseCase(private val bubbleRepository: BubbleRepository) {

    suspend operator fun invoke() {
        bubbleRepository.stopService()
    }
}