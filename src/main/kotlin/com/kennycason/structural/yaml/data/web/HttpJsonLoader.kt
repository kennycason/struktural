package com.kennycason.structural.yaml.data.web

import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.ObjectMapper
import com.kennycason.structural.exception.InvalidInputException
import com.kennycason.structural.yaml.Config
import com.kennycason.structural.yaml.data.JsonLoader
import java.io.File
import java.io.InputStream
import java.nio.file.Path
import java.util.concurrent.TimeUnit
import com.sun.corba.se.spi.presentation.rmi.StubAdapter.request
import org.apache.http.Header
import org.apache.http.client.utils.URIBuilder
import org.apache.http.message.BasicHeader
import java.net.URI


/**
 * Created by kenny on 5/25/17.
 */
class HttpJsonLoader(val config: Config, val request: Request) : JsonLoader {
    override fun load(): JsonNode {
        val httpRequests = HttpRequests()
        val headers = request.headers.toTypedArray()

        return when (request.method.toUpperCase()) {
            "GET" -> httpRequests.get(buildUri(request), headers)
            "POST" -> httpRequests.post(buildUri(request), headers, request.body)
            "PATCH" -> httpRequests.patch(buildUri(request), headers, request.body)
            "DELETE" -> httpRequests.delete(buildUri(request), headers)
            else -> throw InvalidInputException("Invalid Http request method. Found [${request.method}]. Valid methods are GET, POST, PATCH, DELETE")
        }
    }

    private fun buildUri(request: Request): URI {
        val parameters = if (request.parameters.isEmpty()) { "" }
        else {
            "?" + request.parameters.joinToString("&")
        }
        val uri = URI(buildBaseURI() + request.uri + parameters)
        println("Making request to uri: $uri")
        return uri
    }

    private fun buildBaseURI(): String {
        if (config.port == 0) { return config. baseUrl }
        return config.baseUrl + ':' + config.port
    }

}