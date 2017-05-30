package com.kennycason.struktural.yaml

import com.fasterxml.jackson.databind.JsonNode
import com.kennycason.struktural.Mode
import com.kennycason.struktural.exception.InvalidInputException
import com.kennycason.struktural.data.FileJsonLoader
import com.kennycason.struktural.data.InputStreamJsonLoader
import com.kennycason.struktural.data.JsonLoader
import com.kennycason.struktural.data.web.HttpJsonLoader
import com.kennycason.struktural.data.web.HttpMethod
import com.kennycason.struktural.data.web.Request
import com.kennycason.struktural.yaml.transform.ExpectsMapTransform
import com.kennycason.struktural.yaml.transform.IdentityValueTransform
import com.kennycason.struktural.yaml.transform.TypeValueTransform
import com.kennycason.struktural.yaml.transform.ValueTransform
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
        val expectsMapTransform = ExpectsMapTransform(mode)
        return TestCase(
                config = config,
                mode = mode,
                jsonLoader = parseJsonLoader(test, config),
                expects = when (mode) {
                    Mode.STRUCTURE -> expectsMapTransform.transformToAny(test.get("expects") as Iterable<Any>)
                    Mode.TYPE, Mode.VALUE -> expectsMapTransform.transformToPairs(test.get("expects") as Iterable<Map<String, Any>>)
                })
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
                method = parseHttpMethod(requestNode),
                parameters = parseParameters(requestNode),
                body = if (requestNode.contains("body")) { requestNode.get("body") as String } else { null },
                headers = parseHeaders(requestNode))
    }

    private fun parseHttpMethod(requestNode: Map<String, Any>): HttpMethod {
        if (!requestNode.contains("method")) { return HttpMethod.GET }

        val method = requestNode.get("method") as String
        return when (method) {
            "GET" -> HttpMethod.GET
            "POST" -> HttpMethod.POST
            "PATCH" -> HttpMethod.PATCH
            "DELETE" -> HttpMethod.DELETE
            else -> throw InvalidInputException("Invalid Request method provided. Found: [$method]. Valid values are: ${HttpMethod.values()}")
        }
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