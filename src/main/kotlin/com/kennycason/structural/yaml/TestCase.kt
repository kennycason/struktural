package com.kennycason.structural.yaml

import com.kennycason.structural.yaml.data.JsonLoader

/**
 * Created by kenny on 5/25/17.
 */
data class TestCase(val config: Config,
                    val mode: Mode,
                    val jsonLoader: JsonLoader,
                    val expects: List<Pair<String, Any>>)