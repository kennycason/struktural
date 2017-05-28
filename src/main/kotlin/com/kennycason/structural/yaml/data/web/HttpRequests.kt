package com.kennycason.structural.yaml.data.web

import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.ObjectMapper
import org.apache.http.Header
import org.apache.http.client.methods.HttpDelete
import org.apache.http.client.methods.HttpGet
import org.apache.http.client.methods.HttpPatch
import org.apache.http.client.methods.HttpPost
import org.apache.http.entity.ByteArrayEntity
import org.apache.http.entity.StringEntity
import org.apache.http.impl.client.HttpClientBuilder
import java.io.IOException
import java.net.URI
import java.nio.charset.Charset

/**
 * Created by kenny on 5/25/17.
 */
class HttpRequests(private val objectMapper: ObjectMapper,
                   private val baseUrl: String,
                   private val port: Int) {

    fun get(endpoint: String, headers: Array<Header>): JsonNode  {
        val request = HttpGet(buildUrl(endpoint))
        request.setHeaders(headers)

        return objectMapper.readTree(
                HttpClientBuilder.create()
                        .build()
                        .execute(request)
                        .getEntity().getContent())
    }

    fun post(endpoint: String,
             headers: Array<Header>,
             data: String?): JsonNode {
        val request = HttpPost(buildUrl(endpoint))
        request.setHeaders(headers)
        if (data != null) {
            request.setEntity(StringEntity(data))
        }

        return objectMapper.readTree(
                HttpClientBuilder.create()
                        .build()
                        .execute(request)
                        .getEntity().getContent())
    }

    fun patch(endpoint: String,
                  headers: Array<Header>,
                  data: Any): JsonNode {
        val request = HttpPatch(buildUrl(endpoint))
        request.setHeaders(headers)
        request.setEntity(ByteArrayEntity(objectMapper.writeValueAsBytes(data)))

        return objectMapper.readTree(
                HttpClientBuilder.create()
                        .build()
                        .execute(request)
                        .getEntity().getContent())
    }

    fun delete(endpoint: String,
                   headers: Array<Header>): JsonNode {
        val request = HttpDelete(buildUrl(endpoint))
        request.setHeaders(headers)

        return objectMapper.readTree(
                HttpClientBuilder.create()
                        .build()
                        .execute(request)
                        .getEntity().getContent())
    }

    private fun buildUrl(endpoint: String): String {
        if (port == 0) { return baseUrl + endpoint }
        return baseUrl + ':' + port + endpoint
    }

}