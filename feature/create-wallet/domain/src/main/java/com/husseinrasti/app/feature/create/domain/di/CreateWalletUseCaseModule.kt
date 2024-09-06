package com.husseinrasti.app.feature.create.domain.di

import com.husseinrasti.app.feature.create.domain.usecase.CheckRecoveryPhrasesUseCase
import com.husseinrasti.app.feature.create.domain.usecase.CheckRecoveryPhrasesUseCaseImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import dagger.hilt.android.scopes.ViewModelScoped
import com.husseinrasti.app.feature.create.domain.usecase.FilterPhrasesUseCase
import com.husseinrasti.app.feature.create.domain.usecase.FilterPhrasesUseCaseImpl
import com.husseinrasti.app.feature.create.domain.usecase.GeneratePhrasesUseCase
import com.husseinrasti.app.feature.create.domain.usecase.GeneratePhrasesUseCaseImpl
import com.husseinrasti.app.feature.create.domain.usecase.GetPhrasesUseCase
import com.husseinrasti.app.feature.create.domain.usecase.GetPhrasesUseCaseImpl
import com.husseinrasti.app.feature.create.domain.usecase.MatchPhrasesUseCase
import com.husseinrasti.app.feature.create.domain.usecase.MatchPhrasesUseCaseImpl

@Module
@InstallIn(ViewModelComponent::class)
interface CreateWalletUseCaseModule {

    @Binds
    @ViewModelScoped
    fun provideFilterPhrasesUseCase(
        filterPhrasesUseCaseImpl: FilterPhrasesUseCaseImpl
    ): FilterPhrasesUseCase

    @Binds
    @ViewModelScoped
    fun provideGeneratePhrasesUseCase(
        generatePhrasesUseCaseImpl: GeneratePhrasesUseCaseImpl
    ): GeneratePhrasesUseCase

    @Binds
    @ViewModelScoped
    fun provideGetPhrasesUseCase(
        getPhrasesUseCaseImpl: GetPhrasesUseCaseImpl
    ): GetPhrasesUseCase

    @Binds
    @ViewModelScoped
    fun provideCheckRecoveryPhrasesUseCase(
        checkRecoveryPhrasesUseCaseImpl: CheckRecoveryPhrasesUseCaseImpl
    ): CheckRecoveryPhrasesUseCase

    @Binds
    @ViewModelScoped
    fun bindMatchPhrasesUseCase(
        matchPhrasesUseCaseImpl: MatchPhrasesUseCaseImpl
    ): MatchPhrasesUseCase

}