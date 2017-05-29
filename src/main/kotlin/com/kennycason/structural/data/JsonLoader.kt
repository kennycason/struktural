package com.kennycason.structural.data

import com.fasterxml.jackson.databind.JsonNode

/**
 * Created by kenny on 5/25/17.
 */
interface JsonLoader {
    fun load(): JsonNode
}