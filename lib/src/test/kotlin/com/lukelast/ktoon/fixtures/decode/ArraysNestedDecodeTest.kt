package com.lukelast.ktoon.fixtures.decode

import com.lukelast.ktoon.fixtures.runDecodeFixtureTest
import kotlinx.serialization.Serializable
import org.junit.jupiter.api.Test

/**
 * Tests from arrays-nested.json fixture - Nested and mixed array decoding: list format, arrays of arrays, root arrays, mixed types.
 */
class ArraysNestedDecodeTest {

    private val fixture = "arrays-nested"

    @Serializable
    data class NestedItem(val id: Int, val name: String, val extra: Boolean? = null)

    @Serializable
    data class ItemsResult(val items: List<NestedItem>)

    @Test
    fun `parses list arrays for non-uniform objects`() {
        runDecodeFixtureTest<ItemsResult>(fixture)
    }

    @Test
    fun `parses list arrays with empty items`() {
        runDecodeFixtureTest<Map<String, List<Map<String, String>>>>(fixture)
    }

    @Serializable
    data class DeepProperties(val properties: Map<String, Map<String, String>>)

    @Serializable
    data class DeepItem(val id: Int? = null)

    @Test
    fun `parses list arrays with deeply nested objects`() {
        runDecodeFixtureTest<Map<String, List<Map<String, Map<String, String>>>>>(fixture)
    }

    @Serializable
    data class NestedProp(val x: Int)

    @Serializable
    data class ItemWithNested(val id: Int, val nested: NestedProp)

    @Serializable
    data class ItemWithNestedResult(val items: List<ItemWithNested>)

    @Test
    fun `parses list arrays containing objects with nested properties`() {
        runDecodeFixtureTest<ItemWithNestedResult>(fixture)
    }

    @Serializable
    data class User(val id: Int, val name: String)

    @Serializable
    data class ItemWithUsers(val users: List<User>, val status: String? = null)

    @Serializable
    data class ItemsWithUsersResult(val items: List<ItemWithUsers>)

    @Test
    fun `parses list items whose first field is a tabular array`() {
        runDecodeFixtureTest<ItemsWithUsersResult>(fixture)
    }

    @Test
    fun `parses single-field list-item object with tabular array`() {
        runDecodeFixtureTest<ItemsWithUsersResult>(fixture)
    }

    @Serializable
    data class ItemWithData(val name: String, val data: List<String>)

    @Serializable
    data class ItemsWithDataResult(val items: List<ItemWithData>)

    @Test
    fun `parses objects containing arrays (including empty arrays) in list format`() {
        runDecodeFixtureTest<ItemsWithDataResult>(fixture)
    }

    @Serializable
    data class MatrixItem(val matrix: List<List<Int>>, val name: String)

    @Serializable
    data class MatrixResult(val items: List<MatrixItem>)

    @Test
    fun `parses arrays of arrays within objects`() {
        runDecodeFixtureTest<MatrixResult>(fixture)
    }

    @Serializable
    data class PairsResult(val pairs: List<List<String>>)

    @Test
    fun `parses nested arrays of primitives`() {
        runDecodeFixtureTest<PairsResult>(fixture)
    }

    @Test
    fun `parses quoted strings and mixed lengths in nested arrays`() {
        runDecodeFixtureTest<PairsResult>(fixture)
    }

    @Serializable
    data class EmptyInnerResult(val pairs: List<List<String>>)

    @Test
    fun `parses empty inner arrays`() {
        runDecodeFixtureTest<EmptyInnerResult>(fixture)
    }

    @Serializable
    data class MixedLengthResult(val pairs: List<List<Int>>)

    @Test
    fun `parses mixed-length inner arrays`() {
        runDecodeFixtureTest<MixedLengthResult>(fixture)
    }

    @Test
    fun `parses root-level primitive array inline`() {
        runDecodeFixtureTest<List<Any>>(fixture)
    }

    @Serializable
    data class IdOnly(val id: Int)

    @Test
    fun `parses root-level array of uniform objects in tabular format`() {
        runDecodeFixtureTest<List<IdOnly>>(fixture)
    }

    @Serializable
    data class RootId(val id: Int, val name: String? = null)

    @Test
    fun `parses root-level array of non-uniform objects in list format`() {
        runDecodeFixtureTest<List<RootId>>(fixture)
    }

    @Test
    fun `parses root-level array mixing primitive object and array of objects in list format`() {
        runDecodeFixtureTest<List<Map<String, Int>>>(fixture)
    }

    @Test
    fun `parses root-level array of arrays`() {
        runDecodeFixtureTest<List<List<Int>>>(fixture)
    }

    @Test
    fun `parses empty root-level array`() {
        runDecodeFixtureTest<List<String>>(fixture)
    }

    @Serializable
    data class ComplexUser(
        val id: Int,
        val name: String,
        val tags: List<String>,
        val active: Boolean,
        val prefs: List<String>
    )

    @Serializable
    data class ComplexResult(val user: ComplexUser)

    @Test
    fun `parses complex mixed object with arrays and nested objects`() {
        runDecodeFixtureTest<ComplexResult>(fixture)
    }

    @Test
    fun `parses arrays mixing primitives, objects, and strings in list format`() {
        runDecodeFixtureTest<Map<String, List<Map<String, Int>>>>(fixture)
    }

    @Test
    fun `parses arrays mixing objects and arrays`() {
        runDecodeFixtureTest<Map<String, List<Map<String, Int>>>>(fixture)
    }

    @Serializable
    data class CustomItems(val `x-items`: List<RootId>)

    @Test
    fun `parses quoted key with list array format`() {
        runDecodeFixtureTest<CustomItems>(fixture)
    }
}
