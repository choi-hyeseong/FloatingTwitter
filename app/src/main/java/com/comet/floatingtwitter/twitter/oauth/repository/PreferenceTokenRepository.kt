package com.comet.floatingtwitter.twitter.oauth.repository

import com.comet.floatingtwitter.common.storage.LocalDataStorage
import com.comet.floatingtwitter.twitter.oauth.model.OAuthToken

class PreferenceTokenRepository(private val localDataStorage: LocalDataStorage) : TokenRepository {

    companion object {
        private const val ACCESS_TOKEN = "ACCESS_TOKEN"
        private const val REFRESH_TOKEN = "REFRESH_TOKEN"
    }

    override suspend fun loadToken(): OAuthToken? {
        val accessToken = localDataStorage.getString(ACCESS_TOKEN, "")
        val refreshToken = localDataStorage.getString(REFRESH_TOKEN, "")
        if (accessToken.isEmpty() || refreshToken.isEmpty()) return null

        return OAuthToken(accessToken, refreshToken)
    }

    override suspend fun saveToken(token: OAuthToken) {
        localDataStorage.putString(ACCESS_TOKEN, token.accessToken)
        localDataStorage.putString(REFRESH_TOKEN, token.refreshToken)
    }


}