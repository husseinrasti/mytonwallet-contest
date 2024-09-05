package com.husseinrasti.app.feature.auth.domain.entity

class EncryptDataResult(
    val data: ByteArray,
    val iv: ByteArray? = null
)