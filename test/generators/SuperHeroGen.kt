package com.netag.generators

import com.netag.models.*
import com.netag.services.SuperHeroDto
import io.kotest.property.Arb
import io.kotest.property.arbitrary.*


val AppearanceGen: Arb<Appearance> = Arb.bind(
    Arb.string(10, 10),
    Arb.string(10, 10),
    Arb.list(Arb.string(10, 10), 1..2),
    Arb.list(Arb.string(10, 10), 1..2),
    Arb.string(10, 10),
    Arb.string(10, 10),
    ::Appearance
)

val BiographyGen: Arb<Biography> = Arb.bind(
    Arb.string(10, 10),
    Arb.string(10, 10),
    Arb.list(Arb.string(10, 10), 1..2),
    Arb.string(10, 10),
    Arb.string(10, 10),
    Arb.string(10, 10),
    Arb.string(10, 10),
    ::Biography
)

val ConnectionsGen: Arb<Connections> = Arb.bind(
    Arb.string(10, 10),
    Arb.string(10, 10),
    ::Connections
)

val ImageGen: Arb<Image> = Arb.string().map { Image(it) }

val PowerstatsGen: Arb<Powerstats> = Arb.bind(
    Arb.int(0, 100),
    Arb.int(0, 100),
    Arb.int(0, 100),
    Arb.int(0, 100),
    Arb.int(0, 100),
    Arb.int(0, 100),
    ::Powerstats
)

val WorkGen: Arb<Work> =
    Arb.bind<String, String, Work>(
        Arb.string(10, 10),
        Arb.string(10, 10),
        ::Work
    )

val SuperHeroGen: Arb<SuperHero> = arb { rs ->
    generateSequence {
        SuperHero(
            Arb.positiveInts().next(rs),
            Arb.string().next(rs),
            PowerstatsGen.next(rs),
            BiographyGen.next(rs),
            AppearanceGen.next(rs),
            WorkGen.next(rs),
            ConnectionsGen.next(rs),
            ImageGen.next(rs)
        )
    }
}

val SuperHeroDtoGen: Arb<SuperHeroDto> = Arb.list(SuperHeroGen, 1..2).map(::SuperHeroDto)
