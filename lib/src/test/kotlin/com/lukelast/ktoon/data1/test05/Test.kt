package com.lukelast.ktoon.data1.test05

import com.lukelast.ktoon.data1.Runner
import com.lukelast.ktoon.data2.Garage

class Test05 : Runner() {
    override fun run() = doTest(data)
}

private val data = Garage(owner = "", location = "", capacity = 0, inventory = listOf())
