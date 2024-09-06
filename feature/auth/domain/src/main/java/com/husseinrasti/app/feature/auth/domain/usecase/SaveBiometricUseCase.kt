package com.husseinrasti.app.feature.auth.domain.usecase

import com.husseinrasti.app.feature.auth.domain.repository.AuthRepository
import javax.inject.Inject

interface SaveBiometricUseCase {
    suspend operator fun invoke(params: Params)

    @JvmInline
    value class Params(val enabled: Boolean)
}

class SaveBiometricUseCaseImpl @Inject constructor(
    private val repository: AuthRepository
) : SaveBiometricUseCase {
    override suspend fun invoke(params: SaveBiometricUseCase.Params) {
        repository.saveBiometric(params.enabled)
    }
}