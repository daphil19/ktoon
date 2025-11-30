# Development Guide

Use the Gradle wrapper (`./gradlew`) and JDK 17 to match the toolchain used by the library and demo.

## Testing
- `./gradlew test` — run all tests.
- `./gradlew clean`

## Benchmarks
- `./gradlew :lib:benchmark` — run all kotlinx-benchmark targets (the `bench` target) for the library.
- Reports: HTML/JSON under `lib/build/reports/benchmarks/`.

## Demo app
- `./gradlew :demo:run` — quick end-to-end check that encoding/decoding works in a runnable app.
