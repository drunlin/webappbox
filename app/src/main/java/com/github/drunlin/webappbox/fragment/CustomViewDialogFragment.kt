package com.github.drunlin.webappbox.fragment

import android.os.Bundle
import android.support.v4.app.DialogFragment
import android.support.v7.app.AlertDialog
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

abstract class CustomViewDialogFragment : DialogFragment() {
    abstract protected val layoutResId: Int

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(layoutResId, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        (dialog as? AlertDialog)?.setView(view)

        super.onActivityCreated(savedInstanceState)
    }
}
