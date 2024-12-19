package com.kennycason.struktural.yaml.transform

import com.kennycason.struktural.Mode

/**
 * This class transforms data from:
 * List<Map<String, Any>> to (User friendly) List<Pair<String, Any>>
 *
 * This is used because the Yaml to Map loader defaults to this format,
 * however Struktural provides a different interface
 *
 *  TODO this model interface needs to be cleaned up.
 */
class ExpectsMapTransform(mode: Mode) {
    private val valueTransform = selectValueTransform(mode)

    // for use in "structure" mode
    fun transformToAny(from: Iterable<Any>): Iterable<Any> {
        val transformed = mutableListOf<Any>()
        from.forEach { item ->
            if (item is String) {
                transformed.add(item)
            } else if (item is Map<*, *>) {
                item.entries.forEach { entry ->
                    transformed.add(Pair(entry.key, transformToAny(entry.value as Iterable<Any>)))
                }
            }
        }
        return transformed
    }

    // for use in "type" and "value" mode
    fun transformToPairs(from: Iterable<Map<String, Any>>): Iterable<Pair<String, Any>> {
        val transformed = mutableListOf<Pair<String, Any>>()
        from.forEach { item ->
            transformed.addAll(transformToPairs(item))
        }
        return transformed
    }

    private fun transformToPairs(from: Map<String, Any>): Iterable<Pair<String, Any>> {
        val map = mutableListOf<Pair<String, Any>>()
        from.entries.forEach { entry ->
            if (entry.value is Map<*, *>) {
                map.add(Pair(entry.key, transformToPairs(entry.value as Map<String, Any>)))
            } else {
                map.add(Pair(entry.key, valueTransform.transform(entry.value)))
            }
        }
        return map
    }

    private fun selectValueTransform(mode: Mode) = when (mode) {
        Mode.TYPE -> TypeValueTransform()
        else -> IdentityValueTransform()
    }
}
