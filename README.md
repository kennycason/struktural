# Structural

## What
Structural is a suite of tools written in Kotlin designed to make testing of APIs easier in Java/Kotlin.

Structural is designed to give flexible control over the level of desired testing.

| Features | Description |
| -------- | ----------- |
| Assert Json Structure | A lightweight test to assert presence of fields |
| Assert Json Types | A middleweight test to assert presence of fields and their types |
| Assert Json Values | A heavyweight test to assert presence of fields and their values |

Structural provides two interfaces.
1. A native Kotlin interface for running tests. (Interfaces natively with Java)
2. A YAML driven test format. Place tests in a YAML format and don't type any Java/Kotlin at all!
    - There are also plans to build a Maven plugin for this. Initially there will be a helper class to load and run all the YAML tests.

There are two libraries that influenced me and my desire to build this library.
1. Ruby's [Airborne](https://github.com/skyscreamer/JSONassert) library which when combined with RSpec make beautiful and lightweight API testing.
2. Java's [Skyscreamer's JSONAsert](https://github.com/skyscreamer/JSONassert) library which made api testing pain a bit easier.

## Why
Testing APIs in Java/Kotlin often involves verbose methodologies following on of three patterns:
1. Assert each field individually from a raw JSON or Map object.
2. Compare expected JSON from a resource like sample_expected.json with a response
3. Mapping responses to POJOs and peforming `equals` checks or checking field by field.

This is an attempt to make writing integration tests more fun and remove some of the pain often associated with writing API integration tests in Java.
I think this is especially important as the world continues to adopt Service Oriented Architectures.

## Where
Structural is available on Maven Central.

TODO


## How

### Kotlin Interface

#### Assert Field Structure
```kotlin
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
Structural.assertStructure(json,
        listOf("name",
                "age",
                Pair("job",
                    listOf("id",
                           "title"))))

// nested array of objects
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
        }
    ]
}
"""
validator.assert(json,
        listOf(Pair("languages",
                listOf("name",
                       "coolness"))))
```


#### Assert Field Type Structure
```kotlin
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
Structural.assertTypes(json,
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
Structural.assertTypes(json,
         listOf(Pair("name", String::class),
                Pair("age", Number::class),
                Pair("shoe_size", Number::class),
                Pair("favorite_number", Number::class),
                Pair("long_number", Number::class),
                Pair("random", Array<Any>::class),
                Pair("job", Object::class),
                Pair("job", listOf(Pair("id", Number::class),
                           Pair("title", String::class)))))

// nested array of objects
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
        }
    ]
}
"""
validator.assert(json,
        listOf(Pair("languages",
                listOf(Pair("name", String::class),
                       Pair("coolness", Number::class)))))
```

#### Assert Field Values
```kotlin
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
Structural.assertValues(json,
         listOf(Pair("name", "kenny"),
                Pair("age", 64),
                Pair("shoe_size", 10.5),
                Pair("favorite_number", 2.718281828459045235),
                Pair("long_number", 1223235345342348),
                Pair("job", listOf(Pair("id", 123456),
                                   Pair("title", "Software Engineer")))))

// only match partial
Structural.assertValues(json,
         listOf(Pair("name", "kenny"),
                Pair("favorite_number", 2.718281828459045235)))

// simple array example
val json = """
{
    "numbers": [1,2,3,4,5,6]
}
"""
Structural.assertValues(json,
                listOf(Pair("numbers", arrayOf(1, 2, 3, 4, 5, 6))))


// nested array example
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
                listOf(Pair("favorite_language", "kotlin")))))
```


## When
NOW


## Notes
Currently the project has a hard dependency on Retrofit and Jackson Json parsing. Eventually these may be extracted out so that you can choose your library.
