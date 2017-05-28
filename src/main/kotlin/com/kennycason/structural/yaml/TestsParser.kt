package com.kennycason.structural.yaml

import com.fasterxml.jackson.databind.JsonNode
import com.kennycason.structural.exception.InvalidInputException
import com.kennycason.structural.yaml.data.FileJsonLoader
import com.kennycason.structural.yaml.data.InputStreamJsonLoader
import com.kennycason.structural.yaml.data.JsonLoader
import com.kennycason.structural.yaml.data.web.HttpJsonLoader
import com.kennycason.structural.yaml.data.web.Request
import com.kennycason.structural.yaml.transform.ExpectsMapTransform
import com.kennycason.structural.yaml.transform.IdentityValueTransform
import com.kennycason.structural.yaml.transform.TypeValueTransform
import com.kennycason.structural.yaml.transform.ValueTransform
import org.apache.http.Header
import org.apache.http.message.BasicHeader
import java.io.File
import java.util.regex.Pattern

/**
 * Created by kenny on 5/26/17.
 */
class TestsParser {
    private val colonPattern = Pattern.compile(":")

    fun parse(testModel: Map<String, Any>, config: Config): List<TestCase> {
        if (!testModel.contains("tests")) {
            throw InvalidInputException("Test Yaml must have 'tests' block")
        }
        if (testModel.get("tests") !is List<*>) {
            throw InvalidInputException("Test Yaml 'tests' block must be list")
        }
        val testsList = testModel.get("tests") as List<Map<String, Any>>

        val tests = mutableListOf<TestCase>()
        testsList.forEach { node ->
            tests.add(parseTest(node, config))
        }
        return tests
    }

    private fun parseTest(test: Map<String, Any>, config: Config): TestCase {
        val mode = parseMode(test)
        val expectsMapTransform = ExpectsMapTransform(selectValueTransform(mode))
        return TestCase(
                config = config,
                mode = mode,
                jsonLoader = parseJsonLoader(test, config),
                expects = expectsMapTransform.transform(test.get("expects") as List<Map<String, Any>>))
    }

    private fun selectValueTransform(mode: Mode) = when (mode) {
        Mode.TYPE -> TypeValueTransform()
        else -> IdentityValueTransform()
    }

    private fun parseMode(test: Map<String, Any>): Mode {
        if (!test.contains("mode")) {
            throw InvalidInputException("Test must have 'mode' block. Possible values: (missing, type, value)")
        }
        return when (test.get("mode")) {
            "structure" -> Mode.STRUCTURE
            "type" -> Mode.TYPE
            "value" -> Mode.VALUE
            else -> throw InvalidInputException("Invalid mode value. Possible values: (missing, type, value)")
        }
    }

    private fun parseJsonLoader(test: Map<String, Any>, config: Config): JsonLoader {
        if (!test.contains("data")) {
            throw InvalidInputException("Test must have 'data' block.")
        }
        val dataNode = test.get("data") as Map<String, Any>
        if (dataNode.contains("resource")) {
            val resource = javaClass.getResourceAsStream(dataNode.get("resource") as String)
            if (resource == null) {
                throw InvalidInputException("Provided resource $resource is null")
            }
            return InputStreamJsonLoader(resource)
        }
        if (dataNode.contains("file")) {
            return FileJsonLoader(File(dataNode.get("file") as String))
        }
        if (dataNode.contains("request")) {
            return HttpJsonLoader(config, parseRequest(dataNode.get("request") as Map<String, Any>))
        }
        throw InvalidInputException("'data' block must contain either 'resource', 'file', or 'request' block.")
    }

    private fun parseRequest(requestNode: Map<String, Any>): Request {
        if (!requestNode.contains("uri")) {
            throw InvalidInputException("Request block must have 'uri' block.")
        }
        if (!requestNode.contains("method")) {
            throw InvalidInputException("Request block must have 'method' block.")
        }
        return Request(
                uri = requestNode.get("uri") as String,
                parameters = parseParameters(requestNode),
                body = if (requestNode.contains("body")) { requestNode.get("body") as String } else { null },
                headers = parseHeaders(requestNode))
    }

    private fun parseHeaders(requestNode: Map<String, Any>): List<Header> {
        if (!requestNode.contains("headers")) {
            return emptyList()
        }
        return (requestNode.get("headers") as List<String>)
                .map {header ->
                    if (!header.contains(':')) {
                        throw InvalidInputException("Invalid header format. Must contain ':' between key: value")
                    }
                    val keyValue = header.split(colonPattern, 2)
                    BasicHeader(keyValue[0], keyValue[1])
                }
                .toList()
    }

    private fun parseParameters(requestNode: Map<String, Any>): List<String> {
        if (!requestNode.contains("params")
                || requestNode.get("params") == null) {
            return emptyList()
        }
        return requestNode.get("params") as List<String>
    }

}