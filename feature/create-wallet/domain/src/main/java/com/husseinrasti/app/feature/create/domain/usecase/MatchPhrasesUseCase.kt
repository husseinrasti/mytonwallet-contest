package com.husseinrasti.app.feature.create.domain.usecase

import android.content.res.Resources.NotFoundException
import com.husseinrasti.app.feature.create.domain.repository.CreateWalletRepository
import javax.inject.Inject

interface MatchPhrasesUseCase {
    suspend operator fun invoke(params: Params): Result<Boolean>

    @JvmInline
    value class Params(val phrases: List<String>)
}


class MatchPhrasesUseCaseImpl @Inject constructor(
    private val repository: CreateWalletRepository
) : MatchPhrasesUseCase {
    override suspend fun invoke(params: MatchPhrasesUseCase.Params): Result<Boolean> {
        val phrases = repository.getPhrases().getOrNull()
        return if (phrases?.containsAll(params.phrases) == true) Result.success(true)
        else Result.failure(NotFoundException())
    }
}