package com.husseinrasti.app.feature.wallet.domain.entity

data class TransactionEntity(
    val id: String,
    val amount: String,
    val amountUSD: String,
    val sender: String,
    val receiver: String,
    val type: Type,
    val symbol: String,
    val image: String,
    val trxLink: String,
    val date: Long,
    val fee: String,
) {
    enum class Type {
        SEND,
        RECEIVE
    }
}
