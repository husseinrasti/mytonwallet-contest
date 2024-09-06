package com.husseinrasti.app.feature.create.ui.navigation

import com.husseinrasti.app.component.navigation.NavigationEvent
import kotlinx.serialization.Serializable

sealed interface CreateWalletRouter : NavigationEvent {
    @Serializable
    data object Start : CreateWalletRouter

    @Serializable
    data object WalletCreation : CreateWalletRouter

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
    data object PhraseRecovery : CreateWalletRouter

    @Serializable
    data object Passcode : CreateWalletRouter

    @Serializable
    data class Biometric(val passcode: String, val isUse6Digits: Boolean) : CreateWalletRouter
}
