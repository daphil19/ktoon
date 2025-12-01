package com.lukelast.ktoon.fixtures.decode

import com.lukelast.ktoon.fixtures.runDecodeFixtureTest
import org.junit.jupiter.api.Test

/**
 * Tests from primitives.json fixture - Primitive value decoding: strings, numbers, booleans, null, unescaping.
 */
class PrimitivesDecodeTest {

    private val fixture = "primitives"

    @Test
    fun `parses safe unquoted string`() {
        runDecodeFixtureTest<String>(fixture)
    }

    @Test
    fun `parses unquoted string with underscore and numbers`() {
        runDecodeFixtureTest<String>(fixture)
    }

    @Test
    fun `parses empty quoted string`() {
        runDecodeFixtureTest<String>(fixture)
    }

    @Test
    fun `parses quoted string with newline escape`() {
        runDecodeFixtureTest<String>(fixture)
    }

    @Test
    fun `parses quoted string with tab escape`() {
        runDecodeFixtureTest<String>(fixture)
    }

    @Test
    fun `parses quoted string with carriage return escape`() {
        runDecodeFixtureTest<String>(fixture)
    }

    @Test
    fun `parses quoted string with backslash escape`() {
        runDecodeFixtureTest<String>(fixture)
    }

    @Test
    fun `parses quoted string with escaped quotes`() {
        runDecodeFixtureTest<String>(fixture)
    }

    @Test
    fun `parses Unicode string`() {
        runDecodeFixtureTest<String>(fixture)
    }

    @Test
    fun `parses Chinese characters`() {
        runDecodeFixtureTest<String>(fixture)
    }

    @Test
    fun `parses emoji`() {
        runDecodeFixtureTest<String>(fixture)
    }

    @Test
    fun `parses string with emoji and spaces`() {
        runDecodeFixtureTest<String>(fixture)
    }

    @Test
    fun `parses positive integer`() {
        runDecodeFixtureTest<Int>(fixture)
    }

    @Test
    fun `parses decimal number`() {
        runDecodeFixtureTest<Double>(fixture)
    }

    @Test
    fun `parses negative integer`() {
        runDecodeFixtureTest<Int>(fixture)
    }

    @Test
    fun `parses true`() {
        runDecodeFixtureTest<Boolean>(fixture)
    }

    @Test
    fun `parses false`() {
        runDecodeFixtureTest<Boolean>(fixture)
    }

    @Test
    fun `parses null`() {
        runDecodeFixtureTest<String?>(fixture)
    }

    @Test
    fun `respects ambiguity quoting for true`() {
        runDecodeFixtureTest<String>(fixture)
    }

    @Test
    fun `respects ambiguity quoting for false`() {
        runDecodeFixtureTest<String>(fixture)
    }

    @Test
    fun `respects ambiguity quoting for null`() {
        runDecodeFixtureTest<String>(fixture)
    }

    @Test
    fun `respects ambiguity quoting for integer`() {
        runDecodeFixtureTest<String>(fixture)
    }

    @Test
    fun `respects ambiguity quoting for negative decimal`() {
        runDecodeFixtureTest<String>(fixture)
    }

    @Test
    fun `respects ambiguity quoting for scientific notation`() {
        runDecodeFixtureTest<String>(fixture)
    }

    @Test
    fun `respects ambiguity quoting for leading-zero`() {
        runDecodeFixtureTest<String>(fixture)
    }
}
