package com.husseinrasti.app.feature.auth.domain.usecase

import com.husseinrasti.app.feature.auth.domain.entity.PasscodeEntity
import com.husseinrasti.app.feature.auth.domain.repository.AuthRepository
import javax.inject.Inject

interface SavePasscodeUseCase {
    suspend operator fun invoke(entity: PasscodeEntity)
}

class SavePasscodeUseCaseImpl @Inject constructor(
    private val repository: AuthRepository
) : SavePasscodeUseCase {
    override suspend fun invoke(entity: PasscodeEntity) {
        repository.savePasscode(entity)
    }
}