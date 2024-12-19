package com.kennycason.struktural

import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.ObjectMapper
import com.kennycason.struktural.yaml.YamlBackedValidator
import java.io.InputStream

/**
 * A transform class for using library
 */
object Struktural {
    private val jsonMissingValidator = JsonStructureValidator()
    private val jsonTypeValidator = JsonTypeValidator()
    private val jsonValueValidator = JsonValueValidator()
    private val yamlBackedValidator = YamlBackedValidator()

    fun setObjectMapper(objectMapper: ObjectMapper) {
        jsonMissingValidator.setObjectMapper(objectMapper)
        jsonTypeValidator.setObjectMapper(objectMapper)
        jsonValueValidator.setObjectMapper(objectMapper)
        yamlBackedValidator.setObjectMapper(objectMapper)
    }

    fun assertStructure(jsonString: String, fields: Iterable<Any>) = jsonMissingValidator.assert(jsonString, fields)
    fun assertStructure(json: JsonNode, fields: Iterable<Any>) = jsonMissingValidator.assert(json, fields)
    fun validateStructure(jsonString: String, fields: Iterable<Any>) = jsonMissingValidator.validate(jsonString, fields)
    fun validateStructure(json: JsonNode, fields: Iterable<Any>) = jsonMissingValidator.validate(json, fields)

    fun assertTypes(jsonString: String, fields: Iterable<Pair<String, Any>>) = jsonTypeValidator.assert(jsonString, fields)
    fun assertTypes(json: JsonNode, fields: Iterable<Pair<String, Any>>) = jsonTypeValidator.assert(json, fields)
    fun validateTypes(jsonString: String, fields: Iterable<Pair<String, Any>>) = jsonTypeValidator.validate(jsonString, fields)
    fun validateTypes(json: JsonNode, fields: Iterable<Pair<String, Any>>) = jsonTypeValidator.validate(json, fields)

    fun assertValues(jsonString: String, fields: Iterable<Pair<String, Any>>) = jsonValueValidator.assert(jsonString, fields)
    fun assertValues(json: JsonNode, fields: Iterable<Pair<String, Any>>) = jsonValueValidator.assert(json, fields)
    fun validateValues(jsonString: String, fields: Iterable<Pair<String, Any>>) = jsonValueValidator.validate(jsonString, fields)
    fun validateValues(json: JsonNode, fields: Iterable<Pair<String, Any>>) = jsonValueValidator.validate(json, fields)

    fun assertYaml(yamlString: String) = yamlBackedValidator.assert(yamlString)
    fun assertYaml(yamlInputStream: InputStream) = yamlBackedValidator.assert(yamlInputStream)
    fun validateYaml(yamlString: String) = yamlBackedValidator.validate(yamlString)
    fun validateYaml(yamlInputStream: InputStream) = yamlBackedValidator.validate(yamlInputStream)
}
