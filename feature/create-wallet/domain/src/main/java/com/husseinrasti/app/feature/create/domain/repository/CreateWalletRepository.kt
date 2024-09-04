package com.husseinrasti.app.feature.create.domain.repository

interface CreateWalletRepository {

    suspend fun generatePhrases(): Result<Boolean>

    suspend fun isExistPhrases(): Result<Boolean>

    suspend fun getPhrases(): Result<List<String>>

    suspend fun getMnemonicList(): List<String>

}