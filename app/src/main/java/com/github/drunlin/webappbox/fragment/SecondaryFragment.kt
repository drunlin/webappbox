package com.github.drunlin.webappbox.fragment

import android.os.Bundle
import android.view.View
import com.github.drunlin.webappbox.R
import com.github.drunlin.webappbox.common.getResourceId
import kotlinx.android.synthetic.main.toolbar.*

abstract class SecondaryFragment : AppBarFragment() {
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        toolbar.setNavigationIcon(context.getResourceId(R.attr.homeAsUpIndicator))
        toolbar.setNavigationOnClickListener { activity.onBackPressed() }
    }
}
