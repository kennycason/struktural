package com.kennycason.struktural

import com.kennycason.struktural.exception.StrukturalException
import org.hamcrest.Matchers
import org.junit.Test

/**
 * Created by kenny on 5/24/17.
 */
class JsonValueValidatorTest {
    private val validator = JsonValueValidator()

    @Test
    fun emptyTest() {
        validator.assert("{}", emptyList())
    }

    @Test(expected = StrukturalException::class)
    fun emptyJson() {
        validator.assert("{}", listOf("foo" to 23))
    }

    @Test
    fun singleField() {
        validator.assert("""{"foo": "bar"}""", listOf("foo" to "bar"))
    }

    @Test(expected = StrukturalException::class)
    fun singleFieldInvalidValue() {
        validator.assert("""{"foo": "bar"}""", listOf("foo" to "rab"))
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
                 listOf(
                     "foo" to "bar",
                     "nested" to listOf("foo2" to "bar2")
                 ))
    }

    @Test
    fun complexNestedField() {
        val json = """
        {
            "name": "kenny",
            "age": 64,
            "shoe_size": 10.5,
            "favorite_number": 2.718281828459045235,
            "long_number": 1223235345342348,
            "job": {
                "id": 123456,
                "title": "Software Engineer"
            }
        }
        """
        validator.assert(json,
                 listOf(
                     "name" to "kenny",
                     "age" to 64,
                     "shoe_size" to 10.5,
                     "favorite_number" to 2.718281828459045235,
                     "long_number" to 1223235345342348,
                     "job" to listOf(
                         "id" to 123456,
                         "title" to "Software Engineer"
                     )
                 ))

        // only match partial
        validator.assert(json,
                 listOf(
                     "name" to "kenny",
                     "favorite_number" to 2.718281828459045235
                 ))
    }

    @Test
    fun nestedArrayField() {
        val json = """
        {
            "numbers": [1,2,3,4,5,6]
        }
        """
        validator.assert(json,
                listOf("numbers" to arrayOf(1, 2, 3, 4, 5, 6)))
    }

    @Test(expected = StrukturalException::class)
    fun nestedArrayFieldNotEqual() {
        val json = """
        {
            "numbers": [1,2,3,4,5,6]
        }
        """
        validator.assert(json,
                listOf("numbers" to arrayOf(1, 2, 3, 4, 5, 6, 7)))
    }

    @Test
    fun nestedArrayObject() {
        val json = """
        {
            "people": [
                {
                    "name": "kenny",
                    "favorite_language": "kotlin",
                    "age": 64
                },
                {
                    "name": "martin",
                    "favorite_language": "kotlin",
                    "age": 92
                },
                {
                    "name": "andrew",
                    "favorite_language": "kotlin",
                    "age": 13180
                }
            ]
        }
        """
        validator.assert(json,
                listOf("people" to listOf("favorite_language" to "kotlin")))
    }


    @Test(expected = StrukturalException::class)
    fun nestedArrayObjectDifferentValues() {
        val json = """
        {
            "people": [
                {
                    "name": "kenny",
                    "favorite_language": "kotlin",
                    "age": 64
                },
                {
                    "name": "martin",
                    "favorite_language": "kotlin",
                    "age": 92
                }
            ]
        }
        """
        validator.assert(json,
                listOf("people" to listOf("age" to 64)))
    }

    @Test
    fun usingMatchers() {
        val json = """
        {
            "people": [
                {
                    "name": "kenny",
                    "favorite_language": "Kotlin",
                    "age": 64
                },
                {
                    "name": "martin",
                    "favorite_language": "Kotlin",
                    "age": 92
                }
            ]
        }
        """
        validator.assert(json,
                listOf(
                    "people" to listOf(
                        "name" to Matchers.notNullValue(),
                        "favorite_language" to Matchers.equalToIgnoringCase("kotlin"),
                        "age" to Matchers.greaterThan(50)
                    )
                ))
    }

}
