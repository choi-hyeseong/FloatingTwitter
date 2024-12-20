package com.comet.floatingtwitter.module

import android.content.Context
import com.comet.floatingtwitter.common.storage.LocalDataStorage
import com.comet.floatingtwitter.common.storage.PreferenceDataStore
import com.comet.floatingtwitter.twitter.setting.view.validator.ColorValidator
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

// Preferences Datastore
@Module
@InstallIn(SingletonComponent::class) //전역적, 단일
class CommonModule {

    @Provides
    @Singleton
    fun providePreferenceDataStore(@ApplicationContext context: Context): LocalDataStorage {
        return PreferenceDataStore(context)
    }

    @Provides
    @Singleton
    fun provideColorValidator(): ColorValidator {
        return ColorValidator()
    }
}