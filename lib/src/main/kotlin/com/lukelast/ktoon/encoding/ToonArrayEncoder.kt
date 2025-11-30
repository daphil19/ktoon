package com.lukelast.ktoon.encoding

import com.lukelast.ktoon.ToonConfiguration
import com.lukelast.ktoon.ToonEncodingException
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.descriptors.StructureKind
import kotlinx.serialization.encoding.AbstractEncoder
import kotlinx.serialization.encoding.CompositeEncoder
import kotlinx.serialization.modules.SerializersModule

/**
 * Encoder for TOON arrays in all three formats:
 * 1. Inline: `tags[3]: admin,ops,dev`
 * 2. Tabular: `users[2]{id,name}:\n 1,Alice\n 2,Bob`
 * 3. Expanded: `items[2]:\n - value1\n - value2`
 *
 * The format is selected automatically based on array content, or can be overridden via annotations
 * or configuration.
 *
 * @property writer Output writer
 * @property config Configuration
 * @property serializersModule Serializers module
 * @property descriptor Descriptor of the array
 * @property indentLevel Current indentation level
 * @property isRoot Whether this is a root-level array
 * @property key The key for this array (null if root)
 */
@OptIn(ExperimentalSerializationApi::class)
internal class ToonArrayEncoder(
    private val writer: ToonWriter,
    private val config: ToonConfiguration,
    override val serializersModule: SerializersModule,
    private val descriptor: SerialDescriptor,
    private val indentLevel: Int,
    private val isRoot: Boolean,
    private val key: String?,
) : AbstractEncoder() {

    private var elementIndex = 0
    private val elements = mutableListOf<EncodedElement>()
    private var elementDescriptor: SerialDescriptor? = null
    private var selectedFormat: ArrayFormatSelector.ArrayFormat? = null

    /** Represents an encoded array element (for buffering before format decision). */
    sealed class EncodedElement {
        data class Primitive(val value: String) : EncodedElement()

        data class Structure(
            val descriptor: SerialDescriptor,
            val values: List<Pair<String, EncodedElement>>,
        ) : EncodedElement()

        data class NestedArray(val elements: List<EncodedElement>) : EncodedElement()
    }

    /** Begins encoding an element in the array. */
    override fun encodeElement(descriptor: SerialDescriptor, index: Int): Boolean {
        elementIndex = index

        // Capture element descriptor for format decision
        if (elementDescriptor == null && descriptor.elementsCount > 0) {
            elementDescriptor = descriptor.getElementDescriptor(0)
        }

        return true
    }

    /** Encodes a null value. */
    override fun encodeNull() {
        elements.add(EncodedElement.Primitive("null"))
    }

    /** Encodes a boolean value. */
    override fun encodeBoolean(value: Boolean) {
        elements.add(EncodedElement.Primitive(if (value) "true" else "false"))
    }

    /** Encodes a byte value. */
    override fun encodeByte(value: Byte) {
        elements.add(EncodedElement.Primitive(NumberNormalizer.normalize(value)))
    }

    /** Encodes a short value. */
    override fun encodeShort(value: Short) {
        elements.add(EncodedElement.Primitive(NumberNormalizer.normalize(value)))
    }

    /** Encodes an int value. */
    override fun encodeInt(value: Int) {
        elements.add(EncodedElement.Primitive(NumberNormalizer.normalize(value)))
    }

    /** Encodes a long value. */
    override fun encodeLong(value: Long) {
        elements.add(EncodedElement.Primitive(NumberNormalizer.normalize(value)))
    }

    /** Encodes a float value. */
    override fun encodeFloat(value: Float) {
        elements.add(EncodedElement.Primitive(NumberNormalizer.normalize(value)))
    }

    /** Encodes a double value. */
    override fun encodeDouble(value: Double) {
        elements.add(EncodedElement.Primitive(NumberNormalizer.normalize(value)))
    }

    /** Encodes a char value. */
    override fun encodeChar(value: Char) {
        val quoted =
            StringQuoting.quote(
                value.toString(),
                StringQuoting.QuotingContext.ARRAY_ELEMENT,
                config.delimiter.char,
            )
        elements.add(EncodedElement.Primitive(quoted))
    }

    /** Encodes a string value. */
    override fun encodeString(value: String) {
        val quoted =
            StringQuoting.quote(
                value,
                StringQuoting.QuotingContext.ARRAY_ELEMENT,
                config.delimiter.char,
            )
        elements.add(EncodedElement.Primitive(quoted))
    }

    /** Encodes an enum value. */
    override fun encodeEnum(enumDescriptor: SerialDescriptor, index: Int) {
        val enumName = enumDescriptor.getElementName(index)
        val quoted =
            StringQuoting.quote(
                enumName,
                StringQuoting.QuotingContext.ARRAY_ELEMENT,
                config.delimiter.char,
            )
        elements.add(EncodedElement.Primitive(quoted))
    }

    /** Begins encoding a nested structure within the array. */
    override fun beginStructure(descriptor: SerialDescriptor): CompositeEncoder {
        // Store the element descriptor for format decision
        if (elementDescriptor == null) {
            elementDescriptor = descriptor
        }

        return when (descriptor.kind) {
            StructureKind.CLASS,
            StructureKind.OBJECT -> {
                // For objects, use a special encoder that captures field values
                TabularElementEncoder(
                    config = config,
                    serializersModule = serializersModule,
                    descriptor = descriptor,
                    onComplete = { values ->
                        elements.add(EncodedElement.Structure(descriptor, values))
                    },
                )
            }
            StructureKind.LIST -> {
                // For nested arrays, capture elements to handle formatting
                NestedArrayCapturer(
                    config = config,
                    serializersModule = serializersModule,
                    descriptor = descriptor,
                    onComplete = { nestedElements ->
                        elements.add(EncodedElement.NestedArray(nestedElements))
                    },
                )
            }
            else -> {
                // For other structures, fall back to expanded format
                // This is handled by writing directly in endStructure
                this
            }
        }
    }

    /** Ends encoding of the array and writes it in the selected format. */
    override fun endStructure(descriptor: SerialDescriptor) {
        // Select format based on collected elements
        val format = selectArrayFormat(elements)

        // Write array in the selected format
        when (format) {
            ArrayFormatSelector.ArrayFormat.INLINE -> writeInlineArray(elements, key)
            ArrayFormatSelector.ArrayFormat.TABULAR -> writeTabularArray(elements, key, indentLevel)
            ArrayFormatSelector.ArrayFormat.EXPANDED -> writeExpandedArray(elements, key, indentLevel)
        }
    }

    /** Selects the array format based on analyzed content. */
    private fun selectArrayFormat(elements: List<EncodedElement>): ArrayFormatSelector.ArrayFormat {
        // If format is already selected (e.g., by annotation), use it
        if (selectedFormat != null) {
            return selectedFormat!!
        }

        // Check if all elements are primitives
        val allPrimitive = elements.all { it is EncodedElement.Primitive }

        if (allPrimitive) {
            return ArrayFormatSelector.ArrayFormat.INLINE
        }

        // Check if all elements are structures with same descriptor and all primitive fields
        val allStructures = elements.all { it is EncodedElement.Structure }
        if (allStructures && elements.size >= 1) {
            val structures = elements.map { it as EncodedElement.Structure }
            val firstDescriptor = structures.first().descriptor
            val allSameDescriptor = structures.all { it.descriptor == firstDescriptor }
            val allPrimitiveFields = ArrayFormatSelector.allPropertiesArePrimitive(firstDescriptor)

            if (allSameDescriptor && allPrimitiveFields) {
                // Ensure all structures have the same keys (required for tabular format)
                val firstKeys = structures.first().values.map { it.first }.toSet()
                val allSameKeys = structures.all { structure ->
                    structure.values.map { it.first }.toSet() == firstKeys
                }

                if (allSameKeys) {
                    return ArrayFormatSelector.ArrayFormat.TABULAR
                }
            }
        }

        // Default to expanded format
        return ArrayFormatSelector.ArrayFormat.EXPANDED
    }

    /** Writes array in inline format: `key[N]: val1,val2,val3` */
    private fun writeInlineArray(elements: List<EncodedElement>, key: String?) {
        if (key != null) {
            writer.writeInlineArrayHeader(key, elements.size, config.delimiter.char)
        } else {
            // Root-level array - no key prefix
            writer.writeString("[")
            writer.writeString(elements.size.toString())
            if (config.delimiter.char != ',') {
                writer.writeString(config.delimiter.char.toString())
            }
            writer.writeString("]:")
        }
        
        if (elements.isNotEmpty()) {
            writer.writeSpace()
        }

        elements.forEachIndexed { index, element ->
            if (element is EncodedElement.Primitive) {
                writer.writeString(element.value)
                if (index < elements.size - 1) {
                    writer.writeDelimiter()
                }
            }
        }
    }

    /** Writes array in tabular format: `key[N]{field1,field2}:\n val1,val2\n val3,val4` */
    private fun writeTabularArray(elements: List<EncodedElement>, key: String?, indentLevel: Int) {
        if (elements.isEmpty()) {
            writeInlineArray(elements, key) // Empty arrays use inline format
            return
        }

        val structure =
            elements.first() as? EncodedElement.Structure
                ?: throw ToonEncodingException.unsupportedType("Non-structure in tabular array")

        // Get field names
        val fieldNames = ArrayFormatSelector.getFieldNames(structure.descriptor)
        
        // Quote field names
        val quotedFieldNames = fieldNames.map {
            StringQuoting.quote(it, StringQuoting.QuotingContext.OBJECT_KEY, config.delimiter.char)
        }

        // Write header: [N]{field1,field2}: or key[N]{field1,field2}:
        if (key != null) {
            writer.writeTabularArrayHeader(key, elements.size, quotedFieldNames, config.delimiter.char)
        } else {
            // Root-level array - no key prefix
            writer.writeString("[")
            writer.writeString(elements.size.toString())
            if (config.delimiter.char != ',') {
                writer.writeString(config.delimiter.char.toString())
            }
            writer.writeString("]{")
            writer.writeString(quotedFieldNames.joinToString(config.delimiter.char.toString()))
            writer.writeString("}:")
        }

        // Write each row
        elements.forEach { element ->
            if (element is EncodedElement.Structure) {
                writer.writeNewline()
                writer.writeIndent(indentLevel + 1)

                element.values.forEachIndexed { index, (_, value) ->
                    if (value is EncodedElement.Primitive) {
                        writer.writeString(value.value)
                    } else {
                        // Fallback for non-primitive in tabular (shouldn't happen if selectArrayFormat works)
                        writer.writeString(value.toString())
                    }
                    if (index < element.values.size - 1) {
                        writer.writeDelimiter()
                    }
                }
            }
        }
    }

    /** Writes array in expanded format: `key[N]:\n - val1\n - val2` */
    private fun writeExpandedArray(elements: List<EncodedElement>, key: String?, indentLevel: Int) {
        if (key != null) {
            writer.writeExpandedArrayHeader(key, elements.size, config.delimiter.char)
        } else {
            // Root-level array - no key prefix
            writer.writeString("[")
            writer.writeString(elements.size.toString())
            if (config.delimiter.char != ',') {
                writer.writeString(config.delimiter.char.toString())
            }
            writer.writeString("]:")
        }

        elements.forEach { element ->
            writer.writeNewline()
            writer.writeIndent(indentLevel + 1)
            writer.writeDash()
            writer.writeSpace()

            when (element) {
                is EncodedElement.Primitive -> {
                    writer.writeString(element.value)
                }
                is EncodedElement.Structure -> {
                    // Write object fields
                    element.values.forEachIndexed { index, (fieldName, value) ->
                        val quotedKey = StringQuoting.quote(
                            fieldName,
                            StringQuoting.QuotingContext.OBJECT_KEY,
                            config.delimiter.char
                        )
                        
                        if (index == 0) {
                            // First field: write on same line as dash
                        } else {
                            // Subsequent fields: new line, indent + 2
                            writer.writeNewline()
                            writer.writeIndent(indentLevel + 2)
                        }

                        when (value) {
                            is EncodedElement.Primitive -> {
                                writer.writeKeyValue(quotedKey, value.value)
                            }
                            is EncodedElement.NestedArray -> {
                                // Recursive write for nested array as field value
                                val format = selectArrayFormat(value.elements)
                                when (format) {
                                    ArrayFormatSelector.ArrayFormat.INLINE -> writeInlineArray(value.elements, quotedKey)
                                    ArrayFormatSelector.ArrayFormat.TABULAR -> writeTabularArray(value.elements, quotedKey, indentLevel + 2)
                                    ArrayFormatSelector.ArrayFormat.EXPANDED -> writeExpandedArray(value.elements, quotedKey, indentLevel + 2)
                                }
                            }
                            is EncodedElement.Structure -> {
                                // Nested object as field value
                                writer.writeKey(quotedKey)
                                writeStructure(value.values, indentLevel + 2)
                            }
                        }
                    }
                }
                is EncodedElement.NestedArray -> {
                    // Recursive write for nested array
                    val format = selectArrayFormat(element.elements)
                    when (format) {
                        ArrayFormatSelector.ArrayFormat.INLINE -> writeInlineArray(element.elements, null)
                        ArrayFormatSelector.ArrayFormat.TABULAR -> writeTabularArray(element.elements, null, indentLevel + 1)
                        ArrayFormatSelector.ArrayFormat.EXPANDED -> writeExpandedArray(element.elements, null, indentLevel + 1)
                    }
                }
            }
        }
    }

    private fun writeStructure(values: List<Pair<String, EncodedElement>>, indentLevel: Int) {
        if (values.isEmpty()) {
            writer.writeSpace()
            writer.writeString("{}")
            return
        }

        values.forEach { (fieldName, value) ->
            writer.writeNewline()
            writer.writeIndent(indentLevel + 1)
            
            val quotedKey = StringQuoting.quote(
                fieldName,
                StringQuoting.QuotingContext.OBJECT_KEY,
                config.delimiter.char
            )

            when (value) {
                is EncodedElement.Primitive -> {
                    writer.writeKeyValue(quotedKey, value.value)
                }
                is EncodedElement.NestedArray -> {
                    val format = selectArrayFormat(value.elements)
                    when (format) {
                        ArrayFormatSelector.ArrayFormat.INLINE -> writeInlineArray(value.elements, quotedKey)
                        ArrayFormatSelector.ArrayFormat.TABULAR -> writeTabularArray(value.elements, quotedKey, indentLevel + 1)
                        ArrayFormatSelector.ArrayFormat.EXPANDED -> writeExpandedArray(value.elements, quotedKey, indentLevel + 1)
                    }
                }
                is EncodedElement.Structure -> {
                    writer.writeKey(quotedKey)
                    writeStructure(value.values, indentLevel + 1)
                }
            }
        }
    }
}

