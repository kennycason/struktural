package com.kennycason.structural

import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.node.JsonNodeType
import com.kennycason.structural.error.Error
import com.kennycason.structural.error.ErrorType
import com.kennycason.structural.exception.InvalidInputException
import com.kennycason.structural.exception.StructuralException
import com.kennycason.structural.json.JsonNodeValueValidator
import kotlin.reflect.KClass

/**
 * Test fields not missing and field value.
 *
 */
class JsonValueValidator {
    private val objectMapper = ObjectMapper()
    private val jsonNodeValueValidator = JsonNodeValueValidator()

    fun assert(jsonString: String, fieldTypes: Iterable<Pair<String, Any>>) = assert(objectMapper.readTree(jsonString), fieldTypes)

    fun assert(json: JsonNode, fieldTypes: Iterable<Pair<String, Any>>) {
        val result = validate(json, fieldTypes)
        if (result.valid) { return }
        throw StructuralException(result.errors.joinToString("\n"))
    }

    fun validate(jsonString: String,
                fieldTypes: Iterable<Pair<String, Any>>) = assert(objectMapper.readTree(jsonString), fieldTypes)

    fun validate(json: JsonNode, fieldTypes: Iterable<Pair<String, Any>>): ValidationResult {
        // keep track of all errors
        val errors = mutableListOf<Error>()
        // keep track of the path for logging convenience
        val path = ""
        walkFields(json, fieldTypes, path, errors)

        return ValidationResult(errors.isEmpty(), errors)
    }

    private fun  walkFields(json: JsonNode,
                            fields: Iterable<Any>,
                            path: String,
                            errors: MutableList<Error>) {
        fields.forEach { field ->
            // could also be a Pair<String, Iterable<*>> which is a nested field
            // could be a a Pair<String, Any> which is a value to test equality
            if (field is Pair<*, *>) {
                // nest object, validate and recur
                validateNestedField(field)

                // set variables here so smart casting works later
                val fieldName = field.first!! as String
                val value = field.second!!

                if (!json.has(fieldName)) {
                    errors.add(Error(ErrorType.MISSING, "Field ${normalizeFieldPath(path, fieldName)} missing."))
                    return@forEach
                }

                val nestedJsonNode = json.get(fieldName)
                if (value is Iterable<*>) { // is nested object, recur
                    if (nestedJsonNode.isArray) {
                        throw StructuralException("Nested fields must be an Object. Found Array.")
                    }
                    walkFields(nestedJsonNode, value.requireNoNulls(), path + '/' + fieldName, errors)

                } else {
                    val jsonNode = json.get(fieldName)
                    if (!jsonNodeValueValidator.validate(jsonNode, value)) {
                        errors.add(Error(ErrorType.VALUE, "Field ${normalizeFieldPath(path, fieldName)} value did not equal expected value: $value, actual value : $nestedJsonNode"))
                    }
                }

            } else {
                throw InvalidInputException("Input must either be a Pair<String, *>, where * can be Iterable for nested objects, or Any type to test equality. Found ${field::class}")
            }
        }
    }

    private fun validateNestedField(field: Pair<*, *>) {
        val key = field.first
        val value = field.second
        if (key == null) {
            throw InvalidInputException("First value for nested input must be a String. found null")
        }
        if (value == null) {
            throw InvalidInputException("Second value for nested input must be a Iterable. found null")
        }
        // test structure of Pair<String, Any>
        if (key !is String) {
            throw InvalidInputException("First value for nested input must be a String. found ${key::class}")
        }
        // the value can be any type since it's comparing equality, so don't check
    }

    private fun normalizeFieldPath(path: String, field: String) = (path + '/' + field).replace(Regex("^/"), "")

}