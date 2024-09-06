package com.husseinrasti.app.feature.wallet.domain.entity

data class TokenEntity(
    val id: String,
    val amount: String,
    val amountUSD: String,
    val symbol: String,
    val image: String,
    val price: Long,
)