/** Helper encoder for capturing tabular array element field values. */
@OptIn(ExperimentalSerializationApi::class)
private class TabularElementEncoder(
    private val config: ToonConfiguration,
    override val serializersModule: SerializersModule,
    private val descriptor: SerialDescriptor,
    private val onComplete: (List<Pair<String, ToonArrayEncoder.EncodedElement>>) -> Unit,
) : AbstractEncoder() {

    private val fieldValues = mutableListOf<Pair<String, ToonArrayEncoder.EncodedElement>>()
    private var currentIndex = -1

    override fun shouldEncodeElementDefault(descriptor: SerialDescriptor, index: Int): Boolean {
        return false
    }

    override fun encodeElement(descriptor: SerialDescriptor, index: Int): Boolean {
        currentIndex = index
        // Guard against serializers that try to encode indexes not present in the descriptor
        return index in 0 until descriptor.elementsCount
    }

    override fun encodeNull() {
        addField(ToonArrayEncoder.EncodedElement.Primitive("null"))
    }

    override fun encodeBoolean(value: Boolean) {
        addField(ToonArrayEncoder.EncodedElement.Primitive(if (value) "true" else "false"))
    }

    override fun encodeByte(value: Byte) {
        addField(ToonArrayEncoder.EncodedElement.Primitive(NumberNormalizer.normalize(value)))
    }

    override fun encodeShort(value: Short) {
        addField(ToonArrayEncoder.EncodedElement.Primitive(NumberNormalizer.normalize(value)))
    }

    override fun encodeInt(value: Int) {
        addField(ToonArrayEncoder.EncodedElement.Primitive(NumberNormalizer.normalize(value)))
    }

    override fun encodeLong(value: Long) {
        addField(ToonArrayEncoder.EncodedElement.Primitive(NumberNormalizer.normalize(value)))
    }

    override fun encodeFloat(value: Float) {
        addField(ToonArrayEncoder.EncodedElement.Primitive(NumberNormalizer.normalize(value)))
    }

    override fun encodeDouble(value: Double) {
        addField(ToonArrayEncoder.EncodedElement.Primitive(NumberNormalizer.normalize(value)))
    }

    override fun encodeChar(value: Char) {
        val quoted =
            StringQuoting.quote(
                value.toString(),
                StringQuoting.QuotingContext.ARRAY_ELEMENT,
                config.delimiter.char,
            )
        addField(ToonArrayEncoder.EncodedElement.Primitive(quoted))
    }

    override fun encodeString(value: String) {
        val quoted =
            StringQuoting.quote(
                value,
                StringQuoting.QuotingContext.ARRAY_ELEMENT,
                config.delimiter.char,
            )
        addField(ToonArrayEncoder.EncodedElement.Primitive(quoted))
    }

    override fun encodeEnum(enumDescriptor: SerialDescriptor, index: Int) {
        val enumName = enumDescriptor.getElementName(index)
        val quoted =
            StringQuoting.quote(
                enumName,
                StringQuoting.QuotingContext.ARRAY_ELEMENT,
                config.delimiter.char,
            )
        addField(ToonArrayEncoder.EncodedElement.Primitive(quoted))
    }

    private fun addField(value: ToonArrayEncoder.EncodedElement) {
        val fieldName =
            if (currentIndex in 0 until descriptor.elementsCount) {
                descriptor.getElementName(currentIndex)
            } else {
                // Fallback to avoid crashing on mismatched indexes
                "field$currentIndex"
            }
        fieldValues.add(fieldName to value)
    }

    override fun beginStructure(descriptor: SerialDescriptor): CompositeEncoder {
        return when (descriptor.kind) {
            StructureKind.LIST -> {
                NestedArrayCapturer(
                    config = config,
                    serializersModule = serializersModule,
                    descriptor = descriptor,
                    onComplete = { nestedElements ->
                        addField(ToonArrayEncoder.EncodedElement.NestedArray(nestedElements))
                    },
                )
            }
            StructureKind.CLASS,
            StructureKind.OBJECT -> {
                TabularElementEncoder(
                    config = config,
                    serializersModule = serializersModule,
                    descriptor = descriptor,
                    onComplete = { values ->
                        addField(ToonArrayEncoder.EncodedElement.Structure(descriptor, values))
                    },
                )
            }
            else -> this
        }
    }

    override fun endStructure(descriptor: SerialDescriptor) {
        onComplete(fieldValues)
    }
}

