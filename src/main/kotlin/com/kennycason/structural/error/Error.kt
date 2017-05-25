package com.kennycason.structural.error

/**
 * Created by kenny on 5/24/17.
 */
data class Error(val type: ErrorType, val message: String) {
    override fun toString() = "$type: $message"
}