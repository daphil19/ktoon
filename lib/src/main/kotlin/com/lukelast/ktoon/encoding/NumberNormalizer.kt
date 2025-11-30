package com.lukelast.ktoon.encoding

import java.math.BigDecimal

/**
 * Utility for normalizing numbers to TOON canonical format.
 *
 * TOON number normalization rules:
 * - No scientific/exponent notation (1e6 → 1000000)
 * - No leading zeros except single "0"
 * - No trailing fractional zeros (1.5000 → 1.5)
 * - Integer representation when fractional part is zero (1.0 → 1)
 * - Negative zero normalizes to zero (-0 → 0)
 * - NaN and ±Infinity convert to null
 */
internal object NumberNormalizer {

    /**
     * Normalizes a Double value to TOON canonical decimal format.
     *
     * @param value The double value to normalize
     * @return The normalized string representation, or "null" for NaN/Infinity
     */
    fun normalize(value: Double): String {
        // Handle special values
        when {
            value.isNaN() -> return "null"
            value.isInfinite() -> return "null"
            value == 0.0 || value == -0.0 -> return "0"
        }

        // Convert to BigDecimal to avoid scientific notation and get precise representation
        val decimal =
            try {
                // Use BigDecimal constructor that takes double
                BigDecimal.valueOf(value)
            } catch (e: NumberFormatException) {
                // Fallback for edge cases
                return "null"
            }

        // Strip trailing zeros
        val stripped = decimal.stripTrailingZeros()

        // Get plain string (no scientific notation)
        val plainString = stripped.toPlainString()

        // If the result is "-0", normalize to "0"
        return if (plainString == "-0" || plainString == "-0.0") {
            "0"
        } else {
            plainString
        }
    }

    /**
     * Normalizes a Float value to TOON canonical decimal format.
     *
     * @param value The float value to normalize
     * @return The normalized string representation, or "null" for NaN/Infinity
     */
    fun normalize(value: Float): String {
        // Handle special values
        when {
            value.isNaN() -> return "null"
            value.isInfinite() -> return "null"
            value == 0.0f || value == -0.0f -> return "0"
        }

        // Convert to Double for consistent handling
        return normalize(value.toDouble())
    }

    /**
     * Normalizes a Long value to TOON canonical format. Longs are always representable as integers
     * without decimals.
     *
     * @param value The long value to normalize
     * @return The string representation of the long
     */
    fun normalize(value: Long): String {
        return value.toString()
    }

    /**
     * Normalizes an Int value to TOON canonical format. Ints are always representable as integers
     * without decimals.
     *
     * @param value The int value to normalize
     * @return The string representation of the int
     */
    fun normalize(value: Int): String {
        return value.toString()
    }

    /**
     * Normalizes a Short value to TOON canonical format.
     *
     * @param value The short value to normalize
     * @return The string representation of the short
     */
    fun normalize(value: Short): String {
        return value.toString()
    }

    /**
     * Normalizes a Byte value to TOON canonical format.
     *
     * @param value The byte value to normalize
     * @return The string representation of the byte
     */
    fun normalize(value: Byte): String {
        return value.toString()
    }

    /**
     * Normalizes a Number value (generic) to TOON canonical format. Handles various number types by
     * delegating to type-specific methods.
     *
     * @param value The number value to normalize
     * @return The normalized string representation
     */
    fun normalize(value: Number): String {
        return when (value) {
            is Int -> normalize(value)
            is Long -> normalize(value)
            is Double -> normalize(value)
            is Float -> normalize(value)
            is Short -> normalize(value)
            is Byte -> normalize(value)
            is BigDecimal -> normalizeBigDecimal(value)
            else -> {
                // Fallback for unknown number types
                // Try converting to double
                try {
                    normalize(value.toDouble())
                } catch (e: Exception) {
                    value.toString()
                }
            }
        }
    }

    /**
     * Normalizes a BigDecimal to TOON canonical format.
     *
     * @param value The BigDecimal value to normalize
     * @return The normalized string representation
     */
    private fun normalizeBigDecimal(value: BigDecimal): String {
        val stripped = value.stripTrailingZeros()
        val plainString = stripped.toPlainString()

        return if (plainString == "-0" || plainString == "-0.0") {
            "0"
        } else {
            plainString
        }
    }

    /**
     * Parses a normalized TOON number string back to a Double.
     *
     * @param str The string to parse
     * @return The parsed double value
     * @throws NumberFormatException if the string is not a valid number
     */
    fun parseDouble(str: String): Double {
        return when (str) {
            "null" -> Double.NaN
            else -> str.toDouble()
        }
    }

    /**
     * Parses a normalized TOON number string back to a Float.
     *
     * @param str The string to parse
     * @return The parsed float value
     * @throws NumberFormatException if the string is not a valid number
     */
    fun parseFloat(str: String): Float {
        return when (str) {
            "null" -> Float.NaN
            else -> str.toFloat()
        }
    }

    /**
     * Parses a normalized TOON number string back to a Long.
     *
     * @param str The string to parse
     * @return The parsed long value
     * @throws NumberFormatException if the string is not a valid integer
     */
    fun parseLong(str: String): Long {
        return str.toLong()
    }

    /**
     * Parses a normalized TOON number string back to an Int.
     *
     * @param str The string to parse
     * @return The parsed int value
     * @throws NumberFormatException if the string is not a valid integer
     */
    fun parseInt(str: String): Int {
        return str.toInt()
    }

    /**
     * Checks if a string represents a valid TOON number.
     *
     * @param str The string to check
     * @return true if the string is a valid TOON number
     */
    fun isValidNumber(str: String): Boolean {
        if (str == "null") return true
        if (str.isEmpty()) return false

        return try {
            str.toDouble()
            true
        } catch (e: NumberFormatException) {
            false
        }
    }
}
