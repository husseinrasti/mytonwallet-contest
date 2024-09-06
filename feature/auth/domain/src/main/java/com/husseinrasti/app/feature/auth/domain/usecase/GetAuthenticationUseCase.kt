package com.husseinrasti.app.feature.auth.domain.usecase

import com.husseinrasti.app.feature.auth.domain.repository.AuthRepository
import javax.inject.Inject

interface GetAuthenticationUseCase {
    suspend operator fun invoke(): Result<Boolean>
}

class GetAuthenticationUseCaseImpl @Inject constructor(
    private val repository: AuthRepository
) : GetAuthenticationUseCase {
    override suspend fun invoke(): Result<Boolean> {
        return repository.getAuthentication()
    }
}