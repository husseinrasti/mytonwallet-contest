package com.husseinrasti.app.feature.create.ui.navigation

import com.husseinrasti.app.component.navigation.NavigationEvent
import kotlinx.serialization.Serializable

sealed interface CreateWalletRouter : NavigationEvent {
    @Serializable
    object Start : CreateWalletRouter

    @Serializable
    object WalletCreation : CreateWalletRouter

    @Serializable
    data class PhraseShowing(
        val passcode: String,
        val isUse6Digits: Boolean,
        val biometric: Boolean
    ) : CreateWalletRouter

    @Serializable
    data class PhraseTesting(
        val passcode: String,
        val isUse6Digits: Boolean,
        val biometric: Boolean
    ) : CreateWalletRouter

    @Serializable
    object PhraseRecovery : CreateWalletRouter

    @Serializable
    object Passcode : CreateWalletRouter

    @Serializable
    data class Biometric(val passcode: String, val isUse6Digits: Boolean) : CreateWalletRouter
}
