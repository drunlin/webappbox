package com.github.drunlin.webappbox.model

import android.content.Context
import android.graphics.Bitmap
import com.github.drunlin.webappbox.R
import com.github.drunlin.webappbox.common.Callback
import com.github.drunlin.webappbox.common.getBitmap
import com.github.drunlin.webappbox.data.Policy
import com.github.drunlin.webappbox.data.Webapp
import javax.inject.Inject

open class WebappModel(protected val id: Long) {
    @Inject lateinit var context: Context

    val onUrlChange = Callback<(String) -> Unit>()

    open val originalUrl = ""
    open val webapp by lazy { Webapp(id, "", context.getBitmap(R.mipmap.ic_webapp), "", Policy.ASK) }

    open fun setUrl(url: String) {
        webapp.url = url
        onUrlChange.invoke { it(url) }
    }

    open fun setLocationPolicy(policy: Policy) {
        webapp.locationPolicy = policy
    }

    fun update(url: String, icon: Bitmap, name: String, locationPolicy: Policy) {
        webapp.url = url
        webapp.icon = icon
        webapp.name = name
        webapp.locationPolicy = locationPolicy
    }
}
