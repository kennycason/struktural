package com.kennycason.structural

import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.ObjectMapper
import com.kennycason.structural.error.ErrorType
import com.kennycason.structural.error.Error
import com.kennycason.structural.exception.InvalidInputException
import com.kennycason.structural.exception.StructuralException

/**
 * Test fields not missing
 */
class JsonMissingValidator {
    private val objectMapper = ObjectMapper()

    fun assert(jsonString: String, fields: Iterable<Any>) = assert(objectMapper.readTree(jsonString), fields)

    fun assert(json: JsonNode, fields: Iterable<Any>) {
        val result = validate(json, fields)
        if (result.valid) { return }
        throw StructuralException(result.errors.joinToString("\n"))
    }

    fun validate(jsonString: String, fields: Iterable<Any>) = validate(objectMapper.readTree(jsonString), fields)

    fun validate(json: JsonNode, fields: Iterable<Any>): ValidationResult {
        // keep track of all errors
        val errors = mutableListOf<Error>()
        // keep track of the path for logging convenience
        val path = ""
        walkFields(json, fields, path, errors)

        return ValidationResult(errors.isEmpty(), errors)
    }

    private fun  walkFields(json: JsonNode, fields: Iterable<Any>, path: String, errors: MutableList<Error>) {
        fields.forEach { field ->
            if (field is String) {
                if (!json.has(field)) {
                    errors.add(Error(ErrorType.MISSING, "Field ${normalizeFieldPath(path, field)} missing."))
                }

            } else if (field is Pair<*, *>) {
                // nest object, validate and recur
                validateNestedField(field)
                val fieldName = field.first!! as String
                val nestedFields = field.second as Iterable<*>
                if (!json.has(fieldName)) {
                    errors.add(Error(ErrorType.MISSING, "Field ${normalizeFieldPath(path, fieldName)} missing."))
                    return@forEach
                }

                val nestedJsonNode = json.get(fieldName)
                if (nestedJsonNode.isArray) { // walk over each item in the array and apply the nested checks
                    nestedJsonNode.forEach { node ->
                        walkFields(node, nestedFields.requireNoNulls(), path + '/' + fieldName, errors)
                    }
                } else { // is nested object, recur
                    walkFields(nestedJsonNode, nestedFields.requireNoNulls(), path + '/' + fieldName, errors)
                }
            } else {
                throw InvalidInputException("Input must either be a String field name, or a Iterable of fields. Found ${field::class}")
            }
        }
    }

    private fun validateNestedField(field: Pair<*, *>) {
        if (field.first == null) {
            throw InvalidInputException("First value for nested input must be a String. found null")
        }
        if (field.second == null) {
            throw InvalidInputException("Second value for nested input must be a Iterable. found null")
        }
        // test structure of Pair<String, Any>
        if (field.first !is String) {
            throw InvalidInputException("First value for nested input must be a String. found ${field.first!!::class}")
        }
        if (field.second !is Iterable<*>) {
            throw InvalidInputException("First value for nested input must be a Iterable. found ${field.second!!::class}")
        }
    }

    private fun normalizeFieldPath(path: String, field: String) = (path + '/' + field).replace(Regex("^/"), "")

}