package com.kennycason.structural

import com.kennycason.structural.exception.StructuralException
import org.junit.Test

/**
 * Created by kenny on 5/23/17.
 */
class JsonMissingValidatorTest {
    private val validator = JsonMissingValidator()

    @Test
    fun emptyTest() {
        validator.assert("{}", emptyList())
    }

    @Test(expected = StructuralException::class)
    fun emptyJson() {
        validator.assert("{}", listOf("foo"))
    }

    @Test
    fun singleField() {
        validator.assert("""{"foo": "bar"}""", listOf("foo"))
    }

    @Test
    fun singleNestedField() {
        val json = """
        {
            "foo": "bar",
            "nested": {"foo2": "bar2"}
        }
        """
        validator.assert(json,
                listOf("foo",
                    Pair("nested", listOf("foo2"))))
    }

    @Test
    fun complexNestedField() {
        val json = """
        {
            "name": "kenny",
            "age": 64,
            "job": {
                "id": 123456,
                "title": "Software Engineer"
            }
        }
        """
        validator.assert(json,
                listOf("name",
                        "age",
                        Pair("job",
                            listOf("id",
                                   "title"))))
    }

    @Test
    fun nestedArrayField() {
        val json = """
        {
            "id": "sample",
            "numbers": [1,2,3,4,5,6]
        }
        """
        validator.assert(json,
                listOf("id",
                        "numbers"))
    }

    @Test(expected = StructuralException::class)
    fun accessNestedArrayField() {
        val json = """
        {
            "id": "sample",
            "numbers": [1,2,3,4,5,6]
        }
        """
        validator.assert(json,
                listOf("id",
                        Pair("numbers", listOf("does_not_exist"))))
    }

}