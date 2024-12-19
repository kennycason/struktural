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
import java.text.SimpleDateFormat
import kotlin.reflect.KClass

fun assertJsonTypes(
    jsonString: String,
    mapper: ObjectMapper = Struktural.getObjectMapper(),
    block: JsonTypeAssertion.JsonValidatorScope.() -> Unit
) {
    val validator = JsonTypeAssertion()
    validator.validateJson(jsonString, mapper, block)
}

class JsonTypeAssertion {

    fun validateJson(
        jsonString: String,
        mapper: ObjectMapper = Struktural.getObjectMapper(),
        block: JsonValidatorScope.() -> Unit
    ) {
        val json = mapper.readTree(jsonString)
        val scope = JsonValidatorScope(json)
        scope.block()

        if (scope.errors.isNotEmpty()) {
            throw StrukturalException(scope.errors.joinToString("\n"))
        }
    }

    inner class JsonValidatorScope(private val json: JsonNode) {
        val errors = mutableListOf<String>()

        fun string(fieldName: String) {
            validateField(fieldName, String::class)
        }

        fun number(fieldName: String) {
            validateField(fieldName, Number::class)
        }

        fun boolean(fieldName: String) {
            validateField(fieldName, Boolean::class)
        }

        fun integer(fieldName: String) {
            validateFieldWithPredicate(fieldName, "integer") { it.isInt }
        }

        fun long(fieldName: String) {
            validateFieldWithPredicate(fieldName, "long") { it.isLong }
        }

        fun decimal(fieldName: String) {
            validateFieldWithPredicate(fieldName, "decimal") { it.isFloat || it.isDouble }
        }

        fun nullableString(fieldName: String) {
            validateField(fieldName, String::class, allowNull = true)
        }

        fun nullableNumber(fieldName: String) {
            validateField(fieldName, Number::class, allowNull = true)
        }

        fun nullableBoolean(fieldName: String) {
            validateFieldWithPredicate(fieldName, "nullable boolean") {
                it.isNull || it.isBoolean
            }
        }

        fun nullableInteger(fieldName: String) {
            validateFieldWithPredicate(fieldName, "nullable integer") {
                it.isNull || it.isInt
            }
        }

        fun nullableLong(fieldName: String) {
            validateFieldWithPredicate(fieldName, "nullable long") {
                it.isNull || it.isLong
            }
        }

        fun nullableDecimal(fieldName: String) {
            validateFieldWithPredicate(fieldName, "nullable decimal") {
                it.isNull || it.isFloat || it.isDouble
            }
        }

        // Date/Time Types
        fun date(fieldName: String, pattern: String = "yyyy-MM-dd") {
            validateFieldWithPredicate(fieldName, "date") {
                it.isTextual && isValidDate(it.asText(), pattern)
            }
        }

        fun dateTime(fieldName: String, pattern: String = "yyyy-MM-dd'T'HH:mm:ss") {
            validateFieldWithPredicate(fieldName, "date-time") {
                it.isTextual && isValidDate(it.asText(), pattern)
            }
        }

        fun <T : Enum<T>> enum(fieldName: String, enumClass: KClass<T>) {
            val allowedValues = enumClass.java.enumConstants.map { it.name }
            validateFieldWithPredicate(fieldName, "enum of ${enumClass.simpleName}") {
                it.isTextual && it.asText() in allowedValues
            }
        }

        fun enum(fieldName: String, block: () -> Array<String>) {
            val allowedValues = block().toList()
            validateFieldWithPredicate(fieldName, "enum of ${allowedValues.joinToString()}") {
                it.isTextual && it.asText() in allowedValues
            }
        }


        fun matchesRegex(fieldName: String, regex: Regex) {
            validateFieldWithPredicate(fieldName, "matching regex $regex") {
                it.isTextual && it.asText().matches(regex)
            }
        }

        fun custom(fieldName: String, description: String, validation: (JsonNode) -> Boolean) {
            validateFieldWithPredicate(fieldName, description, validation)
        }

        fun array(fieldName: String, block: JsonValidatorScope.() -> Unit) {
            val node = json[fieldName]
            if (node == null || !node.isArray) {
                errors.add("Expected an array at field '$fieldName'.")
                return
            }
            node.forEach { element ->
                JsonValidatorScope(element).block()
            }
        }

        fun objectField(fieldName: String, block: JsonValidatorScope.() -> Unit) {
            val node = json[fieldName]
            if (node == null || !node.isObject) {
                errors.add("Expected an object at field '$fieldName'.")
                return
            }
            JsonValidatorScope(node).block()
        }

        private fun validateField(fieldName: String, type: KClass<*>, allowNull: Boolean = false) {
            val node = json[fieldName]
            if (node == null) {
                errors.add("Field '$fieldName' is missing.")
                return
            }
            if (node.isNull && allowNull) return

            val isValidType = when (type) {
                String::class -> node.isTextual
                Number::class -> node.isNumber
                Boolean::class -> node.isBoolean
                else -> false
            }
            if (!isValidType) {
                errors.add("Field '$fieldName' is not of type '${type.simpleName}'. Found '${node.nodeType}'.")
            }
        }

        private fun validateFieldWithPredicate(
            fieldName: String,
            description: String,
            predicate: (JsonNode) -> Boolean
        ) {
            val node = json[fieldName]
            if (node == null) {
                errors.add("Field '$fieldName' is missing.")
                return
            }
            if (!predicate(node)) {
                errors.add("Field '$fieldName' is not $description.")
            }
        }

        private fun isValidDate(value: String, pattern: String): Boolean {
            return try {
                SimpleDateFormat(pattern).parse(value)
                true
            } catch (e: Exception) {
                false
            }
        }
    }
}
