package com.kennycason.structural.yaml.transform

import com.kennycason.structural.exception.InvalidInputException

/**
 * Created by kenny on 5/26/17.
 */
class TypeValueTransform : ValueTransform {

    override fun transform(value: Any) = when (value) {
        "string" -> String::class
        "int" -> Int::class
        "long" -> Long::class
        "float" -> Float::class
        "double" -> Double::class
        "number" -> Number::class
        "bool" -> Boolean::class
        "object" -> Any::class
        else -> throw InvalidInputException("Invalid value for type. Must be one of string,int,long,float,double,number,bool,object")
    }

}