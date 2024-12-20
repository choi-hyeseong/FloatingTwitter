package com.comet.floatingtwitter.twitter.api.dao

import com.comet.floatingtwitter.twitter.api.dto.DirectMessageDTO
import com.comet.floatingtwitter.twitter.oauth.model.OAuthToken
import com.skydoves.sandwich.ApiResponse
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
     * @return 트윗 값이 없는경우 Failure를 반환합니다.
     */
    suspend fun getMentionData(userId : String, token : OAuthToken) : Result<List<Tweet>>

    /**
     * DM 데이터 가져오기
     */
    suspend fun getDMData(token: OAuthToken) : ApiResponse<DirectMessageDTO>

    /**
     * 아바타 이미지 가져오기
     */
    suspend fun getAvatarResource(user : User, token: OAuthToken) : ApiResponse<ResponseBody>

    /**
     * 유저 정보 가져오기. 아바타 이미지 가져올때 사용됨
     */
    suspend fun getUserInfo(token: OAuthToken) : Result<User>
}