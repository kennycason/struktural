package com.kennycason.struktural

import com.kennycason.struktural.error.Error

/**
 * Created by kenny on 5/24/17.
 */
data class ValidationResult(val valid: Boolean, val errors: List<Error>)