# ktoon [![](https://jitpack.io/v/lukelast/ktoon.svg)](https://jitpack.io/#lukelast/ktoon)

Kotlin serializer for TOON (Token-Oriented Object Notation). For format details and motivation, see https://toonformat.dev/ and the authoritative spec at https://github.com/toon-format/spec.

## Add to your project (JitPack)
Using the Gradle Kotlin DSL:
```kotlin
// settings.gradle.kts
dependencyResolutionManagement {
    repositories {
        mavenCentral()
        maven(url = "https://jitpack.io")
    }
}
```
```kotlin
// build.gradle.kts
dependencies {
    implementation("com.github.lukelast:ktoon:VERSION")
}
```

## Basic usage
```kotlin
import com.lukelast.ktoon.Ktoon
import kotlinx.serialization.Serializable

@Serializable
data class User(val id: Int, val name: String)

fun main() {
    val encoded = Ktoon.Default.encodeToString(User(1, "Alice"))
    println(encoded)
}
```
