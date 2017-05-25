package com.kennycason.structural.json

import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.node.JsonNodeType
import kotlin.reflect.KClass

/**
 * Created by kenny on 5/24/17.
 */
class JsonNodeTypeValidator {

    fun validate(jsonNode: JsonNode, type: KClass<*>): Boolean {
        val jsonNodeType = jsonNode.nodeType!!
        when (jsonNodeType) {
            JsonNodeType.ARRAY -> {
                if (type == Array<Any>::class) { return true }
            }
            JsonNodeType.BOOLEAN -> {
                if (type == Boolean::class) { return true }
            }
            JsonNodeType.NUMBER -> {
                // simple checks
                if (type == Number::class) { return true }
                if (jsonNode.isInt && type == Int::class) { return true }
                if (jsonNode.isLong && type == Long::class) { return true }
                if (jsonNode.isDouble && type == Double::class) { return true }

                // Jackson can't seem to default to double.
                // I guess that whether it's a double or float is not relevant.
                // TODO consider stronger assertions/checks
                if ((jsonNode.isDouble || jsonNode.isDouble)
                            && type == Float::class) { return true }

            }
            JsonNodeType.OBJECT -> {
                if (type == Any::class) { return true }
            }
            JsonNodeType.STRING,
            JsonNodeType.BINARY-> {
                if (type == String::class) { return true }
            }
            JsonNodeType.POJO,
            JsonNodeType.MISSING,
            JsonNodeType.NULL -> {
                throw IllegalStateException("An illegal state occurred in Structural. Unknown Json Node Type. Found $jsonNodeType")
            }
        }
        return false
    }

}