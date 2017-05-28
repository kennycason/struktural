package com.kennycason.structural.yaml

import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.PropertyNamingStrategy
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory
import com.kennycason.structural.Structural
import com.kennycason.structural.error.Error
import com.kennycason.structural.exception.InvalidInputException
import com.kennycason.structural.exception.StructuralException
import com.kennycason.structural.yaml.data.FileJsonLoader
import com.kennycason.structural.yaml.data.InputStreamJsonLoader
import com.kennycason.structural.yaml.data.JsonLoader
import com.kennycason.structural.yaml.data.web.HttpJsonLoader
import com.kennycason.structural.yaml.data.web.Request
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
        val testModel: Map<String, Any> = yamlObjectMapper.readValue(yamlInputStream,
                object: TypeReference<Map<String, Any>>() {})
        val config = parseConfig(testModel)
        val tests = testsParser.parse(testModel, config)

        val errors = mutableListOf<Error>()
        tests.forEach { test ->
            when (test.mode) {
                Mode.STRUCTURE ->
                    errors.addAll(
                            Structural.validateStructure(test.jsonLoader.load(), test.expects).errors)
                Mode.TYPE ->
                    errors.addAll(
                            Structural.validateTypes(test.jsonLoader.load(), test.expects as Iterable<Pair<String, Any>>).errors)
                Mode.VALUE ->
                    errors.addAll(
                            Structural.validateValues(test.jsonLoader.load(), test.expects as Iterable<Pair<String, Any>>).errors)
            }
        }
        if (errors.size > 0) {
            throw StructuralException(errors.joinToString("\n"))
        }
    }

    private fun parseConfig(testModel: Map<String, Any>): Config {
        if (!testModel.contains("config")) {
            return Config("", 0)
        }
        val configMap = testModel.get("config") as Map<String, Any>
        return Config(
                baseUrl = if (configMap.contains("base_url")) { configMap.get("base_url") as String } else { "" },
                port = if (configMap.contains("port")) { configMap.get("port") as Int } else { 0 }
        )
    }

}