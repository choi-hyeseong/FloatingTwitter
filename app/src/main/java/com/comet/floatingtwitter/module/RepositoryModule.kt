package com.comet.floatingtwitter.module

import com.comet.floatingtwitter.common.storage.LocalDataStorage
import com.comet.floatingtwitter.twitter.api.dao.TwitterDao
import com.comet.floatingtwitter.twitter.api.repository.TwitterAPIRepository
import com.comet.floatingtwitter.twitter.api.repository.TwitterRepository
import com.comet.floatingtwitter.twitter.oauth.repository.LoginRepository
import com.comet.floatingtwitter.twitter.oauth.repository.OAuthLoginRepository
import com.comet.floatingtwitter.twitter.oauth.repository.PreferenceTokenRepository
import com.comet.floatingtwitter.twitter.oauth.repository.TokenRepository
import com.comet.floatingtwitter.twitter.setting.repository.PreferenceSettingRepository
import com.comet.floatingtwitter.twitter.setting.repository.SettingRepository
import com.twitter.clientlib.auth.TwitterOAuth20Service
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class RepositoryModule {

    companion object {
        const val INTERVAL = 60 // 1분마다 반복
    }

    @Provides
    @Singleton
    fun provideTwitterAPIRepository(twitterDao: TwitterDao) : TwitterRepository {
        return TwitterAPIRepository(twitterDao, INTERVAL)
    }

    @Provides
    @Singleton
    fun provideLoginRepository(oAuth20Service: TwitterOAuth20Service) : LoginRepository {
        return OAuthLoginRepository(oAuth20Service)
    }

    @Provides
    @Singleton
    fun provideTokenRepository(localDataStorage: LocalDataStorage) : TokenRepository {
        return PreferenceTokenRepository(localDataStorage)
    }

    @Provides
    @Singleton
    fun provideSettingRepository(localDataStorage: LocalDataStorage) : SettingRepository {
        return PreferenceSettingRepository(localDataStorage)
    }

}