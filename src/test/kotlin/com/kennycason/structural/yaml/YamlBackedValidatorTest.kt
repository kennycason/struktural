package com.kennycason.structural.yaml

import org.junit.Ignore
import org.junit.Test

/**
 * Created by kenny on 5/25/17.
 */
class YamlBackedValidatorTest {
    private val resourcePath = "/com/kennycason/structural/yaml/"
    private val validator = YamlBackedValidator()

    @Test
    fun resourceSingleTest() {
        validator.assert(javaClass.getResourceAsStream("resource_structure_test.yml"))
        validator.assert(javaClass.getResourceAsStream("resource_types_test.yml"))
        validator.assert(javaClass.getResourceAsStream("resource_values_test.yml"))
    }

    @Test
    fun resourceManyTests() {
        validator.assert(javaClass.getResourceAsStream("resource_all_test.yml"))
    }

    @Ignore
    fun urlManyTests() {
        validator.assert(javaClass.getResourceAsStream("url_all_test.yml"))
    }

}