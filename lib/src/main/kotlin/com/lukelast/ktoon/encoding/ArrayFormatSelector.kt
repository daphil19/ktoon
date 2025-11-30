package com.lukelast.ktoon.encoding

import com.lukelast.ktoon.ToonConfiguration
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.descriptors.StructureKind

/**
 * Utility for selecting the appropriate TOON array format based on content analysis.
 *
 * TOON supports three array formats:
 * 1. **Inline**: `tags[3]: admin,ops,dev` - for primitive arrays
 * 2. **Tabular**: `users[2]{id,name}:\n 1,Alice\n 2,Bob` - for uniform objects
 * 3. **Expanded**: `items[2]:\n - value1\n - value2` - for mixed/nested content
 *
 * Format selection uses automatic heuristics by default, with support for manual override via
 * annotations or configuration.
 */
@OptIn(ExperimentalSerializationApi::class)
internal object ArrayFormatSelector {

    /**
     * Maximum line length for inline array format. Arrays longer than this will use expanded format
     * even if all elements are primitive.
     */
    private const val MAX_INLINE_LENGTH = 80

    /**
     * Minimum number of elements required for tabular format. Single-element arrays are clearer in
     * expanded format.
     */
    private const val MIN_TABULAR_ELEMENTS = 2

    /** TOON array format types. */
    enum class ArrayFormat {
        /** Inline format: all values on one line separated by delimiter */
        INLINE,

        /** Tabular format: uniform objects with fields as columns */
        TABULAR,

        /** Expanded format: each element on its own line with dash prefix */
        EXPANDED,
    }

    /**
     * Selects the appropriate array format based on descriptor analysis.
     *
     * Decision logic:
     * - **Inline**: All elements primitive + no delimiter conflicts + estimated length ≤ 80 chars
     * - **Tabular**: All elements objects + same structure + all properties primitive + count ≥ 2
     * - **Expanded**: Everything else (default/fallback)
     *
     * @param elementDescriptor Descriptor of array element type
     * @param estimatedCount Estimated number of elements (may be unknown)
     * @param config Configuration (for delimiter check)
     * @return Selected array format
     */
    fun selectFormat(
        elementDescriptor: SerialDescriptor,
        estimatedCount: Int = -1,
        config: ToonConfiguration,
    ): ArrayFormat {
        // Check for inline format (primitives only)
        if (isPrimitiveType(elementDescriptor)) {
            // Primitive arrays can use inline format if not too long
            // Estimate: ~10 chars per element average
            val estimatedLength = if (estimatedCount > 0) estimatedCount * 10 else 0
            return if (estimatedLength <= MAX_INLINE_LENGTH) {
                ArrayFormat.INLINE
            } else {
                ArrayFormat.EXPANDED
            }
        }

        // Check for tabular format (uniform objects)
        if (isTabularCandidate(elementDescriptor, estimatedCount)) {
            return ArrayFormat.TABULAR
        }

        // Default to expanded format
        return ArrayFormat.EXPANDED
    }

    /** Checks if a descriptor represents a primitive type suitable for inline arrays. */
    private fun isPrimitiveType(descriptor: SerialDescriptor): Boolean {
        return when (descriptor.kind) {
            PrimitiveKind.BOOLEAN,
            PrimitiveKind.BYTE,
            PrimitiveKind.SHORT,
            PrimitiveKind.INT,
            PrimitiveKind.LONG,
            PrimitiveKind.FLOAT,
            PrimitiveKind.DOUBLE,
            PrimitiveKind.CHAR,
            PrimitiveKind.STRING -> true
            else -> false
        }
    }

    /**
     * Checks if a descriptor is a candidate for tabular format.
     *
     * Requirements:
     * - Element is a structured type (CLASS/OBJECT)
     * - Has at least one property
     * - All properties are primitive types
     * - Array has at least MIN_TABULAR_ELEMENTS elements
     */
    private fun isTabularCandidate(descriptor: SerialDescriptor, estimatedCount: Int): Boolean {
        // Need at least 2 elements for tabular to be worthwhile
        if (estimatedCount in 0 until MIN_TABULAR_ELEMENTS) {
            return false
        }

        // Must be a class/object structure
        if (descriptor.kind != StructureKind.CLASS && descriptor.kind != StructureKind.OBJECT) {
            return false
        }

        // Must have at least one property
        val elementCount = descriptor.elementsCount
        if (elementCount == 0) {
            return false
        }

        // All properties must be primitive types
        for (i in 0 until elementCount) {
            val fieldDescriptor = descriptor.getElementDescriptor(i)
            if (!isPrimitiveType(fieldDescriptor)) {
                return false
            }
        }

        return true
    }

    /**
     * Extracts field names from a descriptor for tabular format header.
     *
     * @param descriptor Descriptor of the object
     * @return List of field names
     */
    fun getFieldNames(descriptor: SerialDescriptor): List<String> {
        val count = descriptor.elementsCount
        return (0 until count).map { descriptor.getElementName(it) }
    }

    /**
     * Checks if all properties of a descriptor are primitive. Used during encoding to verify
     * tabular format assumptions.
     */
    fun allPropertiesArePrimitive(descriptor: SerialDescriptor): Boolean {
        val count = descriptor.elementsCount
        for (i in 0 until count) {
            val fieldDescriptor = descriptor.getElementDescriptor(i)
            if (!isPrimitiveType(fieldDescriptor)) {
                return false
            }
        }
        return true
    }

    /**
     * Estimates the inline length of primitive values for format selection. This is a rough
     * estimate used during pre-encoding analysis.
     *
     * @param descriptor Element descriptor
     * @param count Number of elements
     * @param delimiter Delimiter character
     * @return Estimated character count
     */
    fun estimateInlineLength(descriptor: SerialDescriptor, count: Int, delimiter: Char): Int {
        // Rough estimates per type
        val avgElementLength =
            when (descriptor.kind) {
                PrimitiveKind.BOOLEAN -> 5 // "true" or "false"
                PrimitiveKind.BYTE,
                PrimitiveKind.SHORT -> 4 // "-128" to "127"
                PrimitiveKind.INT -> 8 // average integer
                PrimitiveKind.LONG -> 12 // average long
                PrimitiveKind.FLOAT,
                PrimitiveKind.DOUBLE -> 10 // average decimal
                PrimitiveKind.CHAR -> 3 // "'x'" or escaped
                PrimitiveKind.STRING -> 15 // average string length (wild guess)
                else -> 10 // fallback
            }

        // Account for delimiters between elements
        return (count * avgElementLength) + ((count - 1) * 1) // 1 char per delimiter
    }
}
