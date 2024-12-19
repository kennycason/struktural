package com.kennycason.struktural

import com.kennycason.struktural.exception.StrukturalException
import com.kennycason.struktural.json.Nullable
import org.junit.Test
//
/**
 * Created by kenny on 5/24/17.
 */
class JsonTypeValidatorTest {
    private val validator = JsonTypeValidator()

    @Test
    fun emptyTest() {
        validator.assert("{}", emptyList())
    }

    @Test(expected = StrukturalException::class)
    fun emptyJson() {
        validator.assert("{}", listOf("foo" to Int::class))
    }

    @Test
    fun singleField() {
        validator.assert("""{"foo": "bar"}""", listOf("foo" to String::class))
    }

    @Test(expected = StrukturalException::class)
    fun singleFieldInvalidType() {
        validator.assert("""{"foo": "bar"}""", listOf("foo" to Int::class))
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
                     "foo" to String::class,
                     "nested" to listOf("foo2" to String::class)
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
            "random": [1,2,3,4,5,6],
            "job": {
                "id": 123456,
                "title": "Software Engineer"
            }
        }
        """
        // strict number types
        validator.assert(json,
                 listOf(
                     "name" to String::class,
                     "age" to Int::class,
                     "shoe_size" to Float::class,
                     "favorite_number" to Double::class,
                     "long_number" to Long::class,
                     "random" to Array<Any>::class,
                     "job" to Object::class,
                     "job" to listOf(
                         "id" to Int::class,
                         "title" to String::class
                     )
                 ))

        // relaxed number types
        validator.assert(json,
                 listOf(
                     "name" to String::class,
                     "age" to Number::class,
                     "shoe_size" to Number::class,
                     "favorite_number" to Number::class,
                     "long_number" to Number::class,
                     "random" to Array<Any>::class,
                     "job" to Object::class,
                     "job" to listOf(
                         "id" to Number::class,
                         "title" to String::class
                     )
                 ))
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
                listOf(
                    "id" to String::class,
                    "numbers" to Array<Any>::class
                ))
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
                listOf(
                    "id" to String::class,
                    "numbers" to listOf("does_not_exist")
                ))
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
                listOf(
                    "languages" to listOf(
                        "name" to String::class,
                        "coolness" to Number::class
                    )
                ))
    }

    @Test
    fun nullTest() {
        val json = """
        {
            "foo": null
        }
        """
        validator.assert(json,
                listOf("foo" to Nullable(String::class)))

        val json2 = """
        {
            "foo": "bar"
        }
        """
        validator.assert(json2,
                listOf("foo" to Nullable(String::class)))
    }

    @Test(expected = StrukturalException::class)
    fun unexpectedNullValue() {
        val json = """
        {
            "foo": null
        }
        """
        validator.assert(json,
                listOf("foo" to String::class))
    }


}
