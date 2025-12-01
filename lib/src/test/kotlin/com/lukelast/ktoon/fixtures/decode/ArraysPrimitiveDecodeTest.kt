package com.lukelast.ktoon.fixtures.decode

import com.lukelast.ktoon.fixtures.runDecodeFixtureTest
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonElement
import org.junit.jupiter.api.Test

/**
 * Tests from arrays-primitive.json fixture - Primitive array decoding: inline arrays of strings, numbers, booleans, quoted strings.
 */
class ArraysPrimitiveDecodeTest {

    private val fixture = "arrays-primitive"

    @Test
    fun `parses string arrays inline`() {
        @Serializable data class Tags(val tags: List<String>)
        runDecodeFixtureTest<Tags>(fixture)
    }

    @Test
    fun `parses number arrays inline`() {
        @Serializable data class Nums(val nums: List<Int>)
        runDecodeFixtureTest<Nums>(fixture)
    }

    @Test
    fun `parses mixed primitive arrays inline`() {
        @Serializable data class Data(val data: List<JsonElement>)
        runDecodeFixtureTest<Data>(fixture)
    }

    @Test
    fun `parses empty arrays`() {
        @Serializable data class Items(val items: List<String>)
        runDecodeFixtureTest<Items>(fixture)
    }

    @Test
    fun `parses single-item array with empty string`() {
        @Serializable data class Items(val items: List<String>)
        runDecodeFixtureTest<Items>(fixture)
    }

    @Test
    fun `parses multi-item array with empty string`() {
        @Serializable data class Items(val items: List<String>)
        runDecodeFixtureTest<Items>(fixture)
    }

    @Test
    fun `parses whitespace-only strings in arrays`() {
        @Serializable data class Items(val items: List<String>)
        runDecodeFixtureTest<Items>(fixture)
    }

    @Test
    fun `parses strings with delimiters in arrays`() {
        @Serializable data class Items(val items: List<String>)
        runDecodeFixtureTest<Items>(fixture)
    }

    @Test
    fun `parses strings that look like primitives when quoted`() {
        @Serializable data class Items(val items: List<String>)
        runDecodeFixtureTest<Items>(fixture)
    }

    @Test
    fun `parses strings with structural tokens in arrays`() {
        @Serializable data class Items(val items: List<String>)
        runDecodeFixtureTest<Items>(fixture)
    }

    @Test
    fun `parses quoted key with inline array`() {
        @Serializable data class MyKey(@SerialName("my-key") val myKey: List<Int>)
        runDecodeFixtureTest<MyKey>(fixture)
    }

    @Test
    fun `parses quoted key containing brackets with inline array`() {
        @Serializable data class CustomKey(@SerialName("key[test]") val keyTest: List<Int>)
        runDecodeFixtureTest<CustomKey>(fixture)
    }

    @Test
    fun `parses quoted key with empty array`() {
        @Serializable data class CustomKey(@SerialName("x-custom") val xCustom: List<String>)
        runDecodeFixtureTest<CustomKey>(fixture)
    }
}
