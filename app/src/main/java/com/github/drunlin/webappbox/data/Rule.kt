package com.github.drunlin.webappbox.data

data class Rule(override var id: Long,
                var pattern: URLPattern,
                var color: Int,
                var launchMode: LaunchMode,
                var orientation: Orientation,
                var fullScreen: Boolean,
                var userAgent: UserAgent,
                var jsEnabled: Boolean) : Unique