/** Helper encoder for capturing nested array elements. */
@OptIn(ExperimentalSerializationApi::class)
private class NestedArrayCapturer(
    private val config: ToonConfiguration,
    override val serializersModule: SerializersModule,
    private val descriptor: SerialDescriptor,
    private val onComplete: (List<ToonArrayEncoder.EncodedElement>) -> Unit,
) : AbstractEncoder() {

    private val elements = mutableListOf<ToonArrayEncoder.EncodedElement>()
    private var elementDescriptor: SerialDescriptor? = null

    override fun encodeElement(descriptor: SerialDescriptor, index: Int): Boolean {
        if (elementDescriptor == null && descriptor.elementsCount > 0) {
            elementDescriptor = descriptor.getElementDescriptor(0)
        }
        return true
    }

    override fun encodeNull() {
        elements.add(ToonArrayEncoder.EncodedElement.Primitive("null"))
    }

    override fun encodeBoolean(value: Boolean) {
        elements.add(ToonArrayEncoder.EncodedElement.Primitive(if (value) "true" else "false"))
    }

    override fun encodeByte(value: Byte) {
        elements.add(ToonArrayEncoder.EncodedElement.Primitive(NumberNormalizer.normalize(value)))
    }

    override fun encodeShort(value: Short) {
        elements.add(ToonArrayEncoder.EncodedElement.Primitive(NumberNormalizer.normalize(value)))
    }

    override fun encodeInt(value: Int) {
        elements.add(ToonArrayEncoder.EncodedElement.Primitive(NumberNormalizer.normalize(value)))
    }

    override fun encodeLong(value: Long) {
        elements.add(ToonArrayEncoder.EncodedElement.Primitive(NumberNormalizer.normalize(value)))
    }

    override fun encodeFloat(value: Float) {
        elements.add(ToonArrayEncoder.EncodedElement.Primitive(NumberNormalizer.normalize(value)))
    }

    override fun encodeDouble(value: Double) {
        elements.add(ToonArrayEncoder.EncodedElement.Primitive(NumberNormalizer.normalize(value)))
    }

    override fun encodeChar(value: Char) {
        val quoted =
            StringQuoting.quote(
                value.toString(),
                StringQuoting.QuotingContext.ARRAY_ELEMENT,
                config.delimiter.char,
            )
        elements.add(ToonArrayEncoder.EncodedElement.Primitive(quoted))
    }

    override fun encodeString(value: String) {
        val quoted =
            StringQuoting.quote(
                value,
                StringQuoting.QuotingContext.ARRAY_ELEMENT,
                config.delimiter.char,
            )
        elements.add(ToonArrayEncoder.EncodedElement.Primitive(quoted))
    }

    override fun encodeEnum(enumDescriptor: SerialDescriptor, index: Int) {
        val enumName = enumDescriptor.getElementName(index)
        val quoted =
            StringQuoting.quote(
                enumName,
                StringQuoting.QuotingContext.ARRAY_ELEMENT,
                config.delimiter.char,
            )
        elements.add(ToonArrayEncoder.EncodedElement.Primitive(quoted))
    }

    override fun beginStructure(descriptor: SerialDescriptor): CompositeEncoder {
        return when (descriptor.kind) {
            StructureKind.LIST -> {
                // Recursive nested array
                NestedArrayCapturer(
                    config = config,
                    serializersModule = serializersModule,
                    descriptor = descriptor,
                    onComplete = { nestedElements ->
                        elements.add(ToonArrayEncoder.EncodedElement.NestedArray(nestedElements))
                    },
                )
            }
            StructureKind.CLASS,
            StructureKind.OBJECT -> {
                // Nested object (tabular row candidate)
                TabularElementEncoder(
                    config = config,
                    serializersModule = serializersModule,
                    descriptor = descriptor,
                    onComplete = { values ->
                        elements.add(ToonArrayEncoder.EncodedElement.Structure(descriptor, values))
                    },
                )
            }
            else -> this
        }
    }

    override fun endStructure(descriptor: SerialDescriptor) {
        onComplete(elements)
    }
}
