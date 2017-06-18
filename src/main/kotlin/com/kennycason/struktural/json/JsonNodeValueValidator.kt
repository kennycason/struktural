package com.kennycason.struktural.json

import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.node.JsonNodeType
import com.kennycason.struktural.Struktural
import com.kennycason.struktural.exception.InvalidInputException
import com.kennycason.struktural.exception.StrukturalException
import org.hamcrest.Matcher
import kotlin.reflect.KClass

/**
 * Created by kenny on 5/24/17.
 */
class JsonNodeValueValidator {

    fun validate(jsonNode: JsonNode, value: Any): Boolean {
        if (value is Matcher<*>) {
            return validateMatcher(jsonNode, value)
        }

        val jsonNodeType = jsonNode.nodeType!!
        when (jsonNodeType) {
            JsonNodeType.ARRAY -> {
                if (value is Array<*>) {
                    if (value.size != jsonNode.size()) { return false }
                    value.forEachIndexed { i, v ->
                        // recur since this function already handles node value comparison
                        if (!validate(jsonNode.get(i), v!!)) { return false }
                    }
                    return true
                }
            }
            JsonNodeType.BOOLEAN -> {
                if (value is Boolean) {
                    return value == jsonNode.asBoolean()
                }
            }
            JsonNodeType.NUMBER -> {
                if (value is Number) {
                    if (jsonNode.isInt) {
                        return value == jsonNode.intValue()
                    }
                    if (jsonNode.isLong) {
                        return value == jsonNode.longValue()
                    }
                    if (jsonNode.isFloat) {
                        return value == jsonNode.floatValue()
                    }
                    if (jsonNode.isDouble) {
                        return value == jsonNode.doubleValue()
                    }
                }
            }
            JsonNodeType.OBJECT -> {
                // TODO consider supporting this
                throw StrukturalException("Can not test equality for Json Objects")
            }
            JsonNodeType.STRING,
            JsonNodeType.BINARY -> {
                if (value is String) {
                    return jsonNode.asText() == value
                }
            }
            JsonNodeType.POJO,
            JsonNodeType.MISSING,
            JsonNodeType.NULL -> {
                return false
                // throw IllegalStateException("An illegal state occurred in Struktural. Unknown Json Node Type. Found $jsonNodeType")
            }
        }
        return false
    }

    private fun validateMatcher(jsonNode: JsonNode, matcher: Matcher<*>): Boolean {
        return matcher.matches(extractValue(jsonNode))
    }

    private fun extractValue(jsonNode: JsonNode): Any? =
            when (jsonNode.nodeType!!) {
                JsonNodeType.ARRAY -> throw StrukturalException("Can not use matchers to test equality for Json Arrays")
                JsonNodeType.BOOLEAN -> jsonNode.asBoolean()
                JsonNodeType.NUMBER ->
                    if (jsonNode.isInt) { jsonNode.intValue() }
                    else if (jsonNode.isLong) { jsonNode.longValue() }
                    else if (jsonNode.isFloat) { jsonNode.floatValue() }
                    else if (jsonNode.isDouble) { jsonNode.doubleValue() }
                    else { throw StrukturalException("Failed to parse Json Number as an Int, Short, Float, or Double") }
                JsonNodeType.OBJECT -> throw StrukturalException("Can not use matchers to test equality for Json Objects")
                JsonNodeType.STRING,
                JsonNodeType.BINARY -> jsonNode.asText()
                JsonNodeType.POJO,
                JsonNodeType.MISSING,
                JsonNodeType.NULL -> null
            }
}