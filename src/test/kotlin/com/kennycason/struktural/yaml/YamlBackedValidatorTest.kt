package com.kennycason.struktural.yaml

import com.kennycason.struktural.Struktural
import org.junit.Ignore
import org.junit.Test

/**
 * Created by kenny on 5/25/17.
 */
class YamlBackedValidatorTest {
    private val resourcePath = "/com/kennycason/struktural/yaml/"
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
    fun strukturalInputStreamHelper() {
        Struktural.assertYaml(javaClass.getResourceAsStream("resource_structure_test.yml"))
        Struktural.assertYaml(javaClass.getResourceAsStream("resource_types_test.yml"))
        Struktural.assertYaml(javaClass.getResourceAsStream("resource_values_test.yml"))
        Struktural.assertYaml(javaClass.getResourceAsStream("resource_all_test.yml"))
    }

    @Test
    fun strukturalRawStringHelper() {
        val yaml = """
            |---
            |tests:
            |  -
            |    mode: type
            |    data:
            |      resource: /com/kennycason/struktural/json/person_sample_response.json
            |
            |    expects:
            |      - name: string
            |      - age: int
            |      - job:
            |          id: int
            |          title: string
        """.trimMargin()
        Struktural.assertYaml(yaml)
    }

}