package com.husseinrasti.app.feature.create.data.datastore

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

class WalletDatastore @Inject constructor(
    private val dataStore: DataStore<Preferences>,
) {

    private val data: Flow<Preferences> = dataStore.data
    suspend fun getPhrases() =
        data.catch {
            it.printStackTrace()
        }.map { preferences ->
            preferences[PreferenceKeys.KEY_PHRASES] ?: PreferenceDefault.DEFAULT_PHRASES
        }.firstOrNull()

    suspend fun insertPhrases(phrases: List<String>) {
        dataStore.edit { preferences ->
            preferences[PreferenceKeys.KEY_PHRASES] = phrases.toSet()
        }
    }

}