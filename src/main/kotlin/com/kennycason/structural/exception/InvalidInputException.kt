package com.kennycason.structural.exception

/**
 * Functionally equivalent to IllegalArgumentException.
 *
 * Created to make exception handling more convenient.
 */
class InvalidInputException(message: String) : StructuralException(message)