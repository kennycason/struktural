package com.kennycason.structural

/**
 * Created by kenny on 5/25/17.
 */
enum class Mode {
    /**
     * The field is missing in the json block
     */
    STRUCTURE,
    /**
     * The asserted type of the field is different.
     */
    TYPE,
    /**
     * The asserted value does not Equal.
     */
    VALUE
}