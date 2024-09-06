package com.husseinrasti.app.feature.auth.data.datastore

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import com.husseinrasti.app.core.datastore.PreferenceDefault
import com.husseinrasti.app.core.datastore.PreferenceKeys
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class AuthDatastore @Inject constructor(
    private val dataStore: DataStore<Preferences>,
) {

    private val data: Flow<Preferences> = dataStore.data

    suspend fun isEnableBiometric() =
        data.catch {
            it.printStackTrace()
        }.map { preferences ->
            preferences[PreferenceKeys.KEY_BIOMETRIC] ?: PreferenceDefault.DEFAULT_BIOMETRIC
        }.firstOrNull()

    suspend fun getPasscode() =
        data.catch {
            it.printStackTrace()
        }.map { preferences ->
            preferences[PreferenceKeys.KEY_PASSCODE] ?: PreferenceDefault.DEFAULT_PASSCODE
        }.firstOrNull()

    suspend fun isPasscode6Digits() =
        data.catch {
            it.printStackTrace()
        }.map { preferences ->
            preferences[PreferenceKeys.KEY_PASSCODE_DIGITS] ?: PreferenceDefault.DEFAULT_PASSCODE_DIGITS
        }.firstOrNull()

    suspend fun getPhrases() =
        data.catch {
            it.printStackTrace()
        }.map { preferences ->
            preferences[PreferenceKeys.KEY_PHRASES] ?: PreferenceDefault.DEFAULT_PHRASES
        }.firstOrNull()

    suspend fun insertBiometric(biometric: Boolean) {
        dataStore.edit { preferences ->
            preferences[PreferenceKeys.KEY_BIOMETRIC] = biometric
        }
    }

    suspend fun insertPasscode(passcode: String) {
        dataStore.edit { preferences ->
            preferences[PreferenceKeys.KEY_PASSCODE] = passcode
        }
    }

    suspend fun insertPasscodeDigits(has6Digits: Boolean) {
        dataStore.edit { preferences ->
            preferences[PreferenceKeys.KEY_PASSCODE_DIGITS] = has6Digits
        }
    }

}