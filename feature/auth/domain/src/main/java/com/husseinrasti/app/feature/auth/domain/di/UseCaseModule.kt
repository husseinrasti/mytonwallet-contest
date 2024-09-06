package com.husseinrasti.app.feature.auth.domain.di

import com.husseinrasti.app.feature.auth.domain.usecase.CheckAuthenticationUseCase
import com.husseinrasti.app.feature.auth.domain.usecase.CheckAuthenticationUseCaseImpl
import com.husseinrasti.app.feature.auth.domain.usecase.GetAuthenticationUseCase
import com.husseinrasti.app.feature.auth.domain.usecase.GetAuthenticationUseCaseImpl
import com.husseinrasti.app.feature.auth.domain.usecase.GetNumPasscodeDigitsUseCase
import com.husseinrasti.app.feature.auth.domain.usecase.GetNumPasscodeDigitsUseCaseImpl
import com.husseinrasti.app.feature.auth.domain.usecase.SaveBiometricUseCase
import com.husseinrasti.app.feature.auth.domain.usecase.SaveBiometricUseCaseImpl
import com.husseinrasti.app.feature.auth.domain.usecase.SavePasscodeUseCase
import com.husseinrasti.app.feature.auth.domain.usecase.SavePasscodeUseCaseImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import dagger.hilt.android.scopes.ViewModelScoped

@Module
@InstallIn(ViewModelComponent::class)
interface UseCaseModule {

    @Binds
    @ViewModelScoped
    fun bindGetAuthenticationUseCase(
        getAuthenticationUseCaseImpl: GetAuthenticationUseCaseImpl
    ): GetAuthenticationUseCase

    @Binds
    @ViewModelScoped
    fun bindSaveBiometricUseCase(
        saveBiometricUseCaseImpl: SaveBiometricUseCaseImpl
    ): SaveBiometricUseCase

    @Binds
    @ViewModelScoped
    fun bindSavePasscodeUseCase(
        savePasscodeUseCaseImpl: SavePasscodeUseCaseImpl
    ): SavePasscodeUseCase

    @Binds
    @ViewModelScoped
    fun bindCheckAuthenticationUseCase(
        checkAuthenticationUseCaseImpl: CheckAuthenticationUseCaseImpl
    ): CheckAuthenticationUseCase

    @Binds
    @ViewModelScoped
    fun bindGetNumPasscodeDigitsUseCase(
        getNumPasscodeDigitsUseCaseImpl: GetNumPasscodeDigitsUseCaseImpl
    ): GetNumPasscodeDigitsUseCase

}