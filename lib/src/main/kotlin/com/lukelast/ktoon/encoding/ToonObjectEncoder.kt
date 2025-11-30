package com.lukelast.ktoon.encoding

import com.lukelast.ktoon.ToonConfiguration
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.SerializationStrategy
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.descriptors.StructureKind
import kotlinx.serialization.encoding.AbstractEncoder
import kotlinx.serialization.encoding.CompositeEncoder
import kotlinx.serialization.modules.SerializersModule

/**
 * Encoder for TOON objects (structures with named fields).
 *
 * Handles encoding of:
 * - Data classes
 * - Objects with properties
 * - Maps (as key-value pairs)
 * - Nested structures
 *
 * Example output:
 * ```
 * id: 1
 * name: Alice
 * address:
 *   city: NYC
 *   zip: 10001
 * ```
 *
 * @property writer Output writer
 * @property config Configuration
 * @property serializersModule Serializers module
 * @property descriptor Descriptor of the object being encoded
 * @property indentLevel Current indentation level
 * @property isRoot Whether this is the root object
 */
@OptIn(ExperimentalSerializationApi::class)
internal class ToonObjectEncoder(
    private val writer: ToonWriter,
    private val config: ToonConfiguration,
    override val serializersModule: SerializersModule,
    private val descriptor: SerialDescriptor,
    private val indentLevel: Int,
    private val isRoot: Boolean = false,
) : AbstractEncoder() {

    private var elementIndex = 0
    private var currentKey: String? = null

    /** Encodes the element index (field position) and stores the key name. */
    override fun encodeElement(descriptor: SerialDescriptor, index: Int): Boolean {
        elementIndex = index
        currentKey = descriptor.getElementName(index)

        // Write newline and indentation for non-root objects
        if (isRoot && elementIndex > 0) {
            writer.writeNewline()
        } else if (!isRoot) {
            writer.writeNewline()
        }

        writer.writeIndent(indentLevel)

        return true
    }

    /** Encodes a null value. */
    override fun encodeNull() {
        writeKeyAndValue("null")
    }

    /** Encodes a boolean value. */
    override fun encodeBoolean(value: Boolean) {
        writeKeyAndValue(if (value) "true" else "false")
    }

    /** Encodes a byte value. */
    override fun encodeByte(value: Byte) {
        writeKeyAndValue(NumberNormalizer.normalize(value))
    }

    /** Encodes a short value. */
    override fun encodeShort(value: Short) {
        writeKeyAndValue(NumberNormalizer.normalize(value))
    }

    /** Encodes an int value. */
    override fun encodeInt(value: Int) {
        writeKeyAndValue(NumberNormalizer.normalize(value))
    }

    /** Encodes a long value. */
    override fun encodeLong(value: Long) {
        writeKeyAndValue(NumberNormalizer.normalize(value))
    }

    /** Encodes a float value. */
    override fun encodeFloat(value: Float) {
        writeKeyAndValue(NumberNormalizer.normalize(value))
    }

    /** Encodes a double value. */
    override fun encodeDouble(value: Double) {
        writeKeyAndValue(NumberNormalizer.normalize(value))
    }

    /** Encodes a char value. */
    override fun encodeChar(value: Char) {
        val quoted =
            StringQuoting.quote(
                value.toString(),
                StringQuoting.QuotingContext.OBJECT_VALUE,
                config.delimiter.char,
            )
        writeKeyAndValue(quoted)
    }

    /** Encodes a string value. */
    override fun encodeString(value: String) {
        val quoted =
            StringQuoting.quote(
                value,
                StringQuoting.QuotingContext.OBJECT_VALUE,
                config.delimiter.char,
            )
        writeKeyAndValue(quoted)
    }

    /** Encodes an enum value. */
    override fun encodeEnum(enumDescriptor: SerialDescriptor, index: Int) {
        val enumName = enumDescriptor.getElementName(index)
        val quoted =
            StringQuoting.quote(
                enumName,
                StringQuoting.QuotingContext.OBJECT_VALUE,
                config.delimiter.char,
            )
        writeKeyAndValue(quoted)
    }

    /** Begins encoding a nested structure. */
    override fun beginStructure(descriptor: SerialDescriptor): CompositeEncoder {
        val key = currentKey ?: "value"

        return when (descriptor.kind) {
            StructureKind.CLASS,
            StructureKind.OBJECT -> {
                // Write the key and colon, then nested object on next lines
                writeKey(key)
                ToonObjectEncoder(
                    writer = writer,
                    config = config,
                    serializersModule = serializersModule,
                    descriptor = descriptor,
                    indentLevel = indentLevel + 1,
                    isRoot = false,
                )
            }
            StructureKind.LIST -> {
                // Write the key, then array on next lines
                ToonArrayEncoder(
                    writer = writer,
                    config = config,
                    serializersModule = serializersModule,
                    descriptor = descriptor,
                    indentLevel = indentLevel,
                    isRoot = false,
                    key = key,
                )
            }
            StructureKind.MAP -> {
                // Maps are encoded as nested objects
                writeKey(key)
                ToonObjectEncoder(
                    writer = writer,
                    config = config,
                    serializersModule = serializersModule,
                    descriptor = descriptor,
                    indentLevel = indentLevel + 1,
                    isRoot = false,
                )
            }
            else -> {
                // Inline structures
                this
            }
        }
    }

    /** Ends encoding of the structure. */
    override fun endStructure(descriptor: SerialDescriptor) {
        // No special action needed at end of object
    }

    /** Writes a key-value pair. */
    private fun writeKeyAndValue(value: String) {
        val key = currentKey ?: return
        val quotedKey =
            StringQuoting.quote(key, StringQuoting.QuotingContext.OBJECT_KEY, config.delimiter.char)
        writer.writeKeyValue(quotedKey, value)
    }

    /** Writes just the key with a colon (for nested structures). */
    private fun writeKey(key: String) {
        val quotedKey =
            StringQuoting.quote(key, StringQuoting.QuotingContext.OBJECT_KEY, config.delimiter.char)
        writer.writeKey(quotedKey)
    }

    /** Encodes a nullable serializable value. */
    override fun <T : Any> encodeNullableSerializableElement(
        descriptor: SerialDescriptor,
        index: Int,
        serializer: SerializationStrategy<T>,
        value: T?,
    ) {
        if (value == null) {
            encodeElement(descriptor, index)
            encodeNull()
        } else {
            super.encodeNullableSerializableElement(descriptor, index, serializer, value)
        }
    }
}
