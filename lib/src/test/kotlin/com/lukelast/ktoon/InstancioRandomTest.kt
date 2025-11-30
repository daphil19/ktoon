package com.lukelast.ktoon

import com.lukelast.ktoon.testdata.FarmTestData
import dev.toonformat.jtoon.JToon
import kotlinx.serialization.json.Json
import org.instancio.Instancio
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Disabled
import org.junit.jupiter.api.Test

class InstancioRandomTest {
    @Test
    @Disabled
    fun random() {
        repeat(1) { time ->
            val randomData = Instancio.of(FarmTestData::class.java).withMaxDepth(100).create()
            println(Json.encodeToString(randomData))
            val ktoon = Ktoon().encodeToString(randomData)
            val jtoon = JToon.encode(randomData)
            assertEquals(jtoon, ktoon)
        }
    }
}
