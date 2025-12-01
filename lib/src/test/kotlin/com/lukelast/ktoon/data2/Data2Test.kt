package com.lukelast.ktoon.data2

import com.lukelast.ktoon.KeyFoldingMode.SAFE
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
                TestData("test03", com.lukelast.ktoon.data2.test03.data),
                TestData("test04", com.lukelast.ktoon.data2.test04.data),
            )
            .map { testData -> dynamicTest("Test ${testData.name}") { doTest(testData) } }
}

data class TestData<T>(val name: String, val data: T, val ktoon: Ktoon = Ktoon.Default)

private inline fun <reified T> doTest(test: TestData<T>) {
    val jsonText = json.encodeToString(test.data)
    val jsonFile = buildPath(test.name, "data.json")
    jsonFile.writeText(jsonText)

    val toonFile = buildPath(test.name, "data.toon")
    if (!toonFile.isReadable()) {
        execToonCli(test, jsonFile, toonFile)
    }

    val expectedToonText = toonFile.readText()

    val encodedToonText = Ktoon().encodeToString(test.data)

    assertEquals(expectedToonText, encodedToonText)
}

private val json = Json { prettyPrint = true }

private fun buildPath(dir: String, fileName: String): Path {
    val basePath = Paths.get("src", "test", "kotlin")
    val packagePath = Paths.get(Data2Test::class.java.`package`.name.replace('.', '/'))
    val fullPath = basePath.resolve(packagePath).resolve(dir).resolve(fileName)
    return fullPath
}

private fun execToonCli(test: TestData<*>, jsonFile: Path, toonFile: Path) {
    val cmd =
        mutableListOf("cmd", "/c", "npx", "@toon-format/cli", jsonFile.name, "-o", toonFile.name)

    if (test.ktoon.configuration.keyFolding == SAFE) {
        cmd.add("--key-folding")
        cmd.add("safe")
    }

    val process =
        ProcessBuilder().command(cmd).directory(toonFile.parent.toFile()).inheritIO().start()
    process.waitFor()
}
