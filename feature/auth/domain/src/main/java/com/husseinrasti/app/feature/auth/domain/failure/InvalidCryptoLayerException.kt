package com.husseinrasti.app.feature.auth.domain.failure

import com.husseinrasti.app.feature.auth.domain.enums.ValidationResult

class InvalidCryptoLayerException(validationResult: ValidationResult) : Exception() {

    val isKeyPermanentlyInvalidated = validationResult == ValidationResult.KEY_PERMANENTLY_INVALIDATED

    val isKeyInitFailed = validationResult == ValidationResult.KEY_INIT_FAIL
}