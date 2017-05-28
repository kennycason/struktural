package com.kennycason.structural.yaml.transform

/**
 * Created by kenny on 5/26/17.
 */
interface ValueTransform {
    fun transform(value: Any): Any
}