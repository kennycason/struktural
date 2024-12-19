package com.kennycason.struktural.data.web

import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.ObjectMapper
import org.apache.http.Header
import org.apache.http.client.methods.HttpDelete
import org.apache.http.client.methods.HttpGet
import org.apache.http.client.methods.HttpPatch
import org.apache.http.client.methods.HttpPost
import org.apache.http.entity.StringEntity
import org.apache.http.impl.client.HttpClientBuilder
import java.net.URI

/**
 * Created by kenny on 5/25/17.
 */
class HttpRequests() {
    private val objectMapper = ObjectMapper()

    fun get(uri: URI,
            headers: Array<Header>): JsonNode  {
        val request = HttpGet(uri)
        request.setHeaders(headers)

        println("GET $uri")
        return objectMapper.readTree(
                HttpClientBuilder.create()
                        .build()
                        .execute(request)
                        .entity.content
        )
    }

    fun post(uri: URI,
             headers: Array<Header>,
             data: String?): JsonNode {
        val request = HttpPost(uri)
        request.setHeaders(headers)
        if (data != null) {
            request.entity = StringEntity(data)
        }

        println("POST $uri")
        return objectMapper.readTree(
                HttpClientBuilder.create()
                        .build()
                        .execute(request)
                        .entity.content
        )
    }

    fun patch(uri: URI,
              headers: Array<Header>,
              data: String?): JsonNode {
        val request = HttpPatch(uri)
        request.setHeaders(headers)
        if (data != null) {
            request.entity = StringEntity(data)
        }

        println("PATCH $uri")
        return objectMapper.readTree(
                HttpClientBuilder.create()
                        .build()
                        .execute(request)
                        .entity.content
        )
    }

    fun delete(uri: URI,
               headers: Array<Header>): JsonNode {
        val request = HttpDelete(uri)
        request.setHeaders(headers)

        println("DELETE $uri")
        return objectMapper.readTree(
                HttpClientBuilder.create()
                        .build()
                        .execute(request)
                        .entity.content
        )
    }

}
