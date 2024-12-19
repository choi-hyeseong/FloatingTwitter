package com.comet.floatingtwitter.overlay.usecase

import android.graphics.drawable.Drawable
import com.comet.floatingtwitter.overlay.repository.BubbleRepository

/**
 * @See BubbleRepository.changeIcon
 */
class ChangeIconUseCase(private val bubbleRepository: BubbleRepository) {

    suspend operator fun invoke(drawable: Drawable) {
        bubbleRepository.changeIcon(drawable)
    }
}