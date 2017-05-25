package com.kennycason.structural

import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.node.JsonNodeType
import com.kennycason.structural.error.Error
import com.kennycason.structural.error.ErrorType
import com.kennycason.structural.exception.InvalidInputException
import com.kennycason.structural.exception.StructuralException
import com.kennycason.structural.json.JsonNodeTypeValidator
import kotlin.reflect.KClass

/**
 * Test fields not missing and field types.
 *
 */
class JsonTypeValidator {
    private val objectMapper = ObjectMapper()
    private val jsonNodeTypeValidator = JsonNodeTypeValidator()

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
            // could be a a Pair<String, KClass> which is a field type assert
            // could also be a Pair<String, Iterable<*>> which is a nested field
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

                // validate field type
                if (value is KClass<*>) {
                    val jsonNode = json.get(fieldName)
                    val jsonNodeType = jsonNode.nodeType!!
                    if (!jsonNodeTypeValidator.validate(jsonNode, value)) {
                        errors.add(Error(ErrorType.TYPE, "${normalizeFieldPath(path, fieldName)} is not of type $value. Found $jsonNodeType"))
                    }

                } else if (value is Iterable<*>) {
                    val nestedFields = value
                    val nestedJsonNode = json.get(fieldName)
                    if (nestedJsonNode.isArray) { // walk over each item in the array and apply the nested checks
                        nestedJsonNode.forEach { node ->
                            walkFields(node, nestedFields.requireNoNulls(), path + '/' + fieldName, errors)
                        }
                    } else { // is nested object
                        walkFields(nestedJsonNode, nestedFields.requireNoNulls(), path + '/' + fieldName, errors)
                    }

                } else {
                    throw IllegalStateException("An illegal state occurred in Structural. Unknown second value of Pair. Found ${value::class}")
                }

            } else {
                throw InvalidInputException("Input must either be a Pair<String, *>, where * can be a KClass type assert, for an Iterable for nested objects. Found ${field::class}")
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
        if (value !is Iterable<*> && value !is KClass<*>) {
            throw InvalidInputException("Input must either be a Pair<String, *>, where * can be a KClass type assert, for an Iterable for nested objects. " +
                    "Found <${key::class}, ${value::class}>")
        }
    }

    private fun normalizeFieldPath(path: String, field: String) = (path + '/' + field).replace(Regex("^/"), "")

}