package com.kennycason.structural

import com.fasterxml.jackson.databind.ObjectMapper

/**
 * Created by kenny on 5/23/17.
 */
class Structural {
    private val structureAsserter = JsonMissingValidator()

    fun assertStructure(jsonString: String, fields: List<Any>) {
        structureAsserter.assert(jsonString, fields)
    }

    fun assertTypes(fields: List<Pair<String, Any>>) {

    }

    fun assertValues(fields: List<Pair<String, Any>>) {

    }

}