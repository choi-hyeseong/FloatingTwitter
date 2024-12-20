package com.comet.floatingtwitter.twitter.api.retrofit

import com.comet.floatingtwitter.twitter.api.dto.DirectMessageDTO
import com.skydoves.sandwich.ApiResponse
import okhttp3.ResponseBody
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Url

interface TwitterAPI {

    @GET
    suspend fun getAvatarImage(@Url imageUrl: String): ApiResponse<ResponseBody>

    @GET
    suspend fun getDMData(@Url directMessageUrl: String, @Header("authorization") authorization: String): ApiResponse<DirectMessageDTO>
}