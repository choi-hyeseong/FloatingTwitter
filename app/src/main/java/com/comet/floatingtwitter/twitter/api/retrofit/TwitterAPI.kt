package com.comet.floatingtwitter.twitter.api.retrofit

import okhttp3.ResponseBody
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.Url

interface TwitterAPI {

    @GET
    suspend fun getAvatarImage(@Url imageUrl : String) : ResponseBody

    @POST
    suspend fun getDMData(@Header("authorization") authorization : String) : Any // TODO
}