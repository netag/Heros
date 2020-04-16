package com.netag

import io.kotest.core.spec.style.FreeSpec
import io.kotest.matchers.shouldBe
import io.ktor.config.MapApplicationConfig
import io.ktor.http.HttpMethod
import io.ktor.server.testing.handleRequest
import io.ktor.server.testing.withTestApplication
import io.ktor.util.KtorExperimentalAPI

@OptIn(KtorExperimentalAPI::class)
class ApplicationSpec : FreeSpec({
    "sanity"  {
        withTestApplication({
            (environment.config as MapApplicationConfig).apply {
                put("super_hero_api.base_url", "change-me")
                put("mongo.uri", "mongodb://root:example@0.0.0.0:27017/heros_test?authSource=admin")
            }
            module()
        })  {
            handleRequest(HttpMethod.Post, "/").apply {
                requestHandled shouldBe false
            }
        }
    }
})
