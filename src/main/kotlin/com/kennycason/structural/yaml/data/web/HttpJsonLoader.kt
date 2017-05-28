package com.kennycason.structural.yaml.data.web

import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.ObjectMapper
import com.kennycason.structural.yaml.Config
import com.kennycason.structural.yaml.data.JsonLoader
import java.io.File
import java.io.InputStream
import java.nio.file.Path
import java.util.concurrent.TimeUnit
import com.sun.corba.se.spi.presentation.rmi.StubAdapter.request
import org.apache.http.Header
import org.apache.http.message.BasicHeader




/**
 * Created by kenny on 5/25/17.
 */
class HttpJsonLoader(val config: Config, val request: Request) : JsonLoader {
    private val objectMapper = ObjectMapper()

    override fun load(): JsonNode {
        val httpRequests = HttpRequests(objectMapper, config.baseUrl, config.port)
        return httpRequests.post(request.uri, request.headers.toTypedArray(), request.body)
    }

}