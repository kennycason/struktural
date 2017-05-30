package com.kennycason.struktural.yaml

import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.PropertyNamingStrategy
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory
import com.kennycason.struktural.Mode
import com.kennycason.struktural.Struktural
import com.kennycason.struktural.ValidationResult
import com.kennycason.struktural.error.Error
import com.kennycason.struktural.exception.InvalidInputException
import com.kennycason.struktural.exception.StrukturalException
import com.kennycason.struktural.data.FileJsonLoader
import com.kennycason.struktural.data.InputStreamJsonLoader
import com.kennycason.struktural.data.JsonLoader
import com.kennycason.struktural.data.web.HttpJsonLoader
import com.kennycason.struktural.data.web.Request
import org.apache.http.Header
import org.apache.http.NameValuePair
import org.apache.http.impl.io.AbstractMessageParser.parseHeaders
import org.apache.http.message.BasicHeader
import java.io.File
import java.io.InputStream
import java.util.regex.Pattern

/**
 * Created by kenny on 5/25/17.
 */
class YamlBackedValidator {
    private val objectMapper = ObjectMapper()
    private val yamlObjectMapper = ObjectMapper(YAMLFactory())
    private val testsParser = TestsParser()
    init {
        objectMapper.propertyNamingStrategy = PropertyNamingStrategy.SNAKE_CASE
    }

    fun assert(yamlInputStream: InputStream) {
        val validationResult = validate(yamlInputStream)
        if (!validationResult.valid) {
            throw StrukturalException(validationResult.errors.joinToString("\n"))
        }
    }

    fun assert(yamlString: String) {
        val validationResult = validate(yamlString)
        if (!validationResult.valid) {
            throw StrukturalException(validationResult.errors.joinToString("\n"))
        }
    }

    fun validate(yamlString: String): ValidationResult {
        val testModel: Map<String, Any> =  yamlObjectMapper.readValue(yamlString, object: TypeReference<Map<String, Any>>() {})
        return validate(testModel)
    }

    fun validate(yamlInputStream: InputStream): ValidationResult {
        val testModel: Map<String, Any> =  yamlObjectMapper.readValue(yamlInputStream, object: TypeReference<Map<String, Any>>() {})
        return validate(testModel)
    }

    private fun validate(testModel: Map<String, Any>): ValidationResult {
        val config = parseConfig(testModel)
        val tests = testsParser.parse(testModel, config)

        val errors = mutableListOf<Error>()
        tests.forEach { test ->
            val response = test.jsonLoader.load()
            when (test.mode) {
                Mode.STRUCTURE -> {
                    val result = Struktural.validateStructure(response, test.expects)
                    if (!result.valid) {
                        errors.addAll(result.errors)
                    }
                }
                Mode.TYPE -> {
                    val result = Struktural.validateTypes(response, test.expects as Iterable<Pair<String, Any>>)
                    if (!result.valid) {
                        errors.addAll(result.errors)
                    }
                }
                Mode.VALUE -> {
                    val result = Struktural.validateValues(response, test.expects as Iterable<Pair<String, Any>>)
                    if (!result.valid) {
                        errors.addAll(result.errors)
                    }
                }
            }
        }
        return ValidationResult(errors.isEmpty(), errors)
    }

    private fun parseConfig(testModel: Map<String, Any>): Config {
        if (!testModel.contains("config")) {
            return Config()
        }
        val configMap = testModel.get("config") as Map<String, Any>
        return Config(
                baseUrl = if (configMap.contains("base_url")) { configMap.get("base_url") as String } else { "" },
                port = if (configMap.contains("port")) { configMap.get("port") as Int } else { 0 }
        )
    }

}