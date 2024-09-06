package com.husseinrasti.app.feature.create.domain.usecase

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import com.husseinrasti.app.core.dagger_hilt.scope.IoDispatcher
import com.husseinrasti.app.feature.create.domain.repository.CreateWalletRepository
import javax.inject.Inject

interface FilterPhrasesUseCase {
    suspend operator fun invoke(params: Params): Result<List<String>>

    @JvmInline
    value class Params(val word: String)
}

class FilterPhrasesUseCaseImpl @Inject constructor(
    @IoDispatcher private val ioDispatcher: CoroutineDispatcher,
    private val repository: CreateWalletRepository,
) : FilterPhrasesUseCase {
    override suspend fun invoke(params: FilterPhrasesUseCase.Params): Result<List<String>> {
        return withContext(ioDispatcher) {
            try {
                Result.success(
                    repository.getMnemonicList().filter {
                        it.subSequence(0, params.word.length).contains(params.word)
                    }
                )
            } catch (e: Exception) {
                e.printStackTrace()
                Result.failure(e)
            }
        }
    }
}