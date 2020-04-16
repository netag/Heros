package services

import arrow.core.Either
import com.netag.db.Client
import com.netag.db.SuperHeroDao
import com.netag.generators.SuperHeroDtoGen
import com.netag.generators.SuperHeroGen
import com.netag.services.SuperHeroApiService
import com.netag.services.SuperHeroService
import io.kotest.assertions.arrow.either.shouldBeLeft
import io.kotest.assertions.arrow.either.shouldBeRight
import io.kotest.core.spec.style.FreeSpec
import io.kotest.matchers.shouldBe
import io.kotest.property.arbitrary.next
import io.ktor.config.ApplicationConfig
import io.ktor.util.KtorExperimentalAPI
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import org.koin.test.KoinTest

@OptIn(KtorExperimentalAPI::class)
class SuperHeroServiceSpec : KoinTest, FreeSpec({
    val mockApiService: SuperHeroApiService = mockk<SuperHeroApiService>()
    val mockConfig: ApplicationConfig = mockk<ApplicationConfig>()
    every {
        mockConfig.property("mongo.uri").getString()
    } returns "mongodb://root:example@0.0.0.0:27017/heros_test?authSource=admin"

    val c = Client(mockConfig)
    val dao: SuperHeroDao = SuperHeroDao(c)

    beforeTest {
        dao.collection.deleteMany()
    }

    val service = SuperHeroService(dao, mockApiService)

    "#fetchOrCreateHero" - {
        suspend fun action(name: String) = service.fetchOrCreateHero(name)

        "when hero doesn't exists in db" - {
            val heroName = "heroname"

            "should call api service" - {
                "and result is successful" - {
                    val mockResponse = SuperHeroDtoGen.next()
                    coEvery { mockApiService.searchHero(heroName) } returns Either.Right(mockResponse)

                    "should save in db and return hero" {
                        action(heroName).shouldBeRight()
                        coVerify { mockApiService.searchHero(heroName) }
                        dao.collection.countDocuments() shouldBe 1
                    }
                }

                "and result failed" - {
                    val mockException = mockk<RuntimeException>()
                    coEvery { mockApiService.searchHero(heroName) } returns Either.Left(mockException)

                    "should return an error" {
                        action(heroName).shouldBeLeft()
                        coVerify { mockApiService.searchHero(heroName) }
                        dao.collection.countDocuments() shouldBe 0
                    }
                }
            }
        }

        "when hero exists in db" - {
            val hero = SuperHeroGen.next()

            beforeTest {
                dao.insert(hero)
            }

            "should return the hero" {
                action(hero.name).shouldBeRight(hero)
                coVerify(exactly = 0) { mockApiService.searchHero(hero.name) }
            }
        }
    }
})