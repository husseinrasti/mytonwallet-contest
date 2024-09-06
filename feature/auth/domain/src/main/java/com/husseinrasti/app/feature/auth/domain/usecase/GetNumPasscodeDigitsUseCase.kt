package com.husseinrasti.app.feature.auth.domain.usecase

import com.husseinrasti.app.feature.auth.domain.repository.AuthRepository
import javax.inject.Inject

interface GetNumPasscodeDigitsUseCase {
    suspend operator fun invoke(): Result<Boolean>
}

class GetNumPasscodeDigitsUseCaseImpl @Inject constructor(
    private val repository: AuthRepository
) : GetNumPasscodeDigitsUseCase {
    override suspend fun invoke(): Result<Boolean> {
        return repository.isUse6Digits()
    }
}