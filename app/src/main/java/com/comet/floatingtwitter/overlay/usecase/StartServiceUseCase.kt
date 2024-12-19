package com.comet.floatingtwitter.overlay.usecase

import com.comet.floatingtwitter.overlay.repository.BubbleRepository

/**
 * @See BubbleRepository.startService
 */
class StartServiceUseCase(private val bubbleRepository: BubbleRepository) {

    suspend operator fun invoke() {
        bubbleRepository.startService()
    }
}