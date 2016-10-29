package com.github.drunlin.webappbox.fragment

import android.content.Context
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.FrameLayout
import com.github.drunlin.webappbox.R
import com.github.drunlin.webappbox.activity.FragmentActivity
import com.github.drunlin.webappbox.common.BUNDLE_COUNT
import com.github.drunlin.webappbox.common.findNullableIndexedValue
import com.github.drunlin.webappbox.common.getSystemService
import com.github.drunlin.webappbox.data.LaunchMode
import com.github.drunlin.webappbox.model.RuleManager
import com.github.drunlin.webappbox.model.WebappModel
import java.util.*
import javax.inject.Inject

class WebappFragment : Fragment() {
    @Inject lateinit var ruleManager: RuleManager
    @Inject lateinit var webappModel: WebappModel

    var onUrlChange: ((String) -> Unit)? = null

    private val webapp by lazy { webappModel.webapp }

    private val fragments = LinkedList<WebappWindowFragment>()
    private val topFragment: WebappWindowFragment get() = fragments.last
    
    private val activity: FragmentActivity get() =  getActivity() as FragmentActivity

    private var pendingUrl: String? = null

    init {
        retainInstance = true
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        (context as WebappContext).component.inject(this)

        repeat(savedInstanceState?.getInt(BUNDLE_COUNT) ?: 0) {
            fragments.add(childFragmentManager
                    .getFragment(savedInstanceState, key(it)) as WebappWindowFragment)
        }
    }

    private fun key(index: Int) = "${WebappWindowFragment::class.java.simpleName}$index"

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)

        for ((i, fragment) in fragments.withIndex()) {
            childFragmentManager.putFragment(outState, key(i), fragment)
        }
        outState.putInt(BUNDLE_COUNT, fragments.size)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): FrameLayout {
        return FrameLayout(context).apply { id = R.id.content }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        if (pendingUrl != null) {
            loadUrl(if (pendingUrl!!.isEmpty()) webapp.url else pendingUrl!!)
            pendingUrl = null
        } else if (savedInstanceState == null) {
            loadUrl(webapp.url)
        }
    }

    fun loadUrl(url: String) {
        if (!isAdded) {
            pendingUrl = url
            return
        }
        fragments.clear()
        onLoadUrl(url)
    }

    fun onLoadUrl(url: String): Boolean {
        val rule = ruleManager.getRule(url)
        val (index, fragment) = fragments.findNullableIndexedValue { it.rule == rule }

        if (fragment == null || rule.launchMode == LaunchMode.NEW_WINDOW) {
            push(WebappWindowFragment(url))
        } else if (fragment == topFragment) {
            fragment.loadUrl(url)
            return false
        } else if(rule.launchMode == LaunchMode.STANDARD || rule.launchMode == LaunchMode.SINGLE_TOP) {
            push(WebappWindowFragment(url))
        } else {
            @Suppress("NON_EXHAUSTIVE_WHEN")
            when (rule.launchMode) {
                LaunchMode.CLEAR_TOP -> clearTop(index)
                LaunchMode.SINGLE_TASK -> moveToTop(index)
            }
            fragment.loadUrl(url)
        }
        return true
    }

    private fun hideSoftKeyboard() {
        if (isAdded && activity.supportFragmentManager == fragmentManager) {
            val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(view!!.windowToken, 0)
        }
    }

    private fun push(fragment: WebappWindowFragment) {
        hideSoftKeyboard()

        childFragmentManager.beginTransaction().apply {
            if (fragments.isNotEmpty()) {
                setCustomAnimations(0, activity.openExitAnimation)
                hide(topFragment)
                setCustomAnimations(activity.openEnterAnimation, 0)
            }
            add(R.id.content, fragment)
        }.commit()

        fragments.add(fragment)
    }

    private fun clearTop(index: Int) {
        if (index == fragments.lastIndex) return

        hideSoftKeyboard()

        childFragmentManager.beginTransaction()
                .setCustomAnimations(0, activity.openExitAnimation)
                .remove(fragments.removeLast())
                .setCustomAnimations(0, 0)
                .apply { repeat(fragments.lastIndex - index) { remove(fragments.removeLast()) } }
                .setCustomAnimations(activity.openEnterAnimation, 0)
                .show(topFragment)
                .commit()
    }

    private fun pop() {
        hideSoftKeyboard()

        childFragmentManager.beginTransaction()
                .setCustomAnimations(0, activity.closeExitAnimation)
                .remove(fragments.removeLast())
                .setCustomAnimations(activity.closeEnterAnimation, 0)
                .show(topFragment)
                .commit()

        onUrlChange?.invoke(topFragment.currentUrl ?: "")
    }

    private fun moveToTop(index: Int) {
        if (index == fragments.lastIndex) return

        hideSoftKeyboard()

        childFragmentManager.beginTransaction()
                .setCustomAnimations(0, activity.openExitAnimation)
                .hide(topFragment)
                .setCustomAnimations(activity.openEnterAnimation, 0)
                .show(fragments[index])
                .commit()

        Collections.swap(fragments, index, fragments.lastIndex)
    }

    fun goBack() = if (fragments.size == 1) activity.onBackPressed() else pop()
}
