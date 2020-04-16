package graphql.queries

import arrow.core.Either
import com.apurebase.kgraphql.KGraphQL
import com.netag.config.fromString
import com.netag.generators.SuperHeroGen
import com.netag.graphql.queries.HeroResponse
import com.netag.graphql.queries.SuperHeroQuery
import com.netag.services.SuperHeroService
import io.kotest.assertions.arrow.option.shouldBeSome
import io.kotest.core.spec.style.FreeSpec
import io.kotest.property.arbitrary.next
import io.ktor.util.KtorExperimentalAPI
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.serialization.Serializable

@Serializable
data class QueryResponse(val superHero: HeroResponse)

@Serializable
data class GraphQLResponse(val data: QueryResponse) {
    constructor(response: HeroResponse) : this(QueryResponse(response))
}

@KtorExperimentalAPI
class SuperHeroQuerySpec : FreeSpec({
    val service = mockk<SuperHeroService>()
    val action = KGraphQL.schema {
        SuperHeroQuery(service).call(this)
    }

    "#call" - {
        val heroName = "heroName"
        val query = """query {
  superHero(name: "$heroName"){
		hero {
		  id
      appearance {
        eyeColor
        gender
        hairColor
        height
        race
        weight
        
      }
      biography {
        aliases
        alignment
        alterEgos
        firstAppearance
        fullName
        placeOfBirth
        publisher
      }
      connections {
        groupAffiliation
        relatives
      }
      name
      powerstats {
        combat
        durability
        intelligence
        power
        speed
        strength
      }
      work {
        base
        occupation
      } 
		}
    err
  }
}""".trimMargin()

        "!when service succeeds" - {
            val mockRes = SuperHeroGen.next()
            coEvery { service.fetchOrCreateHero(heroName) } returns Either.Right(mockRes)
            val expectedResponse = GraphQLResponse(HeroResponse.fromHero(mockRes))

            "it returns a hero without an error" {
                action.execute(query).fromString<GraphQLResponse>().shouldBeSome(expectedResponse)
            }
        }

        "when service fails" - {
            val expectedResponse = GraphQLResponse(HeroResponse.fromError(RuntimeException("mock")))
            coEvery { service.fetchOrCreateHero(heroName) } returns Either.Left(RuntimeException("mock"))

            "it returns the message and no hero" {
                action.execute(query).fromString<GraphQLResponse>().shouldBeSome(expectedResponse)
            }
        }

        "when service throws an exception" - {
            val expectedResponse = GraphQLResponse(HeroResponse.fromError(RuntimeException("mock")))
            coEvery { service.fetchOrCreateHero(heroName) } coAnswers { throw RuntimeException("mock") }

            "it returns the error and no hero" {
                action.execute(query).fromString<GraphQLResponse>().shouldBeSome(expectedResponse)
            }
        }
    }
})