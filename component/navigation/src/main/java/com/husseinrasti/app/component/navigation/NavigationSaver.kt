package com.husseinrasti.app.component.navigation

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.Saver

fun <T> navigationSaver() = Saver<MutableState<T>, Any>(
    save = { state -> state.value ?: "null" },
    restore = { value ->
        @Suppress("UNCHECKED_CAST")
        (mutableStateOf((if (value == "null") null else value) as T))
    }
)