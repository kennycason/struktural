package com.kennycason.struktural

import com.kennycason.struktural.exception.StrukturalException
import org.junit.Test

/**
 * Created by kenny on 5/23/17.
 */
class JsonStructureValidatorTest {
    private val validator = JsonStructureValidator()

    @Test
    fun emptyTest() {
        validator.assert("{}", emptyList())
    }

    @Test(expected = StrukturalException::class)
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

    @Test(expected = StrukturalException::class)
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

    @Test
    fun nestedArrayObject() {
        val json = """
        {
            "languages": [
                {
                    "name": "kotlin",
                    "coolness": 100
                },
                {
                    "name": "java",
                    "coolness": 50
                },
                {
                    "name": "javascript",
                    "coolness": 25
                }
            ]
        }
        """
        validator.assert(json,
                listOf(Pair("languages",
                        listOf("name",
                               "coolness"))))
    }

}