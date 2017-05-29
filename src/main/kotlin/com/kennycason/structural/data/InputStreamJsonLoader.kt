package com.kennycason.structural.data

import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.ObjectMapper
import java.io.File
import java.io.InputStream
import java.nio.file.Path

/**
 * Created by kenny on 5/25/17.
 */
class InputStreamJsonLoader(private val inputStream: InputStream) : JsonLoader {
    private val objectMapper = ObjectMapper()

    override fun load(): JsonNode {
        return objectMapper.readTree(inputStream)!!
    }

}