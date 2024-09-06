package com.husseinrasti.app.feature.auth.data.repository

import android.util.Log
import com.husseinrasti.app.core.dagger_hilt.scope.ApplicationCoroutineIoScope
import com.husseinrasti.app.core.dagger_hilt.scope.IoDispatcher
import com.husseinrasti.app.feature.auth.data.datastore.AuthDatastore
import com.husseinrasti.app.feature.auth.domain.entity.PasscodeEntity
import com.husseinrasti.app.feature.auth.domain.repository.AuthRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

class AuthRepositoryImpl @Inject constructor(
    @IoDispatcher private val ioDispatcher: CoroutineDispatcher,
    @ApplicationCoroutineIoScope private val appScope: CoroutineScope,
    private val authDatastore: AuthDatastore
) : AuthRepository {
    override suspend fun getAuthentication(): Result<Boolean> = withContext(ioDispatcher) {
        try {
            val result = authDatastore.getPhrases().isNullOrEmpty().not() &&
                    (authDatastore.getPasscode().isNullOrEmpty().not() ||
                            authDatastore.isEnableBiometric() ?: false)
            Result.success(result)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun isEnabledBiometric(): Result<Boolean> = withContext(ioDispatcher) {
        try {
            Result.success(authDatastore.isEnableBiometric()!!)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun isUse6Digits(): Result<Boolean> = withContext(ioDispatcher) {
        try {
            Result.success(authDatastore.isPasscode6Digits()!!)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getPasscode(): Result<String> = withContext(ioDispatcher) {
        try {
            Result.success(authDatastore.getPasscode()!!)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun savePasscode(entity: PasscodeEntity) {
        Log.i("TAG", "savePasscode: ${entity.passcode} , ${entity.is6Digits}")
        appScope.launch {
            authDatastore.insertPasscode(entity.passcode)
        }
        appScope.launch {
            authDatastore.insertPasscodeDigits(entity.is6Digits)
        }
    }

    override suspend fun saveBiometric(enabled: Boolean) {
        appScope.launch {
            authDatastore.insertBiometric(enabled)
        }
    }

}