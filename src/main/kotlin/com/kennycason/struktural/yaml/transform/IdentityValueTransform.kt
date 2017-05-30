package com.kennycason.struktural.yaml.transform

import com.kennycason.struktural.exception.InvalidInputException

/**
 * Created by kenny on 5/26/17.
 */
class IdentityValueTransform : ValueTransform {
    override fun transform(value: Any) = value
}