package com.netag

import com.apurebase.kgraphql.graphql
import io.ktor.application.Application
import io.ktor.application.install
import io.ktor.features.CallLogging
import io.ktor.features.DefaultHeaders
import io.ktor.routing.routing
import kotlinx.serialization.Serializable

fun main(args: Array<String>): Unit = io.ktor.server.netty.EngineMain.main(args)

@Serializable
data class MyResponse(val id: Int, val name: String)

@Suppress("unused") // Referenced in application.conf
@kotlin.jvm.JvmOverloads
fun Application.module(testing: Boolean = false) {
    install(DefaultHeaders) {
        header("X-Engine", "Ktor") // will send this header with each response
    }
    install(CallLogging)

    routing {
        graphql {
            query("response") {
                resolver { name: String ->
                    MyResponse(4, name)
                }
            }
        }
    }
}

