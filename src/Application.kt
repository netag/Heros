package com.netag

import com.apurebase.kgraphql.graphql
import com.netag.config.Json
import com.netag.db.Client
import com.netag.db.SuperHeroDao
import com.netag.graphql.SuperHeroSchema
import com.netag.services.SuperHeroApiService
import com.netag.services.SuperHeroService
import io.ktor.application.Application
import io.ktor.application.install
import io.ktor.client.HttpClient
import io.ktor.client.engine.cio.CIO
import io.ktor.client.features.json.JsonFeature
import io.ktor.client.features.json.serializer.KotlinxSerializer
import io.ktor.config.ApplicationConfig
import io.ktor.features.CallLogging
import io.ktor.features.DefaultHeaders
import io.ktor.routing.routing
import io.ktor.util.KtorExperimentalAPI
import kotlinx.serialization.UnstableDefault
import org.koin.dsl.module
import org.koin.ktor.ext.Koin
import org.koin.ktor.ext.inject

fun main(args: Array<String>): Unit = io.ktor.server.netty.EngineMain.main(args)

@OptIn(UnstableDefault::class)
@KtorExperimentalAPI
fun Application.module() {
    install(DefaultHeaders) {
        header("X-Engine", "Ktor") // will send this header with each response
    }
    install(CallLogging)
    install(Koin) {
        modules(koinModule(environment.config))
    }

    routing {
        val superHeroSchema by inject<SuperHeroSchema>()

        graphql {
            superHeroSchema.heroGraphql(this)
        }
    }
}

@KtorExperimentalAPI
fun koinModule(config: ApplicationConfig) = module(createdAtStart = true) {
    single {
        HttpClient(CIO) {
            install(JsonFeature) {
                serializer = KotlinxSerializer(Json.json)
            }
        }
    }
    single { Client(config) }
    single { SuperHeroDao(get()) }
    single { SuperHeroApiService(config, get()) }
    single { SuperHeroService(get(), get()) }
    single { SuperHeroSchema(get()) }
}


