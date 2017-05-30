package com.kennycason.struktural.yaml

import com.kennycason.struktural.Mode
import com.kennycason.struktural.data.JsonLoader

/**
 * Created by kenny on 5/25/17.
 */
data class TestCase(val config: Config,
                    val mode: Mode,
                    val jsonLoader: JsonLoader,
                    val expects: Iterable<Any>)