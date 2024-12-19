package com.kennycason.struktural.data.web

import com.fasterxml.jackson.databind.JsonNode
import com.kennycason.struktural.data.JsonLoader
import com.kennycason.struktural.yaml.Config
import java.net.URI

/**
 * Created by kenny on 5/25/17.
 */
class HttpJsonLoader(
    val config: Config = Config(),
    val request: Request
) : JsonLoader {
    override fun load(): JsonNode {
        val httpRequests = HttpRequests()
        val headers = request.headers.toTypedArray()

        return when (request.method) {
            HttpMethod.GET -> httpRequests.get(buildUri(request), headers)
            HttpMethod.POST -> httpRequests.post(buildUri(request), headers, request.body)
            HttpMethod.PATCH -> httpRequests.patch(buildUri(request), headers, request.body)
            HttpMethod.DELETE -> httpRequests.delete(buildUri(request), headers)
        }
    }

    private fun buildUri(request: Request): URI {
        val parameters = if (request.parameters.isEmpty()) { "" }
        else { "?" + request.parameters.joinToString("&") }
        return URI(buildBaseURI() + request.uri + parameters)
    }

    private fun buildBaseURI(): String {
        if (config.port == 0) {
            return config.baseUrl
        }
        return config.baseUrl + ':' + config.port
    }
}
