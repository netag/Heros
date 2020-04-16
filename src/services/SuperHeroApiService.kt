package com.netag.services

import arrow.core.Either
import arrow.core.Left
import arrow.core.Right
import com.netag.models.SuperHero
import io.ktor.client.HttpClient
import io.ktor.client.request.get
import io.ktor.config.ApplicationConfig
import io.ktor.util.KtorExperimentalAPI
import kotlinx.serialization.Serializable

@Serializable
data class SuperHeroDto(val results: List<SuperHero>)

@KtorExperimentalAPI
class SuperHeroApiService(
    config: ApplicationConfig,
    private val client: HttpClient
) {
    private val baseUrl: String = config.property("super_hero_api.base_url").getString()

    suspend fun searchHero(name: String): Either<RuntimeException, SuperHeroDto> {
        val nameSearch = "$baseUrl/search/$name"

        return try {
            val res: SuperHeroDto = client.get(nameSearch)
            Right(res)
        } catch (err: RuntimeException) {
            println("failed parsing response: ${err.message}")
            Left(err)
        }
    }
}