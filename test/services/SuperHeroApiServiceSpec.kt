package com.netag.services

import com.netag.config.Json
import io.kotest.assertions.arrow.either.shouldBeLeft
import io.kotest.assertions.arrow.either.shouldBeRight
import io.kotest.core.spec.style.FreeSpec
import io.ktor.client.HttpClient
import io.ktor.client.engine.mock.MockEngine
import io.ktor.client.engine.mock.respond
import io.ktor.client.features.json.JsonFeature
import io.ktor.client.features.json.serializer.KotlinxSerializer
import io.ktor.config.ApplicationConfig
import io.ktor.http.ContentType
import io.ktor.http.HttpStatusCode
import io.ktor.http.headersOf
import io.ktor.util.KtorExperimentalAPI
import io.mockk.every
import io.mockk.mockk

@KtorExperimentalAPI
class SuperHeroApiServiceSpec : FreeSpec({

    val config = mockk<ApplicationConfig>()

    fun service(mockApiRespons: String): SuperHeroApiService {
        val mockHttpClient = HttpClient(MockEngine) {
            install(JsonFeature) {
                serializer = KotlinxSerializer(Json.json)
            }
            engine {
                addHandler { _ ->
                    respond(
                        content = mockApiRespons,
                        status = HttpStatusCode.OK,
                        headers = headersOf("Content-Type" to listOf(ContentType.Application.Json.toString()))
                    )
                }
            }
        }
        return SuperHeroApiService(config, mockHttpClient)
    }

    "#searchHero" - {
        beforeTest {
            every { config.property(any()).getString() } returns "mock-base-url"
        }

        "when name exists" - {
            val successResponse: String =
                """{"response":"success","results-for":"ironman","results":[{"id":"732","name":"Ironman","powerstats":{"intelligence":"100","strength":"85","speed":"58","durability":"85","power":"100","combat":"64"},"biography":{"full-name":"Tony Stark","alter-egos":"No alter egos found.","aliases":["Iron Knight","Hogan Potts","Spare Parts Man","Cobalt Man II","Crimson Dynamo","Ironman"],"place-of-birth":"Long Island, New York","first-appearance":"Tales of Suspence #39 (March, 1963)","publisher":"Marvel Comics","alignment":"good"},"appearance":{"gender":"Male","race":"Human","height":["6'6","198 cm"],"weight":["425 lb","191 kg"],"eye-color":"Blue","hair-color":"Black"},"work":{"occupation":"Inventor, Industrialist; former United States Secretary of Defense","base":"Seattle, Washington"},"connections":{"group-affiliation":"Avengers, Illuminati, Stark Resilient; formerly S.H.I.E.L.D., leader of Stark Enterprises, the Pro-Registration Superhero Unit, New Avengers, Mighty Avengers, Hellfire Club, Force Works, Avengers West Coast, United States Department of Defense.","relatives":"Howard Anthony Stark (father, deceased), Maria Stark (mother, deceased), Morgan Stark (cousin), Isaac Stark (ancestor)"},"image":{"url":"https:\/\/www.superherodb.com\/pictures2\/portraits\/10\/100\/85.jpg"}}]}"""

            "should return a hero" {
                service(successResponse).searchHero("heroName").shouldBeRight()
            }
        }

        "when name is missing" - {
            val failedResponse = """{"response":"error","error":"character with given name not found"}"""

            "should return an error" {
                service(failedResponse).searchHero("heroName").shouldBeLeft()
            }
        }
    }
})