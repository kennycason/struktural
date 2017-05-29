package com.kennycason.structural.yaml

import com.kennycason.structural.Structural
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
    fun urlTest() {
        validator.assert(javaClass.getResourceAsStream("url_sample_test.yml"))
        validator.assert(javaClass.getResourceAsStream("url_sample2_test.yml"))
    }

    @Test
    fun structuralInputStreamHelper() {
        Structural.assertYaml(javaClass.getResourceAsStream("resource_structure_test.yml"))
        Structural.assertYaml(javaClass.getResourceAsStream("resource_types_test.yml"))
        Structural.assertYaml(javaClass.getResourceAsStream("resource_values_test.yml"))
        Structural.assertYaml(javaClass.getResourceAsStream("resource_all_test.yml"))
    }

    @Test
    fun structuralRawStringHelper() {
        val yaml = """
            |---
            |tests:
            |  -
            |    mode: type
            |    data:
            |      resource: /com/kennycason/structural/json/person_sample_response.json
            |
            |    expects:
            |      - name: string
            |      - age: int
            |      - job:
            |          id: int
            |          title: string
        """.trimMargin()
        Structural.assertYaml(yaml)
    }

}