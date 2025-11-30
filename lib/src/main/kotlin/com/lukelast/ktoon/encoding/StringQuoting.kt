package com.lukelast.ktoon.encoding

import com.lukelast.ktoon.ToonParsingException

/** Utility for quoting and unquoting strings according to TOON format rules. */
internal object StringQuoting {
    private val KEYWORDS = setOf("true", "false", "null")

    enum class QuotingContext {
        OBJECT_KEY,
        OBJECT_VALUE,
        ARRAY_ELEMENT,
    }

    fun needsQuoting(
        str: String,
        context: QuotingContext = QuotingContext.OBJECT_VALUE,
        delimiter: Char = ',',
    ): Boolean {
        if (str.isEmpty()) return true
        if (str in KEYWORDS) return true
        if (isNumber(str)) return true

        val len = str.length
        val first = str[0]
        if (first == '-') return true
        if (first <= ' ') return true // Starts with whitespace or control

        val last = str[len - 1]
        if (last <= ' ') return true // Ends with whitespace or control

        for (i in 0 until len) {
            val c = str[i]
            if (c < ' ') return true // Control char
            if (shouldQuoteChar(c)) return true
            if (
                (context == QuotingContext.OBJECT_VALUE ||
                    context == QuotingContext.ARRAY_ELEMENT) && c == delimiter
            ) {
                return true
            }
        }

        if (context == QuotingContext.OBJECT_KEY) {
            if (!isValidUnquotedKey(str)) return true
        }

        return false
    }

    private fun shouldQuoteChar(c: Char): Boolean {
        return c == '"' ||
            c == '\\' ||
            c == '\n' ||
            c == '\r' ||
            c == '\t' ||
            c == ':' ||
            c == '[' ||
            c == ']' ||
            c == '{' ||
            c == '}'
    }

    private fun isNumber(str: String): Boolean {
        if (str.isEmpty()) return false
        var i = 0
        val len = str.length
        if (str[i] == '-') {
            i++
            if (i == len) return false
        }

        var hasDot = false
        var hasExp = false
        var hasDigit = false

        while (i < len) {
            val c = str[i]
            if (c in '0'..'9') {
                hasDigit = true
            } else if (c == '.') {
                if (hasDot || hasExp) return false
                hasDot = true
            } else if (c == 'e' || c == 'E') {
                if (hasExp || !hasDigit) return false
                hasExp = true
                if (i + 1 < len && (str[i + 1] == '+' || str[i + 1] == '-')) {
                    i++
                }
                if (i + 1 == len) return false
                hasDigit = false
            } else {
                return false
            }
            i++
        }
        return hasDigit
    }

    private fun isValidUnquotedKey(str: String): Boolean {
        if (str.isEmpty()) return false
        val first = str[0]
        if (!isAlpha(first) && first != '_') return false
        for (i in 1 until str.length) {
            val c = str[i]
            if (!isAlpha(c) && !isDigit(c) && c != '_' && c != '.') return false
        }
        return true
    }

    private fun isAlpha(c: Char): Boolean = c in 'a'..'z' || c in 'A'..'Z'

    private fun isDigit(c: Char): Boolean = c in '0'..'9'

    fun quote(
        str: String,
        context: QuotingContext = QuotingContext.OBJECT_VALUE,
        delimiter: Char = ',',
    ): String {
        if (!needsQuoting(str, context, delimiter)) return str
        val len = str.length
        val sb = StringBuilder(len + 2)
        sb.append('"')
        for (i in 0 until len) {
            when (val c = str[i]) {
                '\\' -> sb.append("\\\\")
                '"' -> sb.append("\\\"")
                '\n' -> sb.append("\\n")
                '\r' -> sb.append("\\r")
                '\t' -> sb.append("\\t")
                else -> sb.append(c)
            }
        }
        sb.append('"')
        return sb.toString()
    }

    fun unquote(str: String, line: Int = -1, column: Int = -1): String {
        if (!str.startsWith('"')) return str
        if (!str.endsWith('"') || str.length < 2)
            throw ToonParsingException.unterminatedString(line, column)
        val content = str.substring(1, str.length - 1)
        if (content.indexOf('\\') == -1) return content

        val sb = StringBuilder(content.length)
        var i = 0
        val len = content.length
        while (i < len) {
            val c = content[i]
            if (c == '\\') {
                if (i + 1 >= len)
                    throw ToonParsingException.invalidEscapeSequence("\\", line, column + i)
                when (val next = content[i + 1]) {
                    '\\' -> sb.append('\\')
                    '"' -> sb.append('"')
                    'n' -> sb.append('\n')
                    'r' -> sb.append('\r')
                    't' -> sb.append('\t')
                    else ->
                        throw ToonParsingException.invalidEscapeSequence(
                            "\\$next",
                            line,
                            column + i,
                        )
                }
                i += 2
            } else {
                sb.append(c)
                i++
            }
        }
        return sb.toString()
    }
}
