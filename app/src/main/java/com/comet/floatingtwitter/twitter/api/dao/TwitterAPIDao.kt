package com.comet.floatingtwitter.twitter.api.dao

import com.comet.floatingtwitter.BuildConfig
import com.comet.floatingtwitter.twitter.api.dto.DirectMessageDTO
import com.comet.floatingtwitter.twitter.api.retrofit.TwitterAPI
import com.comet.floatingtwitter.twitter.oauth.model.OAuthToken
import com.skydoves.sandwich.ApiResponse
import com.twitter.clientlib.TwitterCredentialsOAuth2
import com.twitter.clientlib.api.TwitterApi
import com.twitter.clientlib.model.Tweet
import com.twitter.clientlib.model.User
import okhttp3.ResponseBody

class TwitterAPIDao(private val twitterAPI: TwitterAPI) : TwitterDao {

    companion object {
        const val DM_URL: String = "https://api.twitter.com/2/dm_events?dm_event.fields=id,text,event_type,dm_conversation_id,created_at,sender_id,attachments,participant_ids,referenced_tweets&event_types=MessageCreate&user.fields=created_at,description,id,location,name,pinned_tweet_id,public_metrics,url,username&expansions=sender_id,referenced_tweets.id,attachments.media_keys,participant_ids/"

    }

    override suspend fun getMentionData(id: String, token: OAuthToken): Result<List<Tweet>> {
        val tweeterAPI = TwitterApi(TwitterCredentialsOAuth2(
            BuildConfig.CLIENT_KEY, BuildConfig.CLIENT_SECRET, token.accessToken, token.refreshToken).apply { isOAUth2AutoRefreshToken = true })
        return kotlin.runCatching { tweeterAPI.tweets().usersIdMentions(id).execute().data!! }
    }

    override suspend fun getUserInfo(token: OAuthToken): Result<User> {
        val tweeterAPI = TwitterApi(TwitterCredentialsOAuth2(
            BuildConfig.CLIENT_KEY, BuildConfig.CLIENT_SECRET, token.accessToken, token.refreshToken).apply { isOAUth2AutoRefreshToken = true })
        return kotlin.runCatching {
            tweeterAPI.users()
                .findMyUser()
                .userFields(setOf("profile_image_url"))
                .execute().data!!
        }


    }

    override suspend fun getDMData(token: OAuthToken): ApiResponse<DirectMessageDTO> {
        return twitterAPI.getDMData(DM_URL, "Bearer ${token.accessToken}")
    }

    override suspend fun getAvatarResource(user: User, token: OAuthToken): ApiResponse<ResponseBody> {
        val url = user.profileImageUrl.toString().split("_normal")[0].plus("_400x400.jpg")
        return twitterAPI.getAvatarImage(url)
    }

}