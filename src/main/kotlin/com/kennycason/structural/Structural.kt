package com.kennycason.structural

import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.ObjectMapper

/**
 * A transform class for using library
 */
object Structural {
    private val jsonMissingValidator = JsonMissingValidator()
    private val jsonTypeValidator = JsonTypeValidator()
    private val jsonValueValidator = JsonValueValidator()

    fun assertStructure(jsonString: String, fields: List<Any>) = jsonMissingValidator.assert(jsonString, fields)
    fun assertStructure(json: JsonNode, fields: List<Any>) = jsonMissingValidator.assert(json, fields)
    fun validateStructure(jsonString: String, fields: List<Any>) = jsonMissingValidator.validate(jsonString, fields)
    fun validateStructure(json: JsonNode, fields: List<Any>) = jsonMissingValidator.validate(json, fields)

    fun assertTypes(jsonString: String, fields: Iterable<Pair<String, Any>>) = jsonTypeValidator.assert(jsonString, fields)
    fun assertTypes(json: JsonNode, fields: Iterable<Pair<String, Any>>) = jsonTypeValidator.assert(json, fields)
    fun validateTypes(jsonString: String, fields: Iterable<Pair<String, Any>>) = jsonTypeValidator.validate(jsonString, fields)
    fun validateTypes(json: JsonNode, fields: Iterable<Pair<String, Any>>) = jsonTypeValidator.validate(json, fields)

    fun assertValues(jsonString: String, fields: Iterable<Pair<String, Any>>) = jsonValueValidator.assert(jsonString, fields)
    fun assertValues(json: JsonNode, fields: Iterable<Pair<String, Any>>) = jsonValueValidator.assert(json, fields)
    fun validateValues(jsonString: String, fields: Iterable<Pair<String, Any>>) = jsonValueValidator.validate(jsonString, fields)
    fun validateValues(json: JsonNode, fields: Iterable<Pair<String, Any>>) = jsonValueValidator.validate(json, fields)

}