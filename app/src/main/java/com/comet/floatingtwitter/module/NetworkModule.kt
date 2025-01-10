package com.comet.floatingtwitter.module

import com.comet.floatingtwitter.twitter.api.retrofit.TwitterAPI
import com.google.gson.GsonBuilder
import com.skydoves.sandwich.retrofit.adapters.ApiResponseCallAdapterFactory
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class NetworkModule {

    @Provides
    @Singleton
    fun provideRetrofit(okHttpClient: OkHttpClient): Retrofit {
        val gson = GsonBuilder().setLenient().create();
        return Retrofit.Builder()
            .baseUrl("http://twitter.com/")
            .client(okHttpClient)
            .addCallAdapterFactory(ApiResponseCallAdapterFactory.create())
            .addConverterFactory(
                GsonConverterFactory.create(gson))
            .build()
    }

    @Provides
    @Singleton
    fun provideOkHttpClient(): OkHttpClient {
        //header값 지정해주는 okhttp client (oauth 사용을 위해 bearer 헤더 추가)
        return OkHttpClient.Builder()
            .connectTimeout(60, TimeUnit.SECONDS)
            .addInterceptor(NetworkInterceptor())
            .build()
    }

    @Provides
    @Singleton
    fun provideTwitterAPIService(retrofit: Retrofit): TwitterAPI {
        return retrofit.create(TwitterAPI::class.java)
    }

    // 헤더 추가용 interceptor
    private inner class NetworkInterceptor : Interceptor {
        override fun intercept(chain: Interceptor.Chain): Response {
            val request = chain.request()
                .newBuilder()
                .addHeader("Accept", "/*/")
                .addHeader("Connection", "keep-alive")
                .build()
            return chain.proceed(request)
        }
    }

}
