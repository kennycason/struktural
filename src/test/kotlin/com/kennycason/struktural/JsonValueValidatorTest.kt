package com.kennycason.struktural

import com.kennycason.struktural.exception.StrukturalException
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
        validator.assert("{}", listOf(Pair("foo", 23)))
    }

    @Test
    fun singleField() {
        validator.assert("""{"foo": "bar"}""", listOf(Pair("foo", "bar")))
    }

    @Test(expected = StrukturalException::class)
    fun singleFieldInvalidValue() {
        validator.assert("""{"foo": "bar"}""", listOf(Pair("foo", "rab")))
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
                 listOf(Pair("foo", "bar"),
                        Pair("nested",
                               listOf(Pair("foo2", "bar2")))))
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
                 listOf(Pair("name", "kenny"),
                        Pair("age", 64),
                        Pair("shoe_size", 10.5),
                        Pair("favorite_number", 2.718281828459045235),
                        Pair("long_number", 1223235345342348),
                        Pair("job", listOf(Pair("id", 123456),
                                           Pair("title", "Software Engineer")))))

        // only match partial
        validator.assert(json,
                 listOf(Pair("name", "kenny"),
                        Pair("favorite_number", 2.718281828459045235)))
    }

    @Test
    fun nestedArrayField() {
        val json = """
        {
            "numbers": [1,2,3,4,5,6]
        }
        """
        validator.assert(json,
                listOf(Pair("numbers", arrayOf(1, 2, 3, 4, 5, 6))))
    }

    @Test(expected = StrukturalException::class)
    fun nestedArrayFieldNotEqual() {
        val json = """
        {
            "numbers": [1,2,3,4,5,6]
        }
        """
        validator.assert(json,
                listOf(Pair("numbers", arrayOf(1, 2, 3, 4, 5, 6, 7))))
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
                listOf(Pair("people",
                        listOf(Pair("favorite_language", "kotlin")))))
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
                listOf(Pair("people",
                        listOf(Pair("age", 64)))))
    }
}