package com.lukelast.ktoon.fixtures.decode

import com.lukelast.ktoon.fixtures.runDecodeFixtureTest
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import org.junit.jupiter.api.Test

/**
 * Tests from objects.json fixture - Object decoding: simple objects, nested objects, key parsing, quoted values.
 */
class ObjectsDecodeTest {

    private val fixture = "objects"

    @Serializable
    data class SimpleObject(val id: Int, val name: String, val active: Boolean)

    @Test
    fun `parses objects with primitive values`() {
        runDecodeFixtureTest<SimpleObject>(fixture)
    }

    @Serializable
    data class WithNull(val id: Int, val value: String?)

    @Test
    fun `parses null values in objects`() {
        runDecodeFixtureTest<WithNull>(fixture)
    }

    @Serializable
    data class WithUser(val user: Map<String, String>)

    @Test
    fun `parses empty nested object header`() {
        runDecodeFixtureTest<WithUser>(fixture)
    }

    @Serializable
    data class WithNote(val note: String)

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
        runDecodeFixtureTest<WithNote>(fixture)
    }

    @Serializable
    data class WithText(val text: String)

    @Test
    fun `parses quoted object value with escaped quotes`() {
        runDecodeFixtureTest<WithText>(fixture)
    }

    @Test
    fun `parses quoted object value with leading and trailing spaces`() {
        runDecodeFixtureTest<WithText>(fixture)
    }

    @Test
    fun `parses quoted object value with only spaces`() {
        runDecodeFixtureTest<WithText>(fixture)
    }

    @Serializable
    data class WithStringValue(val v: String)

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

    @Serializable
    data class WithQuotedKey(@SerialName("order:id") val orderId: Int)

    @Test
    fun `parses quoted key with colon`() {
        runDecodeFixtureTest<WithQuotedKey>(fixture)
    }

    @Serializable
    data class WithBracketKey(@SerialName("[index]") val index: Int)

    @Test
    fun `parses quoted key with brackets`() {
        runDecodeFixtureTest<WithBracketKey>(fixture)
    }

    @Serializable
    data class WithBraceKey(@SerialName("{key}") val key: Int)

    @Test
    fun `parses quoted key with braces`() {
        runDecodeFixtureTest<WithBraceKey>(fixture)
    }

    @Serializable
    data class WithCommaKey(@SerialName("a,b") val ab: Int)

    @Test
    fun `parses quoted key with comma`() {
        runDecodeFixtureTest<WithCommaKey>(fixture)
    }

    @Serializable
    data class WithSpaceKey(@SerialName("full name") val fullName: String)

    @Test
    fun `parses quoted key with spaces`() {
        runDecodeFixtureTest<WithSpaceKey>(fixture)
    }

    @Serializable
    data class WithHyphenKey(@SerialName("-lead") val lead: Int)

    @Test
    fun `parses quoted key with leading hyphen`() {
        runDecodeFixtureTest<WithHyphenKey>(fixture)
    }

    @Serializable
    data class WithPaddedKey(@SerialName(" a ") val a: Int)

    @Test
    fun `parses quoted key with leading and trailing spaces`() {
        runDecodeFixtureTest<WithPaddedKey>(fixture)
    }

    @Serializable
    data class WithNumericKey(@SerialName("123") val num: String)

    @Test
    fun `parses quoted numeric key`() {
        runDecodeFixtureTest<WithNumericKey>(fixture)
    }

    @Serializable
    data class WithEmptyKey(@SerialName("") val empty: Int)

    @Test
    fun `parses quoted empty string key`() {
        runDecodeFixtureTest<WithEmptyKey>(fixture)
    }

    @Test
    fun `parses dotted keys as identifiers`() {
        runDecodeFixtureTest<Map<String, String>>(fixture)
    }

    @Serializable
    data class WithUnderscore(val _private: Int)

    @Test
    fun `parses underscore-prefixed keys`() {
        runDecodeFixtureTest<WithUnderscore>(fixture)
    }

    @Serializable
    data class WithUnderscoreContain(val user_name: Int)

    @Test
    fun `parses underscore-containing keys`() {
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

    @Serializable
    data class NestedDeep(val a: NestedB)

    @Serializable
    data class NestedB(val b: NestedC)

    @Serializable
    data class NestedC(val c: String)

    @Test
    fun `parses deeply nested objects with indentation`() {
        runDecodeFixtureTest<NestedDeep>(fixture)
    }
}
