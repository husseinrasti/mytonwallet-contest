package com.husseinrasti.app.feature.auth.domain.usecase

import com.husseinrasti.app.feature.auth.domain.entity.AuthEntity
import com.husseinrasti.app.feature.auth.domain.entity.PasscodeEntity
import com.husseinrasti.app.feature.auth.domain.repository.AuthRepository
import javax.inject.Inject

interface CheckAuthenticationUseCase {
    suspend operator fun invoke(entity: AuthEntity): Result<Boolean>
}

class CheckAuthenticationUseCaseImpl @Inject constructor(
    private val repository: AuthRepository
) : CheckAuthenticationUseCase {
    override suspend fun invoke(entity: AuthEntity): Result<Boolean> {
        val passcode = repository.getPasscode().getOrNull()
        val biometric = repository.isEnabledBiometric().getOrNull()
        return if (entity.biometric != null && entity.biometric == biometric) {
            Result.success(true)
        } else if (entity.passcode.isNullOrEmpty().not() && entity.passcode == passcode) {
            Result.success(true)
        } else Result.success(false)
    }
}