package com.github.drunlin.webappbox.data

data class UserAgent(override var id: Long, var name: String, var value: String) : Unique
