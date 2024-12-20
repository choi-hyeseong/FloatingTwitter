package com.comet.floatingtwitter.module

import com.comet.floatingtwitter.twitter.api.repository.TwitterRepository
import com.comet.floatingtwitter.twitter.api.usecase.GetAvatarResourceUseCase
import com.comet.floatingtwitter.twitter.api.usecase.GetUserInfoUseCase
import com.comet.floatingtwitter.twitter.api.usecase.IsServiceRunningUseCase
import com.comet.floatingtwitter.twitter.api.usecase.StartAPIListeningUseCase
import com.comet.floatingtwitter.twitter.api.usecase.StopAPIListeningUseCase
import com.comet.floatingtwitter.twitter.oauth.repository.LoginRepository
import com.comet.floatingtwitter.twitter.oauth.repository.TokenRepository
import com.comet.floatingtwitter.twitter.oauth.usecase.GetLoginURLUseCase
import com.comet.floatingtwitter.twitter.oauth.usecase.LoadTokenUseCase
import com.comet.floatingtwitter.twitter.oauth.usecase.LoginUseCase
import com.comet.floatingtwitter.twitter.oauth.usecase.SaveTokenUseCase
import com.comet.floatingtwitter.twitter.setting.repository.SettingRepository
import com.comet.floatingtwitter.twitter.setting.usecase.LoadSettingUseCase
import com.comet.floatingtwitter.twitter.setting.usecase.SaveSettingUseCase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class UseCaseModule {

    @Provides
    @Singleton
    fun provideGetAvatarResourceUseCase(twitterRepository: TwitterRepository): GetAvatarResourceUseCase {
        return GetAvatarResourceUseCase(twitterRepository)
    }

    @Provides
    @Singleton
    fun provideGetUserInfoUseCase(twitterRepository: TwitterRepository): GetUserInfoUseCase {
        return GetUserInfoUseCase(twitterRepository)
    }

    @Provides
    @Singleton
    fun provideIsServiceRunningUseCase(twitterRepository: TwitterRepository): IsServiceRunningUseCase {
        return IsServiceRunningUseCase(twitterRepository)
    }

    @Provides
    @Singleton
    fun provideStartAPIListeningUseCase(twitterRepository: TwitterRepository): StartAPIListeningUseCase {
        return StartAPIListeningUseCase(twitterRepository)
    }

    @Provides
    @Singleton
    fun provideStopAPIListeningUseCase(twitterRepository: TwitterRepository): StopAPIListeningUseCase {
        return StopAPIListeningUseCase(twitterRepository)
    }

    @Provides
    @Singleton
    fun provideGetLoginURLUseCase(loginRepository: LoginRepository): GetLoginURLUseCase {
        return GetLoginURLUseCase(loginRepository)
    }

    @Provides
    @Singleton
    fun provideLoadTokenUseCase(tokenRepository: TokenRepository): LoadTokenUseCase {
        return LoadTokenUseCase(tokenRepository)
    }

    @Provides
    @Singleton
    fun provideSaveTokenUseCase(tokenRepository: TokenRepository): SaveTokenUseCase {
        return SaveTokenUseCase(tokenRepository)
    }

    @Provides
    @Singleton
    fun provideLoginUseCase(loginRepository: LoginRepository): LoginUseCase {
        return LoginUseCase(loginRepository)
    }

    @Provides
    @Singleton
    fun provideLoadSettingUseCase(settingRepository: SettingRepository): LoadSettingUseCase {
        return LoadSettingUseCase(settingRepository)
    }

    @Provides
    @Singleton
    fun provideSaveSettingUseCase(settingRepository: SettingRepository): SaveSettingUseCase {
        return SaveSettingUseCase(settingRepository)
    }
}