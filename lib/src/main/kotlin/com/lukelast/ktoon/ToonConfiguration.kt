package com.lukelast.ktoon

/**
 * Configuration for TOON format encoding and decoding.
 *
 * @property strictMode Enable strict validation of TOON format rules (default: true)
 * @property keyFolding Enable collapsing nested single-key objects into dotted notation (default: OFF)
 * @property flattenDepth Maximum depth for key folding (default: null, meaning Infinity)
 * @property pathExpansion Enable expanding dotted keys into nested structures when decoding
 *   (default: false)
 * @property delimiter Delimiter character for array values and tabular format (default: COMMA)
 * @property indentSize Number of spaces per indentation level (default: 2)
 */
data class ToonConfiguration(
    val strictMode: Boolean = true,
    val keyFolding: KeyFoldingMode = KeyFoldingMode.OFF,
    val flattenDepth: Int? = null,
    val pathExpansion: Boolean = false,
    val delimiter: Delimiter = Delimiter.COMMA,
    val indentSize: Int = 2,
) {
    init {
        require(indentSize > 0) { "indentSize must be positive, got $indentSize" }
        require(indentSize <= 16) { "indentSize must be <= 16, got $indentSize" }
        if (flattenDepth != null) {
            require(flattenDepth >= 0) { "flattenDepth must be non-negative, got $flattenDepth" }
        }
    }

    /** Delimiter character for separating values in inline arrays and tabular format. */
    enum class Delimiter(val char: Char, val displayName: String) {
        /** Comma delimiter (default) - most common and readable */
        COMMA(',', "comma"),

        /** Tab delimiter - useful when values may contain commas */
        TAB('\t', "tab"),

        /** Pipe delimiter - alternative when both commas and tabs might appear in values */
        PIPE('|', "pipe");

        override fun toString(): String = displayName
    }

    companion object {
        /** Default configuration with strict mode enabled and standard formatting. */
        val Default = ToonConfiguration()

        /**
         * Compact configuration optimized for minimal output size. Enables key folding for more
         * compact representation.
         */
        val Compact = ToonConfiguration(keyFolding = KeyFoldingMode.SAFE)
    }
}

/** Modes for key folding. */
enum class KeyFoldingMode {
    /** No folding is performed. */
    OFF,
    /** Fold eligible chains according to safe rules. */
    SAFE
}

/**
 * Builder function for creating ToonConfiguration with a DSL-style syntax.
 *
 * Example:
 * ```
 * val config = ToonConfiguration {
 *     strictMode = false
 *     keyFolding = KeyFoldingMode.SAFE
 *     indentSize = 4
 * }
 * ```
 */
inline fun ToonConfiguration(
    builderAction: ToonConfigurationBuilder.() -> Unit
): ToonConfiguration {
    return ToonConfigurationBuilder().apply(builderAction).build()
}

/** Builder class for constructing ToonConfiguration instances. */
class ToonConfigurationBuilder {
    var strictMode: Boolean = true
    var keyFolding: KeyFoldingMode = KeyFoldingMode.OFF
    var flattenDepth: Int? = null
    var pathExpansion: Boolean = false
    var delimiter: ToonConfiguration.Delimiter = ToonConfiguration.Delimiter.COMMA
    var indentSize: Int = 2

    fun build(): ToonConfiguration =
        ToonConfiguration(
            strictMode = strictMode,
            keyFolding = keyFolding,
            flattenDepth = flattenDepth,
            pathExpansion = pathExpansion,
            delimiter = delimiter,
            indentSize = indentSize,
        )
}
