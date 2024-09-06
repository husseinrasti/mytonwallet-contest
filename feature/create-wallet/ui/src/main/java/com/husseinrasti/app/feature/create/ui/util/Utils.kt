package com.husseinrasti.app.feature.create.ui.util


internal const val REQUIRE_MILLISECOND_WRITE_PHRASE = 60000

internal fun isTimeDiffOne(startTime: Long, currentTimeMillis: Long): Boolean {
    return (currentTimeMillis - startTime) < REQUIRE_MILLISECOND_WRITE_PHRASE
}
