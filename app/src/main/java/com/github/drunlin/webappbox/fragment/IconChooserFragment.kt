package com.github.drunlin.webappbox.fragment

import android.app.Dialog
import android.os.Bundle
import android.support.v4.app.DialogFragment
import android.support.v7.app.AlertDialog
import com.github.drunlin.webappbox.R

class IconChooserFragment : DialogFragment() {
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return AlertDialog.Builder(activity)
                .setTitle(R.string.change_icon)
                .setItems(R.array.icon_chooser) { dialog, which -> onClick(which) }
                .setNegativeButton(R.string.cancel, null)
                .create()
    }

    private fun onClick(which: Int) {
        (parentFragment as OnSelectedListener?)?.onSelected(which)
    }

    interface OnSelectedListener {
        fun onSelected(which: Int)
    }
}
