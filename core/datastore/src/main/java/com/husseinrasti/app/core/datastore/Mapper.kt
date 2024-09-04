package com.husseinrasti.app.core.datastore

import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

internal object Mapper {

    fun fromJson(value: String): List<String> {
        val typeItem = object : TypeToken<List<String>>() {}.type
        return Gson().fromJson(value, typeItem)
    }

    fun toJson(list: List<String>): String = Gson().toJson(list)

}