package com.netag.graphql.queries

import com.apurebase.kgraphql.schema.dsl.SchemaBuilder
import com.netag.models.SuperHero
import com.netag.services.SuperHeroService
import io.ktor.util.KtorExperimentalAPI
import kotlinx.serialization.Serializable

@Serializable
data class HeroResponse(val hero: SuperHero?, val err: String?) {
    companion object {
        fun fromHero(hero: SuperHero) = HeroResponse(hero, null)
        fun fromError(err: Throwable) = HeroResponse(null, err.message)
    }
}


@OptIn(KtorExperimentalAPI::class)
class SuperHeroQuery(private val service: SuperHeroService) {
    fun call(sb: SchemaBuilder) = sb.query("superHero") {
        resolver { name: String ->
            val res = try {
                service.fetchOrCreateHero(name).fold(
                    { HeroResponse.fromError(it) },
                    { HeroResponse.fromHero(it) }
                )

            } catch (ex: RuntimeException) {
                HeroResponse.fromError(ex)
            }
            res
        }
    }
}