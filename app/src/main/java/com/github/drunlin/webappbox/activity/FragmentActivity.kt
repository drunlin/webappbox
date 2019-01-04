package com.github.drunlin.webappbox.activity

import android.content.Context
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.MenuItem
import android.view.inputmethod.InputMethodManager
import com.github.drunlin.webappbox.R
import com.github.drunlin.webappbox.common.Callback
import com.github.drunlin.webappbox.common.add
import com.github.drunlin.webappbox.widget.ContentLayout

open class FragmentActivity : TranslucentStatusActivity() {
    val onWindowFocusChanged = Callback<(Boolean) -> Unit>()

    var openEnterAnimation = 0
        private set
    var openExitAnimation = 0
        private set
    var closeEnterAnimation = 0
        private set
    var closeExitAnimation = 0
        private set

    lateinit var contentView: ContentLayout
        private set

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        obtainActivityAnimation()

        contentView = ContentLayout(this).apply { id = R.id.content }
        setContentView(contentView)
    }

    private fun obtainActivityAnimation() {
        val attrs = intArrayOf(
                android.R.attr.activityOpenEnterAnimation,
                android.R.attr.activityOpenExitAnimation,
                android.R.attr.activityCloseEnterAnimation,
                android.R.attr.activityCloseExitAnimation)
        val array = obtainStyledAttributes(android.R.style.Animation_Activity, attrs)
        openEnterAnimation = array.getResourceId(0, android.R.anim.fade_in)
        openExitAnimation = array.getResourceId(1, android.R.anim.fade_out)
        closeEnterAnimation = array.getResourceId(2, android.R.anim.fade_in)
        closeExitAnimation = array.getResourceId(3, android.R.anim.fade_out)
        array.recycle()
    }

    fun setContentFragment(fragment: Fragment) {
        supportFragmentManager.add(R.id.content, fragment)
    }

    fun replaceContentFragment(fragment: Fragment) {
        hideSoftKeyboard()

        val currentFragment = supportFragmentManager.findFragmentById(R.id.content)
        supportFragmentManager.beginTransaction()
                .setCustomAnimations(0, openExitAnimation, closeEnterAnimation, 0)
                .hide(currentFragment!!)
                .setCustomAnimations(openEnterAnimation, 0, 0, closeExitAnimation)
                .add(R.id.content, fragment)
                .addToBackStack(null)
                .commit()
    }

    private fun hideSoftKeyboard() {
        val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(contentView.windowToken, 0)
    }

    override fun onWindowFocusChanged(hasFocus: Boolean) {
        super.onWindowFocusChanged(hasFocus)

        onWindowFocusChanged.invoke { it(hasFocus) }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            onBackPressed()
            return true
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onBackPressed() {
        super.onBackPressed()

        hideSoftKeyboard()
    }
}
