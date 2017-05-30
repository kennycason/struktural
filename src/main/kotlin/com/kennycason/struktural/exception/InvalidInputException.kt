package com.kennycason.struktural.exception

/**
 * Functionally equivalent to IllegalArgumentException.
 *
 * Created to make exception handling more convenient.
 */
class InvalidInputException(message: String) : StrukturalException(message)