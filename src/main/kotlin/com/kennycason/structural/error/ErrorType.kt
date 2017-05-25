package com.kennycason.structural.error

/**
 * Created by kenny on 5/24/17.
 */
enum class ErrorType {
    /**
     * The field is missing in the json block
     */
    MISSING,
    /**
     * The asserted type of the field is different.
     */
    TYPE,
    /**
     * The asserted value does not Equal.
     */
    VALUE
}