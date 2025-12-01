package com.lukelast.ktoon.fixtures.decode

import com.lukelast.ktoon.fixtures.runDecodeFixtureTest
import kotlinx.serialization.Serializable
import org.junit.jupiter.api.Test

/**
 * Tests from delimiters.json fixture - Delimiter decoding: tab and pipe delimiter parsing, delimiter-aware value splitting.
 */
class DelimitersDecodeTest {

    private val fixture = "delimiters"

    @Test
    fun `parses primitive arrays with tab delimiter`() {
        runDecodeFixtureTest<Map<String, List<String>>>(fixture)
    }

    @Test
    fun `parses primitive arrays with pipe delimiter`() {
        runDecodeFixtureTest<Map<String, List<String>>>(fixture)
    }

    @Test
    fun `parses primitive arrays with comma delimiter`() {
        runDecodeFixtureTest<Map<String, List<String>>>(fixture)
    }

    @Serializable
    data class TabularItem(val sku: String, val qty: Int, val price: Double)

    @Serializable
    data class TabularResult(val items: List<TabularItem>)

    @Test
    fun `parses tabular arrays with tab delimiter`() {
        runDecodeFixtureTest<TabularResult>(fixture)
    }

    @Test
    fun `parses tabular arrays with pipe delimiter`() {
        runDecodeFixtureTest<TabularResult>(fixture)
    }

    @Serializable
    data class PairsResult(val pairs: List<List<String>>)

    @Test
    fun `parses nested arrays with tab delimiter`() {
        runDecodeFixtureTest<PairsResult>(fixture)
    }

    @Test
    fun `parses nested arrays with pipe delimiter`() {
        runDecodeFixtureTest<PairsResult>(fixture)
    }

    @Serializable
    data class NestedItem(val tags: List<String>)

    @Serializable
    data class NestedItemsResult(val items: List<NestedItem>)

    @Test
    fun `parses nested arrays inside list items with default comma delimiter`() {
        runDecodeFixtureTest<NestedItemsResult>(fixture)
    }

    @Test
    fun `parses nested arrays inside list items with default comma delimiter when parent uses pipe`() {
        runDecodeFixtureTest<NestedItemsResult>(fixture)
    }

    @Test
    fun `parses root-level array with tab delimiter`() {
        runDecodeFixtureTest<List<String>>(fixture)
    }

    @Test
    fun `parses root-level array with pipe delimiter`() {
        runDecodeFixtureTest<List<String>>(fixture)
    }

    @Serializable
    data class IdOnly(val id: Int)

    @Test
    fun `parses root-level array of objects with tab delimiter`() {
        runDecodeFixtureTest<List<IdOnly>>(fixture)
    }

    @Test
    fun `parses root-level array of objects with pipe delimiter`() {
        runDecodeFixtureTest<List<IdOnly>>(fixture)
    }

    @Test
    fun `parses values containing tab delimiter when quoted`() {
        runDecodeFixtureTest<Map<String, List<String>>>(fixture)
    }

    @Test
    fun `parses values containing pipe delimiter when quoted`() {
        runDecodeFixtureTest<Map<String, List<String>>>(fixture)
    }

    @Test
    fun `does not split on commas when using tab delimiter`() {
        runDecodeFixtureTest<Map<String, List<String>>>(fixture)
    }

    @Test
    fun `does not split on commas when using pipe delimiter`() {
        runDecodeFixtureTest<Map<String, List<String>>>(fixture)
    }

    @Serializable
    data class TabularItemWithNote(val id: Int, val note: String)

    @Serializable
    data class TabularWithNoteResult(val items: List<TabularItemWithNote>)

    @Test
    fun `parses tabular values containing comma with comma delimiter`() {
        runDecodeFixtureTest<TabularWithNoteResult>(fixture)
    }

    @Test
    fun `does not require quoting commas with tab delimiter`() {
        runDecodeFixtureTest<TabularWithNoteResult>(fixture)
    }

    @Test
    fun `does not require quoting commas in object values`() {
        runDecodeFixtureTest<Map<String, String>>(fixture)
    }

    @Serializable
    data class ListStatus(val status: String)

    @Serializable
    data class ListItemResult(val items: List<ListStatus>)

    @Test
    fun `object values in list items follow document delimiter`() {
        runDecodeFixtureTest<ListItemResult>(fixture)
    }

    @Test
    fun `object values with comma must be quoted when document delimiter is comma`() {
        runDecodeFixtureTest<ListItemResult>(fixture)
    }

    @Test
    fun `parses nested array values containing pipe delimiter`() {
        runDecodeFixtureTest<PairsResult>(fixture)
    }

    @Test
    fun `parses nested array values containing tab delimiter`() {
        runDecodeFixtureTest<PairsResult>(fixture)
    }

    @Test
    fun `preserves quoted ambiguity with pipe delimiter`() {
        runDecodeFixtureTest<Map<String, List<String>>>(fixture)
    }

    @Test
    fun `preserves quoted ambiguity with tab delimiter`() {
        runDecodeFixtureTest<Map<String, List<String>>>(fixture)
    }

    @Test
    fun `parses structural-looking strings when quoted with pipe delimiter`() {
        runDecodeFixtureTest<Map<String, List<String>>>(fixture)
    }

    @Test
    fun `parses structural-looking strings when quoted with tab delimiter`() {
        runDecodeFixtureTest<Map<String, List<String>>>(fixture)
    }

    @Serializable
    data class TabularWithSpecialKey(val items: List<Map<String, Int>>)

    @Test
    fun `parses tabular headers with keys containing the active delimiter`() {
        runDecodeFixtureTest<TabularWithSpecialKey>(fixture)
    }
}
