package com.kennycason.struktural.yaml.transform

/**
 * Created by kenny on 5/26/17.
 */
interface ValueTransform {
    fun transform(value: Any): Any
}