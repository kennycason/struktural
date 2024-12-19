package com.kennycason.struktural

import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.node.JsonNodeType
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory
import com.kennycason.struktural.error.Error
import com.kennycason.struktural.exception.InvalidInputException
import com.kennycason.struktural.exception.StrukturalException
import com.kennycason.struktural.json.JsonNodeTypeValidator
import com.kennycason.struktural.json.Nullable
import kotlin.reflect.KClass

/**
 * Test fields not missing and field types.
 *
 */
class JsonTypeValidator {
    private var objectMapper: ObjectMapper = ObjectMapper()
    private val jsonNodeTypeValidator = JsonNodeTypeValidator()

    fun setObjectMapper(objectMapper: ObjectMapper) {
        this.objectMapper = objectMapper
    }


    fun assert(jsonString: String, fieldTypes: Iterable<Pair<String, Any>>) = assert(objectMapper.readTree(jsonString), fieldTypes)

    fun assert(json: JsonNode, fieldTypes: Iterable<Pair<String, Any>>) {
        val result = validate(json, fieldTypes)
        if (result.valid) {
            return
        }
        throw StrukturalException(result.errors.joinToString("\n"))
    }

    fun validate(
        jsonString: String,
        fieldTypes: Iterable<Pair<String, Any>>
    ) = assert(objectMapper.readTree(jsonString), fieldTypes)

    fun validate(json: JsonNode, fieldTypes: Iterable<Pair<String, Any>>): ValidationResult {
        // keep track of all errors
        val errors = mutableListOf<Error>()
        // keep track of the path for logging convenience
        val path = ""
        walkFields(json, fieldTypes, path, errors)

        return ValidationResult(errors.isEmpty(), errors)
    }

    private fun walkFields(
        json: JsonNode,
        fields: Iterable<Any>,
        path: String,
        errors: MutableList<Error>
    ) {
        fields.forEach { field ->
            // could be a Pair<String, KClass> which is a field type assert
            // could also be a Pair<String, Iterable<*>> which is a nested field
            if (field is Pair<*, *>) {
                // nest object, validate and recur
                validateNestedField(field)
                // set variables here so smart casting works later
                val fieldName = field.first!! as String
                val value = field.second!!
                if (!json.has(fieldName)) {
                    errors.add(Error(Mode.STRUCTURE, "Field [${normalizeFieldPath(path, fieldName)}] missing."))
                    return@forEach
                }

                // validate field type
                if (value is KClass<*>) {
                    val jsonNode = json.get(fieldName)
                    val jsonNodeType = jsonNode.nodeType!!
                    if (jsonNodeType == JsonNodeType.NULL) {
                        errors.add(
                            Error(
                                Mode.TYPE,
                                "Field [${normalizeFieldPath(path, fieldName)}] is null, expected [${value.simpleName!!.lowercase()}]."
                            )
                        )
                        return@forEach
                    }
                    if (!jsonNodeTypeValidator.validate(jsonNode, value)) {
                        errors.add(
                            Error(
                                Mode.TYPE,
                                "Field [${normalizeFieldPath(path, fieldName)}] is not of type [${value.simpleName!!.lowercase()}]. " +
                                    "Found [${jsonNodeType.toString().lowercase()}]"
                            )
                        )
                    }
                } else if (value is Nullable) {
                    val jsonNode = json.get(fieldName)
                    val jsonNodeType = jsonNode.nodeType!!
                    // only validate if not null since null is allowed.
                    if (jsonNodeType != JsonNodeType.NULL) {
                        if (!jsonNodeTypeValidator.validate(jsonNode, value.clazz)) {
                            errors.add(
                                Error(
                                    Mode.TYPE,
                                    "Field [${normalizeFieldPath(path, fieldName)}] is not of type [${value.clazz.simpleName!!.lowercase()}] or null. " +
                                        "Found [${jsonNodeType.toString().lowercase()}]"
                                )
                            )
                        }
                    }
                } else if (value is Iterable<*>) {
                    val nestedFields = value
                    val nestedJsonNode = json.get(fieldName)
                    if (nestedJsonNode.isArray) { // walk over each item in the array and apply the nested checks
                        nestedJsonNode.forEach { node ->
                            walkFields(node, nestedFields.requireNoNulls(), "$path/$fieldName", errors)
                        }
                    } else { // is nested object
                        walkFields(nestedJsonNode, nestedFields.requireNoNulls(), "$path/$fieldName", errors)
                    }
                } else {
                    throw IllegalStateException("An illegal state occurred in Struktural. Unknown second value of Pair. Found [${value::class}]")
                }
            } else {
                throw InvalidInputException(
                    "Input must either be a Pair<String, *>, where * can be a KClass type assert oor an Iterable " +
                        "for nested objects. Found [${field::class.simpleName?.lowercase()}]"
                )
            }
        }
    }

    private fun validateNestedField(field: Pair<*, *>) {
        val key = field.first
        val value = field.second
        if (key == null) {
            throw InvalidInputException("First value for nested input must be a String. Found null")
        }
        if (value == null) {
            throw InvalidInputException("Second value for nested input must be a Iterable. Found null")
        }
        // test structure of Pair<String, Any>
        if (key !is String) {
            throw InvalidInputException("First value for nested input must be a String. Found [${key::class.simpleName?.lowercase()}]")
        }
        if (!(value is Iterable<*>
                || value is KClass<*>
                || value is Nullable)) {
            throw InvalidInputException(
                "Input must either be a Pair<String, VALUE>, " +
                    "where VALUE can be a KClass for type assertion, Iterable for pairs of nested objects, or a Nullable value, " +
                    "Found Pair<${key::class.simpleName?.lowercase()}, ${value::class.simpleName?.lowercase()}>"
            )
        }
    }

    private fun normalizeFieldPath(path: String, field: String) = ("$path/$field").replace(Regex("^/"), "")
}
