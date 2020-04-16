package com.netag.db

import io.ktor.config.ApplicationConfig
import io.ktor.util.KtorExperimentalAPI
import org.litote.kmongo.coroutine.CoroutineClient
import org.litote.kmongo.coroutine.CoroutineDatabase
import org.litote.kmongo.coroutine.coroutine
import org.litote.kmongo.reactivestreams.KMongo

@KtorExperimentalAPI
class Client(config: ApplicationConfig) {
    private val connectionString = config.property("mongo.uri").getString()
    private val client: CoroutineClient by lazy {
        KMongo.createClient(connectionString).coroutine
    }

    val db: CoroutineDatabase by lazy {
        client.getDatabase("heros")
    }
}
