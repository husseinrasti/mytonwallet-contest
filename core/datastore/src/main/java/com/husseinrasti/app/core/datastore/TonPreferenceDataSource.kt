package com.husseinrasti.app.core.datastore

import android.content.SharedPreferences
import javax.inject.Inject


class TonPreferenceDataSource @Inject constructor(
    private val preferences: SharedPreferences
) {

    suspend fun clear() {
        edit { clear() }
    }

    suspend fun phrases(): Result<List<String>> = try {
        Result.success(Mapper.fromJson(preferences[PreferenceKeys.KEY_PHRASES]!!))
    } catch (e: Exception) {
        e.printStackTrace()
        Result.failure(e)
    }

    suspend fun setPhrases(phrases: List<String>) {
        preferences[PreferenceKeys.KEY_PHRASES] = Mapper.toJson(phrases)
    }

    fun isActiveBiometric(): Result<Boolean> = try {
        Result.success(preferences[PreferenceKeys.KEY_BIOMETRIC]!!)
    } catch (e: Exception) {
        e.printStackTrace()
        Result.failure(e)
    }

    suspend fun setActiveBiometric(isActive: Boolean) {
        preferences[PreferenceKeys.KEY_BIOMETRIC] = isActive
    }

    fun passcodeBiometric(): Result<String> = try {
        Result.success(preferences[PreferenceKeys.KEY_PASSCODE]!!)
    } catch (e: Exception) {
        e.printStackTrace()
        Result.failure(e)
    }

    suspend fun setPasscode(passcode: String) {
        preferences[PreferenceKeys.KEY_PASSCODE] = passcode
    }

    private inline fun edit(editor: SharedPreferences.Editor.() -> Unit) {
        with(preferences.edit()) {
            editor(this)
            apply()
        }
    }

    /**
     * puts a key value pair in shared prefs if doesn't exists, otherwise updates value on given [key]
     */
    private operator fun SharedPreferences.set(key: String, value: Any?) {
        when (value) {
            is String? -> edit { putString(key, value) }
            is Int -> edit { putInt(key, value) }
            is Boolean -> edit { putBoolean(key, value) }
            is Float -> edit { putFloat(key, value) }
            is Long -> edit { putLong(key, value) }
            is Set<*> -> edit { putStringSet(key, value as Set<String>?) }
            else -> throw UnsupportedOperationException("Not yet implemented")
        }
    }

    /**
     * finds value on given key.
     * [T] is the type of value
     * @param defaultValue optional default value - will take null for strings, false for bool and -1 for numeric values if [defaultValue] is not specified
     */
    private inline operator fun <reified T : Any> SharedPreferences.get(key: String, defaultValue: T? = null): T? {
        return when (T::class) {
            String::class -> getString(key, defaultValue as? String) as T?
            Int::class -> getInt(key, defaultValue as? Int ?: -1) as T?
            Boolean::class -> getBoolean(key, defaultValue as? Boolean ?: false) as T?
            Float::class -> getFloat(key, defaultValue as? Float ?: -1f) as T?
            Long::class -> getLong(key, defaultValue as? Long ?: -1) as T?
            else -> throw UnsupportedOperationException("Not yet implemented")
        }
    }
}