package com.netag.services

import arrow.core.Either
import arrow.core.Right
import com.netag.db.SuperHeroDao
import com.netag.models.SuperHero
import io.ktor.util.KtorExperimentalAPI

@KtorExperimentalAPI
class SuperHeroService(
    private val dao: SuperHeroDao,
    private val api: SuperHeroApiService
) {
    suspend fun fetchOrCreateHero(name: String): Either<RuntimeException, SuperHero> {
        return dao.fetchHero(name).fold({ findAndSaveHero(name) }, { Right(it) })
    }

    private suspend fun findAndSaveHero(name: String): Either<RuntimeException, SuperHero> {
        return api.searchHero(name).flatMap { dto ->
            dao.insert(dto.results.first())
        }
    }
}

suspend fun <L, R1, R2> Either<L, R1>.flatMap(f: suspend (R1) -> Either<L, R2>): Either<L, R2> {
    return when(this) {
        is Either.Left -> this
        is Either.Right -> f(this.b)
    }
}