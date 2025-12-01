package com.lukelast.ktoon.fixtures

import com.lukelast.ktoon.Ktoon
import com.lukelast.ktoon.KtoonException
import com.lukelast.ktoon.fixtures.encode.ArraysNestedEncodeTest
import kotlinx.serialization.DeserializationStrategy
import kotlinx.serialization.SerializationStrategy
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.serializer
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.assertThrows

/**
 * Helper function to run a fixture test with a typed data class.
 *
 * @param fixtureName Fixture file name without path or extension (e.g., "primitives")
 * @param testName Name of the test case in the fixture
 * @param deserializer Deserializer for the input type
 * @param serializer Serializer for encoding with Ktoon
 */
fun <T> runFixtureTest(
    fixtureName: String,
    testName: String,
    deserializer: DeserializationStrategy<T>,
    serializer: SerializationStrategy<T>,
) {
    // Construct full fixture path
    val fixturePath = "fixtures/encode/$fixtureName.json"

    // Load fixture and find test case
    val fixture = loadFixture(fixturePath)
    val testCase =
        fixture.tests.find { it.name == testName }
            ?: error("Test case '$testName' not found in $fixturePath")

    // Deserialize input from JsonElement to typed data class
    val input = fixtureInputJson.decodeFromJsonElement(deserializer, testCase.input)

    // Create Ktoon with test options
    val config = testCase.options.toToonConfiguration()
    val ktoon = Ktoon(configuration = config)

    // Encode with Ktoon
    val encoded = ktoon.encodeToString(serializer, input)

    // Compare with expected
    val expected = testCase.expected.asString()
    assertEquals(
        expected,
        encoded,
        buildString {
            append("Test '$testName' failed")
            testCase.note?.let { append("\nNote: $it") }
            testCase.specSection?.let { append("\nSpec: §$it") }
        },
    )
}

inline fun <reified T> runFixtureTest(
    fixture: String,
    testName: String = currentFixtureTestName(),
) {
    runFixtureTest(fixture, testName, serializer<T>(), serializer<T>())
}

/**
 * Helper function to run a decode fixture test with a typed data class.
 *
 * @param fixtureName Fixture file name without path or extension (e.g., "primitives")
 * @param testName Name of the test case in the fixture
 * @param deserializer Deserializer for decoding from Ktoon
 * @param serializer Serializer for encoding to JSON
 */
fun <T> runDecodeFixtureTest(
    fixtureName: String,
    testName: String,
    deserializer: DeserializationStrategy<T>,
    serializer: SerializationStrategy<T>,
) {
    // Construct full fixture path
    val fixturePath = "fixtures/decode/$fixtureName.json"

    // Load fixture and find test case
    val fixture = loadFixture(fixturePath)
    val testCase =
        fixture.tests.find { it.name == testName }
            ?: error("Test case '$testName' not found in $fixturePath")

    // Create Ktoon with test options
    val config = testCase.options.toToonConfiguration()
    val ktoon = Ktoon(configuration = config)

    // Get TOON input string
    val toonInput = testCase.input.asString()

    if (testCase.shouldError) {
        // Test expects an error to be thrown
        assertThrows<KtoonException> {
            ktoon.decodeFromString(deserializer, toonInput)
        }
    } else {
        // Decode TOON to typed value
        val decoded = ktoon.decodeFromString(deserializer, toonInput)

        // Encode back to JSON for comparison
        val decodedJson = fixtureInputJson.encodeToJsonElement(serializer, decoded)

        // Compare with expected JSON
        assertEquals(
            testCase.expected,
            decodedJson,
            buildString {
                append("Test '$testName' failed")
                testCase.note?.let { append("\nNote: $it") }
                testCase.specSection?.let { append("\nSpec: §$it") }
            },
        )
    }
}

inline fun <reified T> runDecodeFixtureTest(
    fixture: String,
    testName: String = currentFixtureTestName(),
) {
    runDecodeFixtureTest(fixture, testName, serializer<T>(), serializer<T>())
}

fun currentFixtureTestName(): String {
    val encodePackage = "com.lukelast.ktoon.fixtures.encode"
    val decodePackage = "com.lukelast.ktoon.fixtures.decode"
    return Thread.currentThread()
        .stackTrace
        .firstOrNull {
            it.className.startsWith(encodePackage) ||
                it.className.startsWith(decodePackage)
        }
        ?.methodName
        ?: error(
            "Unable to determine fixture test name from stack trace; " +
                "ensure calls originate from $encodePackage or $decodePackage"
        )
}

/** JSON parser for deserializing fixture inputs. */
private val fixtureInputJson = Json {
    ignoreUnknownKeys = false
    isLenient = false
}

fun JsonElement.asString(): String {
    return (this as? JsonPrimitive)?.content
        ?: throw IllegalArgumentException("Expected string JsonElement, got $this")
}
