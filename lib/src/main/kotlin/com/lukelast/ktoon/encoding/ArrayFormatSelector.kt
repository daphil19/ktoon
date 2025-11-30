package com.lukelast.ktoon.encoding

import com.lukelast.ktoon.encoding.ArrayFormatSelector.ArrayFormat.*
import com.lukelast.ktoon.encoding.ToonArrayEncoder.EncodedElement
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.descriptors.elementDescriptors

/** Utility for selecting the appropriate TOON array format based on content analysis. */
@OptIn(ExperimentalSerializationApi::class)
internal object ArrayFormatSelector {

    enum class ArrayFormat {
        INLINE,
        TABULAR,
        EXPANDED,
    }

    fun getFieldNames(descriptor: SerialDescriptor): List<String> =
        (0 until descriptor.elementsCount).map { descriptor.getElementName(it) }

    fun selectFormat(elements: List<EncodedElement>): ArrayFormat {
        if (elements.all { it is EncodedElement.Primitive }) {
            return INLINE
        }
        if (elements.any { it !is EncodedElement.Structure }) {
            return EXPANDED
        }
        val structures = elements.filterIsInstance<EncodedElement.Structure>()
        if (structures.isEmpty()) {
            return EXPANDED
        }
        val first = structures.first()
        if (structures.any { it.descriptor != first.descriptor }) {
            return EXPANDED
        }
        if (first.descriptor.elementDescriptors.any { it.kind !is PrimitiveKind }) {
            return EXPANDED
        }
        val firstFieldNames = first.values.map(Pair<String, EncodedElement>::first).toSet()
        if (
            structures.any {
                it.values.map(Pair<String, EncodedElement>::first).toSet() != firstFieldNames
            }
        ) {
            return EXPANDED
        }
        return TABULAR
    }
}
