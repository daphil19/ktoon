package com.lukelast.ktoon

import com.lukelast.ktoon.testdata.FarmTestData
import dev.toonformat.jtoon.JToon
import org.instancio.Instancio
import org.instancio.settings.Keys
import org.junit.jupiter.api.Disabled
import org.junit.jupiter.api.Test

class InstancioRandomTest {
    @Test
    @Disabled
    fun random() {
        repeat(10) { time ->
            val randomData =
                Instancio.of(FarmTestData::class.java)
                    .withSetting(Keys.COLLECTION_MIN_SIZE, 20)
                    .withMaxDepth(100)
                    .create()
            //            println(Json.encodeToString(randomData))
            val ktoon = Ktoon().encodeToString(randomData)
            val jtoon = JToon.encode(randomData)
            //            assertEquals(jtoon, ktoon)
        }
    }
}
