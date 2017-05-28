package com.kennycason.structural.yaml.transform

import com.kennycason.structural.exception.InvalidInputException

/**
 * This class transforms data from:
 * (User friendly) List<Pair<String, Any>> to List<Map<String, Any>>
 * List<Map<String, Any>> to (User friendly) List<Pair<String, Any>>
 *
 *  TODO this model interface needs to be seriously cleaned up.
 *  Also consider replacing all JsonNode parsing to maps as well
 */
class ExpectsMapTransform(private val valueTransform: ValueTransform) {

    fun transform(from: List<Map<String, Any>>): List<Pair<String, Any>> {
        val transformed = mutableListOf<Pair<String, Any>>()
        from.forEach { item ->
            transformed.addAll(transform(item))
        }
        return transformed
    }

    fun transform(from: Map<String, Any>): List<Pair<String, Any>> {
        val map = mutableListOf<Pair<String, Any>>()
        from.entries.forEach { entry ->
            if (entry.value is Map<*, *>) {
                map.add(
                        Pair(entry.key, transform(entry.value as Map<String, Any>)))
            } else {
                map.add(Pair(entry.key, valueTransform.transform(entry.value)))
            }
        }
        return map
    }

}