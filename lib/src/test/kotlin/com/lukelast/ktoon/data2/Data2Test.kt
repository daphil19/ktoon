package com.lukelast.ktoon.data2

import com.lukelast.ktoon.Ktoon
import kotlinx.serialization.json.Json
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.DynamicTest.dynamicTest
import org.junit.jupiter.api.TestFactory
import java.nio.file.Path
import java.nio.file.Paths
import kotlin.io.path.isReadable
import kotlin.io.path.name
import kotlin.io.path.readText
import kotlin.io.path.writeText

class Data2Test {

    @TestFactory
    fun tests() =
        listOf(
                TestData("test01", com.lukelast.ktoon.data2.test01.data),
                TestData("test02", com.lukelast.ktoon.data2.test02.data),
            )
            .map { testData ->
                dynamicTest("Test ${testData.name}") {
                    doTest(testData)
                }
            }
}

private data class TestData<T>(val name: String, val data: T)

private inline fun <reified T> doTest(test: TestData<T>) {
    val jsonText = json.encodeToString(test.data)
    val jsonFile = buildPath(test.name, "data.json")
    jsonFile.writeText(jsonText)

    val toonFile = buildPath(test.name, "data.toon")
    if (!toonFile.isReadable()) {
        val process =
            ProcessBuilder()
                .command("cmd", "/c", "npx", "@toon-format/cli", jsonFile.name, "-o", toonFile.name)
                .directory(toonFile.parent.toFile())
                .inheritIO()
                .start()
        process.waitFor()
    }

    val expectedToonText = toonFile.readText()

    val encodedToonText = Ktoon().encodeToString(test.data)

    assertEquals(expectedToonText, encodedToonText)
}

val json = Json { prettyPrint = true }

private fun buildPath(dir: String, fileName: String): Path {
    val basePath = Paths.get("src", "test", "kotlin")
    val packagePath = Paths.get(Data2Test::class.java.`package`.name.replace('.', '/'))
    val fullPath = basePath.resolve(packagePath).resolve(dir).resolve(fileName)
    return fullPath
}
