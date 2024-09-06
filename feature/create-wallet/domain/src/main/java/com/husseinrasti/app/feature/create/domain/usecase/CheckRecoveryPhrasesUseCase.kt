package com.husseinrasti.app.feature.create.domain.usecase

import android.content.res.Resources.NotFoundException
import com.husseinrasti.app.feature.create.domain.repository.CreateWalletRepository
import javax.inject.Inject

interface CheckRecoveryPhrasesUseCase {
    suspend operator fun invoke(params: Params): Result<Boolean>

    @JvmInline
    value class Params(val phrases: List<String>)
}


class CheckRecoveryPhrasesUseCaseImpl @Inject constructor(
    private val repository: CreateWalletRepository
) : CheckRecoveryPhrasesUseCase {
    override suspend fun invoke(params: CheckRecoveryPhrasesUseCase.Params): Result<Boolean> {
        val allPhrases = repository.getMnemonicList()
        return if (allPhrases.containsAll(params.phrases)) Result.success(true)
        else Result.failure(NotFoundException())
    }
}