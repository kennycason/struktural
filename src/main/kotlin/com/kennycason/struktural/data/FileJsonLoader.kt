package com.kennycason.struktural.data

import com.fasterxml.jackson.databind.ObjectMapper
import java.io.File

/**
 * Created by kenny on 5/25/17.
 */
class FileJsonLoader(
    private val file: File,
    private val objectMapper: ObjectMapper = ObjectMapper()
) : JsonLoader {

    override fun load() = objectMapper.readTree(file)!!
}
