package com.lukelast.ktoon.data1

import com.lukelast.ktoon.KeyFoldingMode.SAFE
import com.lukelast.ktoon.Ktoon
import kotlinx.serialization.ExperimentalSerializationApi
import java.nio.file.Path
import java.nio.file.Paths
import kotlin.io.path.isReadable
import kotlin.io.path.name
import kotlin.io.path.readText
import kotlin.io.path.writeText
import kotlinx.serialization.json.Json
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

abstract class Runner {
    @Test
    fun test() {
        run()
    }

    abstract fun run()

    open val ktoon = Ktoon.Default

    protected inline fun <reified T> doTest(data: T) {
        val jsonPath = buildPath("data.json")
        val toonPath = buildPath("data.toon")

        val dataToJsonText = jsonPretty.encodeToString(data)

        // Make sure data and json file are in sync.
        jsonPath.writeText(dataToJsonText)

        if (!toonPath.isReadable()) {
            execToonCli(jsonPath, toonPath)
        }
        val expectedToonText = toonPath.readText()

        val actualToonText = ktoon.encodeToString(data)

        assertEquals(expectedToonText, actualToonText)
    }

    fun execToonCli(json: Path, toon: Path) {
        val cmd = mutableListOf("cmd", "/c", "npx", "@toon-format/cli", json.name, "-o", toon.name)

        if (ktoon.configuration.keyFolding == SAFE) {
            cmd.add("--key-folding")
            cmd.add("safe")
        }

        val process =
            ProcessBuilder().command(cmd).directory(toon.parent.toFile()).inheritIO().start()
        process.waitFor()
    }

    fun buildPath(fileName: String): Path {
        val basePath = Paths.get("src", "test", "kotlin")
        val packagePath = Paths.get(this::class.java.`package`.name.replace('.', '/'))
        val fullPath = basePath.resolve(packagePath).resolve(fileName)
        return fullPath
    }
}

@OptIn(ExperimentalSerializationApi::class)
val jsonPretty = Json {
    prettyPrint = true
    prettyPrintIndent = "  "
}
