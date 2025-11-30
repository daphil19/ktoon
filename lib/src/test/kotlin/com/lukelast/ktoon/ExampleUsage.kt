package com.lukelast.ktoon

import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json

object Demo {
    @JvmStatic
    fun main(args: Array<String>) {
        val group =
            Group(
                name = "Developers",
                leader = Person(name = "Alice", age = 30),
                members =
                    listOf(
                        Person(name = "Bob", age = 25),
                        Person(name = "Charlie", age = 28),
                        Person(name = "Dr Frank", age = 89),
                    ),
            )

        println("JSON format:")
        val json = Json { prettyPrint = true }
        val jsonString = json.encodeToString(group)
        println(jsonString)

        val ktoon = Ktoon {
            strictMode = true
            keyFolding = false
            delimiter = ToonConfiguration.Delimiter.PIPE
            indentSize = 2
        }
        val ktoonString = ktoon.encodeToString(group)
        println("Encoded Ktoon format:")
        println(ktoonString)
    }
}

@Serializable data class Group(val name: String, val leader: Person, val members: List<Person>)

@Serializable data class Person(val name: String, val age: Int)
