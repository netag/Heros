package com.netag.graphql

import com.apurebase.kgraphql.schema.dsl.SchemaBuilder
import com.netag.graphql.queries.SuperHeroQuery
import com.netag.services.SuperHeroService
import io.ktor.util.KtorExperimentalAPI

@KtorExperimentalAPI
class SuperHeroSchema(private val service: SuperHeroService) {
    fun heroGraphql(sb: SchemaBuilder) {
        SuperHeroQuery(service).call(sb)
    }
}

