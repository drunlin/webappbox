package com.github.drunlin.webappbox.fragment

import android.app.Dialog
import android.os.Bundle
import android.support.v7.app.AlertDialog
import android.view.View
import com.github.drunlin.webappbox.R
import com.github.drunlin.webappbox.common.ARGUMENT_COLOR
import com.thebluealliance.spectrum.SpectrumPalette

class ColorPickerFragment() : CustomViewDialogFragment() {
    override val layoutResId = R.layout.dialog_color_picker

    constructor(color: Int) : this() {
        arguments = Bundle().apply { putInt(ARGUMENT_COLOR, color) }
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return AlertDialog.Builder(context)
                .setTitle(R.string.status_bar_color)
                .setNegativeButton(R.string.cancel, null)
                .create()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val palette = view.findViewById(R.id.palette) as SpectrumPalette
        palette.setSelectedColor(arguments.getInt(ARGUMENT_COLOR))
        palette.setOnColorSelectedListener {
            dismiss()
            (parentFragment as OnColorSelectedListener?)?.onColorSelected(it)
        }
    }

    interface OnColorSelectedListener {
        fun onColorSelected(color: Int)
    }
}
