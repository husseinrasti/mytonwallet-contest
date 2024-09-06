package com.husseinrasti.app.core.datastore

import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.core.stringSetPreferencesKey

object PreferenceKeys {
    val KEY_PHRASES = stringSetPreferencesKey("phrases")
    val KEY_BIOMETRIC = booleanPreferencesKey("biometric")
    val KEY_PASSCODE_DIGITS = booleanPreferencesKey("passcode_digits")
    val KEY_PASSCODE = stringPreferencesKey("passcode")
}