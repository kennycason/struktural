package com.kennycason.structural.error

import com.kennycason.structural.Mode

/**
 * Created by kenny on 5/24/17.
 */
data class Error(val mode: Mode,
                 val message: String) {
    override fun toString() = "Error in test. Mode: $mode, Reason: $message"
}