package com.husseinrasti.app.feature.create.domain.di

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

}