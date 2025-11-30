plugins {
    alias(libs.plugins.kotlin.jvm)
    alias(libs.plugins.kotlin.serialization)
    alias(libs.plugins.kotlinx.benchmark)
    `java-library`
    `maven-publish`
}

group = "com.github.lukelast"

version = "1-SNAPSHOT"

repositories { mavenCentral() }

sourceSets {
    val main: SourceSet by getting
    val bench: SourceSet by creating {
        compileClasspath += main.output
        runtimeClasspath += main.output
    }
}

kotlin { target { compilations.getByName("bench").associateWith(compilations.getByName("main")) } }

benchmark { targets { register("bench") } }

dependencies {
    api(libs.kotlin.serialization)

    // Test dependencies
    testImplementation(kotlin("reflect"))
    testImplementation(libs.junit.jupiter)
    testRuntimeOnly(libs.junit.platform)
    testRuntimeOnly(libs.slf4j.simple)
    testImplementation(libs.instancio.junit)
    testImplementation(libs.jtoon)

    // Benchmark dependencies
    "benchImplementation"(libs.jtoon)
    "benchImplementation"(libs.instancio.junit)
    "benchImplementation"(libs.kotlin.serialization)
    "benchImplementation"(libs.kotlinx.benchmark.runtime)
}

tasks.test { useJUnitPlatform() }

java { withSourcesJar() }

publishing {
    publications {
        create<MavenPublication>("maven") {
            from(components["java"])
            artifactId = "ktoon"
        }
    }
}
