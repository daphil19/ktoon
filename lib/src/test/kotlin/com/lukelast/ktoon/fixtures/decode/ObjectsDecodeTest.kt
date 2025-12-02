package com.lukelast.ktoon.fixtures.decode

import com.lukelast.ktoon.fixtures.runDecodeFixtureTest
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import org.junit.jupiter.api.Test

/**
 * Tests from objects.json fixture - Object decoding: simple objects, nested objects, key parsing,
 * quoted values.
 */
class ObjectsDecodeTest {
    private val fixture = "objects"

    @Serializable data class WithText(val text: String)

    @Serializable data class WithStringValue(val v: String)

    @Test
    fun `parses objects with primitive values`() {
        @Serializable data class SimpleObject(val id: Int, val name: String, val active: Boolean)
        runDecodeFixtureTest<SimpleObject>(fixture)
    }

    @Test
    fun `parses null values in objects`() {
        @Serializable data class WithNull(val id: Int, val value: String?)
        runDecodeFixtureTest<WithNull>(fixture)
    }

    @Test
    fun `parses empty nested object header`() {
        @Serializable data class WithUser(val user: Map<String, String>)
        runDecodeFixtureTest<WithUser>(fixture)
    }

    @Serializable data class WithNote(val note: String)

    @Test
    fun `parses quoted object value with colon`() {
        runDecodeFixtureTest<WithNote>(fixture)
    }

    @Test
    fun `parses quoted object value with comma`() {
        runDecodeFixtureTest<WithNote>(fixture)
    }

    @Test
    fun `parses quoted object value with newline escape`() {
        runDecodeFixtureTest<WithText>(fixture)
    }

    @Test
    fun `parses quoted object value with escaped quotes`() {
        runDecodeFixtureTest<WithText>(fixture)
    }

    @Test
    fun `parses quoted object value with leading and trailing spaces`() {
        runDecodeFixtureTest<WithText>(
            fixture,
            "parses quoted object value with leading/trailing spaces",
        )
    }

    @Test
    fun `parses quoted object value with only spaces`() {
        runDecodeFixtureTest<WithText>(fixture)
    }

    @Test
    fun `parses quoted string value that looks like true`() {
        runDecodeFixtureTest<WithStringValue>(fixture)
    }

    @Test
    fun `parses quoted string value that looks like integer`() {
        runDecodeFixtureTest<WithStringValue>(fixture)
    }

    @Test
    fun `parses quoted string value that looks like negative decimal`() {
        runDecodeFixtureTest<WithStringValue>(fixture)
    }

    @Serializable data class WithQuotedKey(@SerialName("order:id") val orderId: Int)

    @Test
    fun `parses quoted key with colon`() {
        runDecodeFixtureTest<WithQuotedKey>(fixture)
    }

    @Test
    fun `parses quoted key with brackets`() {
        @Serializable data class WithBracketKey(@SerialName("[index]") val index: Int)
        runDecodeFixtureTest<WithBracketKey>(fixture)
    }

    @Test
    fun `parses quoted key with braces`() {
        @Serializable data class WithBraceKey(@SerialName("{key}") val key: Int)
        runDecodeFixtureTest<WithBraceKey>(fixture)
    }

    @Test
    fun `parses quoted key with comma`() {
        @Serializable data class WithCommaKey(@SerialName("a,b") val ab: Int)
        runDecodeFixtureTest<WithCommaKey>(fixture)
    }

    @Test
    fun `parses quoted key with spaces`() {
        @Serializable data class WithSpaceKey(@SerialName("full name") val fullName: String)
        runDecodeFixtureTest<WithSpaceKey>(fixture)
    }

    @Serializable data class WithHyphenKey(@SerialName("-lead") val lead: Int)

    @Test
    fun `parses quoted key with leading hyphen`() {
        runDecodeFixtureTest<WithHyphenKey>(fixture)
    }

    @Serializable data class WithPaddedKey(@SerialName(" a ") val a: Int)

    @Test
    fun `parses quoted key with leading and trailing spaces`() {
        runDecodeFixtureTest<WithPaddedKey>(fixture)
    }

    @Serializable data class WithNumericKey(@SerialName("123") val num: String)

    @Test
    fun `parses quoted numeric key`() {
        runDecodeFixtureTest<WithNumericKey>(fixture)
    }

    @Serializable data class WithEmptyKey(@SerialName("") val empty: Int)

    @Test
    fun `parses quoted empty string key`() {
        runDecodeFixtureTest<WithEmptyKey>(fixture)
    }

    @Test
    fun `parses dotted keys as identifiers`() {
        runDecodeFixtureTest<Map<String, String>>(fixture)
    }

    @Serializable data class WithUnderscore(val _private: Int)

    @Test
    fun `parses underscore-prefixed keys`() {
        runDecodeFixtureTest<WithUnderscore>(fixture)
    }

    @Test
    fun `parses underscore-containing keys`() {
        @Serializable data class WithUnderscoreContain(val user_name: Int)
        runDecodeFixtureTest<WithUnderscoreContain>(fixture)
    }

    @Test
    fun `unescapes newline in key`() {
        runDecodeFixtureTest<Map<String, Int>>(fixture)
    }

    @Test
    fun `unescapes tab in key`() {
        runDecodeFixtureTest<Map<String, Int>>(fixture)
    }

    @Test
    fun `unescapes quotes in key`() {
        runDecodeFixtureTest<Map<String, Int>>(fixture)
    }

    @Test
    fun `parses deeply nested objects with indentation`() {
        @Serializable data class NestedC(val c: String)
        @Serializable data class NestedB(val b: NestedC)
        @Serializable data class NestedDeep(val a: NestedB)
        runDecodeFixtureTest<NestedDeep>(fixture)
    }
}
