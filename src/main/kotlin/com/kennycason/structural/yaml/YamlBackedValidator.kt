package com.kennycason.structural.yaml

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory
import java.io.InputStream

/**
 * Created by kenny on 5/25/17.
 */
class YamlBackedValidator {
    private val objectMapper = ObjectMapper(YAMLFactory())

    fun assert(yamlInputStream: InputStream) {
        val yaml = objectMapper.readValue(yamlInputStream, Map::class.java)
        println(yaml)
    }

}