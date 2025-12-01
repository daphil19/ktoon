package com.lukelast.ktoon.fixtures.encode

import com.lukelast.ktoon.fixtures.runFixtureTest
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonElement
import org.junit.jupiter.api.Disabled
import org.junit.jupiter.api.Test

/**
 * Tests from arrays-nested.json fixture - Nested and mixed array encoding: arrays of arrays, mixed
 * type arrays, root arrays.
 */
class ArraysNestedEncodeTest {
    private val fixture = "arrays-nested"

    @Test
    fun `encodes nested arrays of primitives`() {
        @Serializable data class Root(val pairs: List<List<String>>)

        runFixtureTest<Root>(fixture)
    }

    @Test
    fun `quotes strings containing delimiters in nested arrays`() {
        @Serializable data class Root(val pairs: List<List<String>>)

        runFixtureTest<Root>(fixture)
    }

    @Test
    fun `encodes empty inner arrays`() {
        @Serializable data class Root(val pairs: List<List<String>>)

        runFixtureTest<Root>(fixture)
    }

    @Test
    fun `encodes mixed-length inner arrays`() {
        @Serializable data class Root(val pairs: List<List<Int>>)

        runFixtureTest<Root>(fixture)
    }

    @Test
    @Disabled
    fun `encodes root-level primitive array`() {
        runFixtureTest<List<JsonElement>>(fixture)
    }

    @Test
    fun `encodes root-level array of uniform objects in tabular format`() {
        @Serializable data class Item(val id: Int)

        runFixtureTest<List<Item>>(fixture)
    }

    @Test
    fun `encodes root-level array of non-uniform objects in list format`() {
        @Serializable data class Item(val id: Int, val name: String? = null)

        runFixtureTest<List<Item>>(fixture)
    }

    @Test
    @Disabled
    fun `encodes root-level array mixing primitive, object, and array of objects in list format`() {
        runFixtureTest<List<JsonElement>>(fixture)
    }

    @Test
    fun `encodes root-level arrays of arrays`() {
        runFixtureTest<List<List<Int>>>(fixture)
    }

    @Test
    fun `encodes empty root-level array`() {
        runFixtureTest<List<String>>(fixture)
    }

    @Test
    fun `encodes complex nested structure`() {
        @Serializable
        data class User(
            val id: Int,
            val name: String,
            val tags: List<String>,
            val active: Boolean,
            val prefs: List<String>,
        )

        @Serializable data class Root(val user: User)

        runFixtureTest<Root>(fixture)
    }

    @Test
    @Disabled
    fun `uses list format for arrays mixing primitives and objects`() {
        @Serializable data class Root(val items: List<JsonElement>)

        runFixtureTest<Root>(fixture)
    }

    @Test
    @Disabled
    fun `uses list format for arrays mixing objects and arrays`() {
        @Serializable data class Root(val items: List<JsonElement>)

        runFixtureTest<Root>(fixture)
    }
}
