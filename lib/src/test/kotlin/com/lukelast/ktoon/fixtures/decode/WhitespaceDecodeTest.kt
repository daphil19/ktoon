package com.lukelast.ktoon.fixtures.decode

import com.lukelast.ktoon.fixtures.runDecodeFixtureTest
import kotlinx.serialization.Serializable
import org.junit.jupiter.api.Test

/**
 * Tests from whitespace.json fixture - Whitespace tolerance in decoding: surrounding spaces around delimiters and values.
 */
class WhitespaceDecodeTest {

    private val fixture = "whitespace"

    @Test
    fun `tolerates spaces around commas in inline arrays`() {
        runDecodeFixtureTest<Map<String, List<String>>>(fixture)
    }

    @Test
    fun `tolerates spaces around pipes in inline arrays`() {
        runDecodeFixtureTest<Map<String, List<String>>>(fixture)
    }

    @Test
    fun `tolerates spaces around tabs in inline arrays`() {
        runDecodeFixtureTest<Map<String, List<String>>>(fixture)
    }

    @Serializable
    data class TabularItem(val id: Int, val name: String)

    @Serializable
    data class TabularResult(val items: List<TabularItem>)

    @Test
    fun `tolerates leading and trailing spaces in tabular row values`() {
        runDecodeFixtureTest<TabularResult>(fixture)
    }

    @Test
    fun `tolerates spaces around delimiters with quoted values`() {
        runDecodeFixtureTest<Map<String, List<String>>>(fixture)
    }

    @Test
    fun `parses empty tokens as empty string`() {
        runDecodeFixtureTest<Map<String, List<String>>>(fixture)
    }
}
