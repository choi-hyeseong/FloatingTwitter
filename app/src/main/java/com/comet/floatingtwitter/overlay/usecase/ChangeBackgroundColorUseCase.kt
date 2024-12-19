package com.comet.floatingtwitter.overlay.usecase

import com.comet.floatingtwitter.overlay.repository.BubbleRepository

/**
 * @See BubbleRepository.changeBackgroundColor
 */
class ChangeBackgroundColorUseCase(private val bubbleRepository: BubbleRepository) {

    suspend operator fun invoke(color : Int) {
        bubbleRepository.changeBackgroundColor(color)
    }
}