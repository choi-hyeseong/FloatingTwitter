package com.comet.floatingtwitter.twitter.api.repository

import android.graphics.drawable.Drawable
import com.comet.floatingtwitter.twitter.api.model.EventData
import com.comet.floatingtwitter.twitter.oauth.model.OAuthToken
import com.twitter.clientlib.model.User

interface TwitterRepository {

    /**
     * DM 정보와 멘션 정보를 확인하는 리스너를 시작합니다.
     * @param callback 이벤트가 발생했을때 처리할 콜백입니다.
     */
    suspend fun startAPIListening(user: User, token: OAuthToken, callback : (List<EventData>) -> Unit)

    /**
     * 리스너를 중단합니다.
     */
    suspend fun stopAPIListening()


    /**
     * 리스너가 동작하는지 확인합니다.
     */
    suspend fun isRunning() : Boolean

    /**
     * 짹쨱이의 유저 이미지를 가져옵니다.
     */
    suspend fun getAvatarResource(user : User, token: OAuthToken) : Result<Drawable>

    /**
     * 짹짹이의 유저 정보를 가져옵니다.
     * @see TwitterAPIDao.getUserInfo
     */
    suspend fun getUserInfo(token : OAuthToken) : Result<User>
}