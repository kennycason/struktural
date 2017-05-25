package com.kennycason.structural.yaml

import org.junit.Test

/**
 * Created by kenny on 5/25/17.
 */
class YamlBackedValidatorTest {
    private val validator = YamlBackedValidator()

    @Test
    fun resourceTest() {
        validator.assert(javaClass.getResourceAsStream("/com/kennycason/structural/yaml/structural_sample.yml"))
    }

}