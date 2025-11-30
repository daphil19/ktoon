package com.lukelast.ktoon

import dev.toonformat.jtoon.JToon
import kotlinx.benchmark.*
import kotlinx.serialization.Serializable
import org.instancio.Instancio
import org.instancio.settings.Keys

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(BenchmarkTimeUnit.MICROSECONDS)
@Warmup(iterations = 8, time = 1, timeUnit = BenchmarkTimeUnit.SECONDS)
@Measurement(iterations = 5, time = 2, timeUnit = BenchmarkTimeUnit.SECONDS)
open class KtoonBenchmark {

    @Serializable
    data class BenchmarkData(
        val name: String,
        val id: Int,
        val items: List<String>,
        val nested: NestedData,
        val moreItems: List<Int>,
        val rows: List<Row>,
    )

    @Serializable data class Row(val id: Long, val name: String, val value: String)

    @Serializable
    data class NestedData(val description: String, val active: Boolean, val score: Double)

    private lateinit var data: BenchmarkData
    private val ktoon = Ktoon()

    @Setup
    fun setup() {
        data =
            Instancio.of(BenchmarkData::class.java)
                .withSeed(0)
                .withSetting(Keys.COLLECTION_MIN_SIZE, 100)
                .create()
        println(data)
    }

    @Benchmark
    fun benchmarkKtoon(): String {
        return ktoon.encodeToString(data)
    }

    @Benchmark
    fun benchmarkJtoon(): String {
        return JToon.encode(data)
    }
}
