package com.netag.db

import arrow.core.*
import com.mongodb.client.model.IndexOptions
import com.netag.models.SuperHero
import io.ktor.util.KtorExperimentalAPI
import kotlinx.coroutines.runBlocking
import org.litote.kmongo.coroutine.CoroutineCollection
import org.litote.kmongo.eq

@KtorExperimentalAPI
class SuperHeroDao(client: Client) {
    internal val collection: CoroutineCollection<SuperHero> = client.db.getCollection<SuperHero>("heros")

    init {
        runBlocking { collection.ensureUniqueIndex(SuperHero::name, indexOptions = IndexOptions().background(true)) }
    }

    suspend fun fetchHero(name: String): Option<SuperHero> {
        return collection.findOne(SuperHero::name eq name).toOption()
    }

    suspend fun insert(hero: SuperHero): Either<RuntimeException, SuperHero> {
        val res = collection.insertOne(hero)
        if (!res.wasAcknowledged()) Left(RuntimeException("failed to insert hero: ${hero.name}"))
        return Right(hero)
    }
}