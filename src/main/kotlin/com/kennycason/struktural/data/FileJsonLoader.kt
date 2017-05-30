package com.kennycason.struktural.data

import com.fasterxml.jackson.databind.ObjectMapper
import java.io.File
import java.nio.file.Path

/**
 * Created by kenny on 5/25/17.
 */
class FileJsonLoader(private val file: File): JsonLoader {
    private val objectMapper = ObjectMapper()

    override fun load() = objectMapper.readTree(file)!!

}