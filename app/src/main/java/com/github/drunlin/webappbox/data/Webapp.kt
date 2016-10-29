package com.github.drunlin.webappbox.data

import android.graphics.Bitmap

data class Webapp(override var id: Long,
                  var url: String,
                  var icon: Bitmap,
                  var name: String,
                  var locationPolicy: Policy) : Unique
