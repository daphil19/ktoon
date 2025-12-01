package com.lukelast.ktoon.fixtures.test

import com.lukelast.ktoon.fixtures.runFixtureTest
import kotlinx.serialization.Serializable
import org.junit.jupiter.api.Test

/**
 * Tests from primitives.json fixture - Primitive value encoding: strings, numbers, booleans, null.
 */
class PrimitivesEncodeTest {

    private val fixture = "primitives"

    @Test
    fun `encodes safe strings without quotes`() {
        runFixtureTest<String>(fixture)
    }

    @Test
    fun `encodes safe string with underscore and numbers`() {
        runFixtureTest<String>(fixture)
    }

    @Test
    fun `quotes empty string`() {
        runFixtureTest<String>(fixture)
    }

    @Test
    fun `quotes string that looks like true`() {
        runFixtureTest<String>(fixture)
    }

    @Test
    fun `quotes string that looks like false`() {
        runFixtureTest<String>(fixture)
    }

    @Test
    fun `quotes string that looks like null`() {
        runFixtureTest<String>(fixture)
    }

    @Test
    fun `quotes string that looks like integer`() {
        runFixtureTest<String>(fixture)
    }

    @Test
    fun `quotes string that looks like negative decimal`() {
        runFixtureTest<String>(fixture)
    }

    @Test
    fun `quotes string that looks like scientific notation`() {
        runFixtureTest<String>(fixture)
    }

    @Test
    fun `quotes string with leading zero`() {
        runFixtureTest<String>(fixture)
    }

    @Test
    fun `escapes newline in string`() {
        runFixtureTest<String>(fixture)
    }

    @Test
    fun `escapes tab in string`() {
        runFixtureTest<String>(fixture)
    }

    @Test
    fun `escapes carriage return in string`() {
        runFixtureTest<String>(fixture)
    }

    @Test
    fun `escapes backslash in string`() {
        runFixtureTest<String>(fixture)
    }

    @Test
    fun `quotes string with array-like syntax`() {
        runFixtureTest<String>(fixture)
    }

    @Test
    fun `quotes string starting with hyphen-space`() {
        runFixtureTest<String>(fixture)
    }

    @Test
    fun `quotes single hyphen as object value`() {
        @Serializable data class Marker(val marker: String)

        runFixtureTest<Marker>(fixture)
    }

    @Test
    fun `quotes string starting with hyphen as object value`() {
        @Serializable data class Note(val note: String)

        runFixtureTest<Note>(fixture)
    }

    @Test
    fun `quotes single hyphen in array`() {
        @Serializable data class Items(val items: List<String>)

        runFixtureTest<Items>(fixture)
    }

    @Test
    fun `quotes leading-hyphen string in array`() {
        @Serializable data class Tags(val tags: List<String>)

        runFixtureTest<Tags>(fixture)
    }

    @Test
    fun `quotes string with bracket notation`() {
        runFixtureTest<String>(fixture)
    }

    @Test
    fun `quotes string with brace notation`() {
        runFixtureTest<String>(fixture)
    }

    @Test
    fun `encodes Unicode string without quotes`() {
        runFixtureTest<String>(fixture)
    }

    @Test
    fun `encodes Chinese characters without quotes`() {
        runFixtureTest<String>(fixture)
    }

    @Test
    fun `encodes emoji without quotes`() {
        runFixtureTest<String>(fixture)
    }

    @Test
    fun `encodes string with emoji and spaces`() {
        runFixtureTest<String>(fixture)
    }

    // Number encoding tests

    @Test
    fun `encodes positive integer`() {
        runFixtureTest<Int>(fixture)
    }

    @Test
    fun `encodes decimal number`() {
        runFixtureTest<Double>(fixture)
    }

    @Test
    fun `encodes negative integer`() {
        runFixtureTest<Int>(fixture)
    }

    @Test
    fun `encodes zero`() {
        runFixtureTest<Int>(fixture)
    }

    @Test
    fun `encodes negative zero as zero`() {
        runFixtureTest<Int>(fixture)
    }

    @Test
    fun `encodes scientific notation as decimal`() {
        runFixtureTest<Int>(fixture)
    }

    @Test
    fun `encodes small decimal from scientific notation`() {
        runFixtureTest<Double>(fixture)
    }

    @Test
    fun `encodes large number`() {
        runFixtureTest<Double>(fixture)
    }

    @Test
    fun `encodes MAX_SAFE_INTEGER`() {
        runFixtureTest<Long>(fixture)
    }

    @Test
    fun `encodes repeating decimal with full precision`() {
        runFixtureTest<Double>(fixture)
    }

    // Boolean and null encoding tests

    @Test
    fun `encodes true`() {
        runFixtureTest<Boolean>(fixture)
    }

    @Test
    fun `encodes false`() {
        runFixtureTest<Boolean>(fixture)
    }

    @Test
    fun `encodes null`() {
        runFixtureTest<String?>(fixture)
    }
}
