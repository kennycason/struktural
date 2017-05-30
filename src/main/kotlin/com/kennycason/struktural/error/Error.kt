package com.kennycason.struktural.error

import com.kennycason.struktural.Mode

/**
 * Created by kenny on 5/24/17.
 */
data class Error(val mode: Mode,
                 val message: String) {
    override fun toString() = "Error in test. Mode: $mode, Reason: $message"
}