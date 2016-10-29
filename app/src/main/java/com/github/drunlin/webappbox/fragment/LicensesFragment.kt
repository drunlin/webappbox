package com.github.drunlin.webappbox.fragment

import android.os.Bundle
import android.view.View
import com.github.drunlin.webappbox.R
import com.github.drunlin.webappbox.common.getRawText
import kotlinx.android.synthetic.main.text_content.*

class LicensesFragment : SecondaryFragment() {
    override val titleResId = R.string.licenses
    override val contentViewResId = R.layout.text_content

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        text.text = context.getRawText(R.raw.licenses)
    }
}
