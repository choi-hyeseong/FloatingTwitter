package com.comet.floatingtwitter.twitter.api.dao

import com.comet.floatingtwitter.twitter.api.retrofit.TwitterAPI
import com.comet.floatingtwitter.twitter.oauth.model.OAuthToken
import com.twitter.clientlib.TwitterCredentialsOAuth2
import com.twitter.clientlib.api.TweetsApi
import com.twitter.clientlib.api.TwitterApi
import com.twitter.clientlib.model.Tweet
import com.twitter.clientlib.model.User
import okhttp3.ResponseBody
import java.net.URL

class TwitterAPIDao(private val twitterAPI: TwitterAPI) : TwitterDao {
    override suspend fun getMentionData(id : String, token: OAuthToken): List<Tweet> {
        val tweeterAPI = TwitterApi(TwitterCredentialsOAuth2(
            "BuildConfig.CLIENT_ID",
            "BuildConfig.CLIENT_SECRET",
            token.accessToken,
            token.refreshToken
        ).apply { isOAUth2AutoRefreshToken = true })
        return kotlin.run { tweeterAPI.tweets().usersIdMentions(id).execute().data!! }
    }

    override suspend fun getUserInfo(token: OAuthToken) : User {
        val tweeterAPI = TwitterApi(TwitterCredentialsOAuth2(
            "BuildConfig.CLIENT_ID",
            "BuildConfig.CLIENT_SECRET",
            token.accessToken,
            token.refreshToken
        ).apply { isOAUth2AutoRefreshToken = true })
        return kotlin.run { tweeterAPI.users().findMyUser().userFields(setOf("profile_image_url")).execute().data!! }


    }

    override suspend fun getDMData(token: OAuthToken): List<Any> {
        TODO("Not yet implemented")
    }

    override suspend fun getAvatarResource(user: User, token: OAuthToken): ResponseBody {
        val url = user.profileImageUrl.toString().split("_normal")[0].plus("_400x400.jpg")
        return twitterAPI.getAvatarImage(url)
    }

}