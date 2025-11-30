plugins {
    alias(libs.plugins.kotlin.jvm)
    alias(libs.plugins.kotlin.serialization)
    `java-library`
    `maven-publish`
}

group = "com.github.lukelast"

version = "1-SNAPSHOT"

repositories { mavenCentral() }

dependencies {
    api(libs.kotlin.serialization)

    // Test dependencies
    testImplementation(kotlin("reflect"))
    testImplementation(libs.junit.jupiter)
    testRuntimeOnly(libs.junit.platform)
    testRuntimeOnly(libs.slf4j.simple)
    testImplementation(libs.instancio.junit)
    testImplementation(libs.jtoon)
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
