package com.github.drunlin.webappbox.fragment

import android.os.Bundle
import com.github.drunlin.webappbox.BR
import com.github.drunlin.webappbox.R
import com.github.drunlin.webappbox.common.showDialog
import com.github.drunlin.webappbox.data.URLPattern
import com.github.drunlin.webappbox.model.PatternManager
import javax.inject.Inject

class PatternsFragment : ListFragment<URLPattern, PatternManager>() {
    @Inject override lateinit var manager: PatternManager

    override val titleResId = R.string.url_patterns
    override val itemResId = R.layout.item_pattern

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        (context as WebappContext).component.inject(this)
    }

    override fun onInsert() {
        showDialog(PatternEditorFragment())
    }

    override fun ListFragment<URLPattern, PatternManager>.ViewHolder.onItemClick() {
        showDialog(PatternEditorFragment(data!!.id))
    }

    override fun ListFragment<URLPattern, PatternManager>.ViewHolder.onBindItem(data: URLPattern) {
        binding.setVariable(BR.pattern, data)
    }
}
