package com.lukelast.ktoon.data2.test06

import com.lukelast.ktoon.Ktoon
import com.lukelast.ktoon.data2.TestData
import kotlinx.serialization.Serializable

val test06 =
    TestData(
        name = "test06",
        data = Root(Field1(Field2("value"))),
        ktoon = Ktoon { keyFoldingSafe() },
    )

@Serializable data class Root(val field1: Field1)

@Serializable data class Field1(val field2: Field2)

@Serializable data class Field2(val field3: String)
