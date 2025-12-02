package com.lukelast.ktoon.rand

import com.lukelast.ktoon.Ktoon
import org.instancio.Instancio
import org.instancio.settings.Keys
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test

class InstancioRandomTest {
    @Test
    fun random() {
        repeat(100) {
            val randomData =
                Instancio.of(FarmTestData::class.java)
                    .withSetting(Keys.COLLECTION_MIN_SIZE, 0)
                    .withSetting(Keys.COLLECTION_MAX_SIZE, 10)
                    .withMaxDepth(500)
                    // .withSetting(Keys.STRING_NULLABLE, true)
                    .withSetting(Keys.STRING_MAX_LENGTH, 10)
                    .create()
            //            Path("original.txt").writeText(randomData.toString())
            val toonText = Ktoon().encodeToString(randomData)
            Assertions.assertTrue(toonText.isNotBlank())
            //            Path("test.toon").writeText(toonText)

            val parsedData = Ktoon().decodeFromString<FarmTestData>(toonText)
            //            Path("parsed.txt").writeText(parsedData.toString())

            Assertions.assertEquals(randomData, parsedData)
        }
    }
}
