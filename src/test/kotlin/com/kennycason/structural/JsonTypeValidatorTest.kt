package com.kennycason.structural

import com.kennycason.structural.exception.StructuralException
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

    @Test(expected = StructuralException::class)
    fun emptyJson() {
        validator.assert("{}", listOf(Pair("foo", Int::class)))
    }

    @Test
    fun singleField() {
        validator.assert("""{"foo": "bar"}""", listOf(Pair("foo", String::class)))
    }

    @Test(expected = StructuralException::class)
    fun singleFieldInvalidType() {
        validator.assert("""{"foo": "bar"}""", listOf(Pair("foo", Int::class)))
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
                 listOf(Pair("foo", String::class),
                        Pair("nested",
                               listOf(Pair("foo2", String::class)))))
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
                 listOf(Pair("name", String::class),
                        Pair("age", Int::class),
                        Pair("shoe_size", Float::class),
                        Pair("favorite_number", Double::class),
                        Pair("long_number", Long::class),
                        Pair("random", Array<Any>::class),
                        Pair("job", Object::class),
                        Pair("job", listOf(Pair("id", Int::class),
                                           Pair("title", String::class)))))

        // relaxed number types
        validator.assert(json,
                 listOf(Pair("name", String::class),
                        Pair("age", Number::class),
                        Pair("shoe_size", Number::class),
                        Pair("favorite_number", Number::class),
                        Pair("long_number", Number::class),
                        Pair("random", Array<Any>::class),
                        Pair("job", Object::class),
                        Pair("job", listOf(Pair("id", Number::class),
                                           Pair("title", String::class)))))
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
                listOf(Pair("id", String::class),
                        Pair("numbers", Array<Any>::class)))
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
                listOf(Pair("id", String::class),
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
                        listOf(Pair("name", String::class),
                               Pair("coolness", Number::class)))))
    }

}