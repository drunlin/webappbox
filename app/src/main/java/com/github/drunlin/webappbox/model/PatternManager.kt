package com.github.drunlin.webappbox.model

import com.github.drunlin.webappbox.common.findIndexedValue
import com.github.drunlin.webappbox.common.generateId
import com.github.drunlin.webappbox.data.URLPattern

open class PatternManager : DataManager<URLPattern>() {
    val patterns by lazy { data }

    fun insert(value: String, regex: Boolean) {
        insert(URLPattern(generateId(), value, regex))
    }

    fun update(id: Long, value: String, regex: Boolean) {
        val (index, pattern) = patterns.findIndexedValue { it.id == id }
        pattern.pattern = value
        pattern.regex = regex
        update(index, pattern)
    }

    fun matches(url: String) = patterns.isEmpty() || patterns.any { it.matches(url) }

    fun getPattern(id: Long) = patterns.find { it.id == id }!!

    fun isExited(pattern: String, regex: Boolean)
            = patterns.any { it.regex == regex && it.pattern == pattern }
}
