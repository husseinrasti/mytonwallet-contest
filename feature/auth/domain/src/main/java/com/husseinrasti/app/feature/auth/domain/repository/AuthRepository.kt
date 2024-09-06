package com.husseinrasti.app.feature.auth.domain.repository

import com.husseinrasti.app.feature.auth.domain.entity.PasscodeEntity

interface AuthRepository {
    suspend fun getAuthentication(): Result<Boolean>
    suspend fun isEnabledBiometric(): Result<Boolean>
    suspend fun isUse6Digits(): Result<Boolean>
    suspend fun getPasscode(): Result<String>
    suspend fun savePasscode(entity: PasscodeEntity)
    suspend fun saveBiometric(enabled: Boolean)
}