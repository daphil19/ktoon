# ktoon [![](https://jitpack.io/v/lukelast/ktoon.svg)](https://jitpack.io/#lukelast/ktoon)

Kotlin serializer for TOON (Token-Oriented Object Notation).

For format details and motivation, see https://toonformat.dev/ and the authoritative spec at https://github.com/toon-format/spec.

## Add to your project (JitPack)

Check JitPack for versions and more installation instructions:
https://jitpack.io/#lukelast/ktoon

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

## Dependencies

* This library is built to target Java 17.
* You need kotlinx serialization which requires a build plugin.
  * https://github.com/Kotlin/kotlinx.serialization

## Demo project

Check out the demo project in the `demo` directory for more examples on how to use `Ktoon`.
[demo/README.md](demo/README.md))

## Development
See the [development guide](DEV.md) for how to do development.
