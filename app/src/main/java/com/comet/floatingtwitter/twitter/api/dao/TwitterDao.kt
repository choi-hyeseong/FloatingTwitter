package com.comet.floatingtwitter.twitter.api.dao

import com.comet.floatingtwitter.twitter.oauth.model.OAuthToken
import com.twitter.clientlib.model.Tweet
import com.twitter.clientlib.model.User
import okhttp3.ResponseBody

/**
 * 트위터 API 접근을 위한 DAO
 */
interface TwitterDao {

    /**
     * 멘션 데이터 가져오기
     * @param userId 해당되는 유저의 id입니다. 내부조회가 가능하지만, 매번 api 호출을 하므로, 최초 호출전 id 캐싱이 필요합니다. (getUserInfo)
     * @throws Exception 멘션 조회가 되지 않는경우 발생합니다.
     * @throws NullPointerException 동일한 사유로 트윗 정보가 조회되지 않으면 발생합니다. (null)
     */
    suspend fun getMentionData(userId : String, token : OAuthToken) : List<Tweet>

    /**
     * DM 데이터 가져오기
     * @throws Exception DM 조회가 되지 않는경우 발생합니다.
     * @throws NullPointerException 동일한 사유로 dm 정보가 조회되지 않으면 발생합니다. (null)
     */
    suspend fun getDMData(token: OAuthToken) : List<Any> // TODO

    /**
     * 아바타 이미지 가져오기
     */
    suspend fun getAvatarResource(user : User, token: OAuthToken) : ResponseBody

    /**
     * 유저 정보 가져오기. 아바타 이미지 가져올때 사용됨
     * @throws Exception 유저 정보 조회가 되지 않는경우 발생합니다.
     * @throws NullPointerException 동일한 사유로 유저 정보가 조회되지 않으면 발생합니다. (null)
     */
    suspend fun getUserInfo(token: OAuthToken) : User
}