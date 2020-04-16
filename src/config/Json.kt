package com.netag.config

import arrow.core.None
import arrow.core.Option
import arrow.core.Some
import kotlinx.serialization.ImplicitReflectionSerializer
import kotlinx.serialization.json.JsonConfiguration
import kotlinx.serialization.parse
import kotlinx.serialization.json.Json as KJson

object Json {
    val json = KJson(JsonConfiguration.Stable.copy(ignoreUnknownKeys = true))

    @OptIn(ImplicitReflectionSerializer::class)
    inline fun <reified T: Any> fromString(str: String): Option<T> {
        return try {
            Some(json.parse(str))
        } catch (ex: RuntimeException) {
            None
        }
    }
}

inline fun <reified T: Any> String.fromString(): Option<T> = Json.fromString(this)
