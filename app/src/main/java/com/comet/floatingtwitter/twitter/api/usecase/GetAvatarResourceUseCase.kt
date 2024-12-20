package com.comet.floatingtwitter.twitter.api.usecase

import android.graphics.drawable.Drawable
import com.comet.floatingtwitter.twitter.api.repository.TwitterRepository
import com.comet.floatingtwitter.twitter.oauth.model.OAuthToken
import com.twitter.clientlib.model.User

/**
 * @see TwitterRepository.getAvatarResource
 */
class GetAvatarResourceUseCase(private val twitterRepository: TwitterRepository) {

    suspend operator fun invoke(user: User, token: OAuthToken): Result<Drawable> {
        return twitterRepository.getAvatarResource(user, token)
    }
}