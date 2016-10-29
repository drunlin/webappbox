package com.github.drunlin.webappbox.model

import com.github.drunlin.webappbox.common.Callback

abstract class ObservableModel {
    val onInsert: Callback<(Int) -> Unit> = Callback()
    val onUpdate: Callback<(Int) -> Unit> = Callback()
}
