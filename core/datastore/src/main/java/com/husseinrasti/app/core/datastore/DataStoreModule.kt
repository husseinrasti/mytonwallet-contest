package com.husseinrasti.app.core.datastore

import android.content.Context
import android.content.SharedPreferences
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKeys
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton


private const val SHARED_PREF_NAME = "TonWalletAndroid"

@Module
@InstallIn(SingletonComponent::class)
object DataStoreModule {

    @Singleton
    @Provides
    fun providePreferences(
        @ApplicationContext appContext: Context,
    ): SharedPreferences = EncryptedSharedPreferences.create(
        SHARED_PREF_NAME,
        MasterKeys.getOrCreate(MasterKeys.AES256_GCM_SPEC),
        appContext,
        EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
        EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
    )


}