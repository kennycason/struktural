package com.kennycason.struktural.data.web

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
class HttpRequests() {
    private val objectMapper = ObjectMapper()

    fun get(uri: URI,
            headers: Array<Header>): JsonNode  {
        val request = HttpGet(uri)
        request.setHeaders(headers)

        return objectMapper.readTree(
                HttpClientBuilder.create()
                        .build()
                        .execute(request)
                        .getEntity().getContent())
    }

    fun post(uri: URI,
             headers: Array<Header>,
             data: String?): JsonNode {
        val request = HttpPost(uri)
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

    fun patch(uri: URI,
              headers: Array<Header>,
              data: String?): JsonNode {
        val request = HttpPatch(uri)
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

    fun delete(uri: URI,
               headers: Array<Header>): JsonNode {
        val request = HttpDelete(uri)
        request.setHeaders(headers)

        return objectMapper.readTree(
                HttpClientBuilder.create()
                        .build()
                        .execute(request)
                        .getEntity().getContent())
    }

}