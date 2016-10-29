package com.github.drunlin.webappbox.data

import java.net.URL

data class URLPattern(override var id: Long, var pattern: String, var regex: Boolean) : Unique {
    constructor(pattern: String = ".*", regex: Boolean = true) : this(0, pattern, regex)

    fun matches(url: String) = if (regex) Regex(pattern).matches(url) else URL(pattern) == URL(url)
}
