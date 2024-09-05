package com.husseinrasti.app.feature.auth.data.di

import com.husseinrasti.app.feature.auth.data.repository.BiometricRepositoryImpl
import com.husseinrasti.app.feature.auth.domain.repository.BiometricRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import dagger.hilt.android.scopes.ViewModelScoped

@Module
@InstallIn(ViewModelComponent::class)
interface RepositoryModule {
//    @Binds
//    @ViewModelScoped
//    fun bindBiometricRepository(
//        biometricRepositoryImpl: BiometricRepositoryImpl
//    ): BiometricRepository

}