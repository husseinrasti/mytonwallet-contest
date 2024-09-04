package com.husseinrasti.app.core.dagger_hilt.module

import com.husseinrasti.app.core.dagger_hilt.scope.ApplicationCoroutineDefaultScope
import com.husseinrasti.app.core.dagger_hilt.scope.ApplicationCoroutineIoScope
import com.husseinrasti.app.core.dagger_hilt.scope.CoroutineDefaultScope
import com.husseinrasti.app.core.dagger_hilt.scope.CoroutineIoScope
import com.husseinrasti.app.core.dagger_hilt.scope.CoroutineMainImmediateScope
import com.husseinrasti.app.core.dagger_hilt.scope.CoroutineMainScope
import com.husseinrasti.app.core.dagger_hilt.scope.DefaultDispatcher
import com.husseinrasti.app.core.dagger_hilt.scope.IoDispatcher
import com.husseinrasti.app.core.dagger_hilt.scope.MainDispatcher
import com.husseinrasti.app.core.dagger_hilt.scope.MainImmediateDispatcher
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
object CoroutinesScopesModule {

    @Singleton
    @Provides
    @ApplicationCoroutineDefaultScope
    fun providesApplicationDefaultScope(
        @DefaultDispatcher defaultDispatcher: CoroutineDispatcher
    ): CoroutineScope = CoroutineScope(SupervisorJob() + defaultDispatcher)

    @Singleton
    @Provides
    @ApplicationCoroutineIoScope
    fun providesApplicationIoScope(
        @IoDispatcher ioDispatcher: CoroutineDispatcher
    ): CoroutineScope = CoroutineScope(SupervisorJob() + ioDispatcher)

    @Singleton
    @Provides
    @CoroutineDefaultScope
    fun providesCoroutineDefaultScope(
        @DefaultDispatcher defaultDispatcher: CoroutineDispatcher
    ): CoroutineScope = CoroutineScope(defaultDispatcher)

    @Singleton
    @Provides
    @CoroutineIoScope
    fun providesCoroutineIoScope(
        @IoDispatcher ioDispatcher: CoroutineDispatcher
    ): CoroutineScope = CoroutineScope(ioDispatcher)

    @Singleton
    @Provides
    @CoroutineMainScope
    fun providesCoroutineMainScope(
        @MainDispatcher mainDispatcher: CoroutineDispatcher
    ): CoroutineScope = CoroutineScope(mainDispatcher)

    @Singleton
    @Provides
    @CoroutineMainImmediateScope
    fun providesCoroutineMainImmediateScope(
        @MainImmediateDispatcher mainImmediateDispatcher: CoroutineDispatcher
    ): CoroutineScope = CoroutineScope(mainImmediateDispatcher)

}