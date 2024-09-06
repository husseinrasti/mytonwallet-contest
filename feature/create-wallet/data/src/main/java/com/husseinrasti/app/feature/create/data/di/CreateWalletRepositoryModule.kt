package com.husseinrasti.app.feature.create.data.di

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import dagger.hilt.android.scopes.ViewModelScoped
import com.husseinrasti.app.feature.create.data.repository.CreateWalletRepositoryImpl
import com.husseinrasti.app.feature.create.domain.repository.CreateWalletRepository

@Module
@InstallIn(ViewModelComponent::class)
interface CreateWalletRepositoryModule {

    @Binds
    @ViewModelScoped
    fun provideRepository(
        createWalletRepositoryImpl: CreateWalletRepositoryImpl
    ): CreateWalletRepository

}