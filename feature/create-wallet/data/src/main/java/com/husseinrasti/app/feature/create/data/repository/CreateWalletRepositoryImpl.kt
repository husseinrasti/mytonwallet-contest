package com.husseinrasti.app.feature.create.data.repository

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import com.husseinrasti.app.core.dagger_hilt.scope.DefaultDispatcher
import com.husseinrasti.app.core.dagger_hilt.scope.IoDispatcher
import com.husseinrasti.app.core.datastore.TonPreferenceDataSource
import com.husseinrasti.app.core.security.mnemonic.Mnemonic
import com.husseinrasti.app.core.security.mnemonic.getMnemonicWorldList
import com.husseinrasti.app.feature.create.domain.repository.CreateWalletRepository
import javax.inject.Inject

class CreateWalletRepositoryImpl @Inject constructor(
    @DefaultDispatcher private val defaultDispatcher: CoroutineDispatcher,
    @IoDispatcher private val ioDispatcher: CoroutineDispatcher,
    private val dataSource: TonPreferenceDataSource,
) : CreateWalletRepository {
    override suspend fun generatePhrases(): Result<Boolean> {
        return withContext(ioDispatcher) {
            try {
                val phrases = Mnemonic.generate(dispatcher = defaultDispatcher)
                dataSource.setPhrases(phrases)
                Result.success(true)
            } catch (e: Exception) {
                e.printStackTrace()
                Result.failure(e)
            }
        }
    }

    override suspend fun getPhrases(): Result<List<String>> =
        dataSource.phrases()

    override suspend fun isExistPhrases(): Result<Boolean> {
        return withContext(ioDispatcher) {
            getPhrases().fold(
                onSuccess = {
                    Result.success(it.isNotEmpty())
                },
                onFailure = {
                    Result.failure(NullPointerException())
                }
            )
        }
    }

    override suspend fun getMnemonicList(): List<String> = getMnemonicWorldList()

}