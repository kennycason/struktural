package com.kennycason.structural

import com.kennycason.structural.error.Error

/**
 * Created by kenny on 5/24/17.
 */
data class ValidationResult(val valid: Boolean, val errors: List<Error>)