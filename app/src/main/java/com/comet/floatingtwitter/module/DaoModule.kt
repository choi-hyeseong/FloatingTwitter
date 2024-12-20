package com.comet.floatingtwitter.module

import com.comet.floatingtwitter.BuildConfig
import com.comet.floatingtwitter.twitter.api.dao.TwitterAPIDao
import com.comet.floatingtwitter.twitter.api.dao.TwitterDao
import com.comet.floatingtwitter.twitter.api.retrofit.TwitterAPI
import com.twitter.clientlib.auth.TwitterOAuth20Service
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class DaoModule {

    companion object {
        const val CALLBACK_URL = "ftweet://auth"
        const val DEFAULT_SCOPE = "offline.access tweet.read users.read dm.read"
    }

    @Provides
    @Singleton
    fun provideTwitterDao(twitterApi: TwitterAPI): TwitterDao {
        return TwitterAPIDao(twitterApi)
    }

    @Provides
    @Singleton
    fun provideTwitterOAuthService(): TwitterOAuth20Service {
        // api key secret이라 해놓고 막상 보면 client key에 secret으로 해놨음
        return TwitterOAuth20Service(BuildConfig.CLIENT_KEY, BuildConfig.CLIENT_SECRET, CALLBACK_URL, DEFAULT_SCOPE)
    }
}