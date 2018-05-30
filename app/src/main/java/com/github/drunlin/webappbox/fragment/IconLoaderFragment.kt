package com.github.drunlin.webappbox.fragment

import android.app.Dialog
import android.graphics.Bitmap
import android.os.Bundle
import android.support.v4.app.DialogFragment
import android.support.v7.app.AlertDialog
import com.github.drunlin.webappbox.R
import com.github.drunlin.webappbox.common.ARGUMENT_URL
import com.github.drunlin.webappbox.common.app
import com.github.drunlin.webappbox.common.asyncCall
import com.github.drunlin.webappbox.model.IconLoader
import javax.inject.Inject

class IconLoaderFragment() : DialogFragment() {
    @Inject lateinit var loader: IconLoader

    init {
        retainInstance = true
    }

    constructor(url: String) : this() {
        arguments = Bundle().apply { putString(ARGUMENT_URL, url) }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        app.component.inject(this)

        asyncCall({ loader.load(arguments!!.getString(ARGUMENT_URL)) }) {
            dismiss()
            if (!loader.canceled) (parentFragment as OnIconLoadedListener?)?.onIconLoaded(it)
        }
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return AlertDialog.Builder(context!!)
                .setTitle(R.string.please_wait)
                .setView(R.layout.dialog_progress)
                .create()
    }

    override fun onDestroy() {
        super.onDestroy()

        loader.cancel()
    }

    interface OnIconLoadedListener {
        fun onIconLoaded(icon: Bitmap?)
    }
}
