package com.kennycason.struktural.json

import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.node.JsonNodeType
import com.kennycason.struktural.Struktural
import com.kennycason.struktural.exception.InvalidInputException
import com.kennycason.struktural.exception.StrukturalException
import kotlin.reflect.KClass

/**
 * Created by kenny on 5/24/17.
 */
class JsonNodeValueValidator {

    fun validate(jsonNode: JsonNode, value: Any): Boolean {
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
                    return jsonNode.asBoolean() == value
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
            JsonNodeType.BINARY-> {
                if (value is String) {
                    return jsonNode.asText() == value
                }
            }
            JsonNodeType.POJO,
            JsonNodeType.MISSING,
            JsonNodeType.NULL -> {
                throw IllegalStateException("An illegal state occurred in Struktural. Unknown Json Node Type. Found $jsonNodeType")
            }
        }
        return false
    }

}