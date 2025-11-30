package com.lukelast.ktoon.encode01

import com.lukelast.ktoon.Ktoon
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class Encode01Test {
    @Test
    fun encode() {
        val data = Apple(variety = "Granny Smith", weight = 1.2)
        val encoded = Ktoon().encodeToString(data)
        val expected =
            """
            variety: Granny Smith
            weight: 1.2
            """
                .trimIndent()
        assertEquals(expected, encoded)
    }
}
