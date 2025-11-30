package com.lukelast.ktoon.encoding

import com.lukelast.ktoon.ToonConfiguration
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.descriptors.StructureKind
import kotlinx.serialization.encoding.AbstractEncoder
import kotlinx.serialization.encoding.CompositeEncoder
import kotlinx.serialization.modules.SerializersModule

/**
 * Root encoder for TOON format.
 *
 * This encoder handles encoding values at the root level and delegates to composite encoders for
 * structured types (objects and arrays).
 *
 * @property writer Output writer for TOON format
 * @property config Configuration for encoding behavior
 * @property serializersModule Module with contextual and polymorphic serializers
 */
@OptIn(ExperimentalSerializationApi::class)
internal class ToonEncoder(
    private val writer: ToonWriter,
    private val config: ToonConfiguration,
    override val serializersModule: SerializersModule,
) : AbstractEncoder() {

    /** Encodes a null value. */
    override fun encodeNull() {
        writer.writeLiteral("null")
    }

    /** Encodes a boolean value. */
    override fun encodeBoolean(value: Boolean) {
        writer.writeLiteral(if (value) "true" else "false")
    }

    /** Encodes a byte value. */
    override fun encodeByte(value: Byte) {
        writer.writeNumber(NumberNormalizer.normalize(value))
    }

    /** Encodes a short value. */
    override fun encodeShort(value: Short) {
        writer.writeNumber(NumberNormalizer.normalize(value))
    }

    /** Encodes an int value. */
    override fun encodeInt(value: Int) {
        writer.writeNumber(NumberNormalizer.normalize(value))
    }

    /** Encodes a long value. */
    override fun encodeLong(value: Long) {
        writer.writeNumber(NumberNormalizer.normalize(value))
    }

    /** Encodes a float value. */
    override fun encodeFloat(value: Float) {
        writer.writeNumber(NumberNormalizer.normalize(value))
    }

    /** Encodes a double value. */
    override fun encodeDouble(value: Double) {
        writer.writeNumber(NumberNormalizer.normalize(value))
    }

    /** Encodes a char value as a single-character string. */
    override fun encodeChar(value: Char) {
        val quoted =
            StringQuoting.quote(
                value.toString(),
                StringQuoting.QuotingContext.OBJECT_VALUE,
                config.delimiter.char,
            )
        writer.writeString(quoted)
    }

    /** Encodes a string value with proper quoting. */
    override fun encodeString(value: String) {
        val quoted =
            StringQuoting.quote(
                value,
                StringQuoting.QuotingContext.OBJECT_VALUE,
                config.delimiter.char,
            )
        writer.writeString(quoted)
    }

    /** Encodes an enum value as its string name. */
    override fun encodeEnum(enumDescriptor: SerialDescriptor, index: Int) {
        val enumName = enumDescriptor.getElementName(index)
        val quoted =
            StringQuoting.quote(
                enumName,
                StringQuoting.QuotingContext.OBJECT_VALUE,
                config.delimiter.char,
            )
        writer.writeString(quoted)
    }

    /**
     * Begins encoding a structure (object or array). Delegates to appropriate composite encoder
     * based on the structure kind.
     */
    override fun beginStructure(descriptor: SerialDescriptor): CompositeEncoder {
        return when (descriptor.kind) {
            StructureKind.CLASS,
            StructureKind.OBJECT -> {
                // Root-level object
                ToonObjectEncoder(
                    writer = writer,
                    config = config,
                    serializersModule = serializersModule,
                    descriptor = descriptor,
                    indentLevel = 0,
                    isRoot = true,
                )
            }
            StructureKind.LIST -> {
                // Root-level array - use expanded format
                ToonArrayEncoder(
                    writer = writer,
                    config = config,
                    serializersModule = serializersModule,
                    descriptor = descriptor,
                    indentLevel = 0,
                    isRoot = true,
                    key = null,
                )
            }
            StructureKind.MAP -> {
                // Maps are encoded as objects
                ToonObjectEncoder(
                    writer = writer,
                    config = config,
                    serializersModule = serializersModule,
                    descriptor = descriptor,
                    indentLevel = 0,
                    isRoot = true,
                )
            }
            else -> {
                // Fallback to this encoder for other types
                this
            }
        }
    }

    /** Ends encoding a structure. No-op for root encoder. */
    override fun endStructure(descriptor: SerialDescriptor) {
        // No-op for root level
    }
}
