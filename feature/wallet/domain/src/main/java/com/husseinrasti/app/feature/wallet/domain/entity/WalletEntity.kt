package com.husseinrasti.app.feature.wallet.domain.entity

data class WalletEntity(
    val name: String,
    val balance: String,
    val symbol: String,
    val tokens: List<TokenEntity>,
    val transactions: List<TransactionEntity>
)