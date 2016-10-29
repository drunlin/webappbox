package com.github.drunlin.webappbox.fragment

import android.support.test.rule.ActivityTestRule
import android.support.test.runner.AndroidJUnit4
import com.github.drunlin.webappbox.activity.WebappContextActivity
import com.github.drunlin.webappbox.common.show
import com.github.drunlin.webappbox.fragmentManager
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class PatternEditorFragmentTest {
    @Rule @JvmField val rule = ActivityTestRule(WebappContextActivity::class.java)

    @Before
    fun setUp() {
        PatternEditorFragment().show(rule.fragmentManager)
    }

    @Test
    fun start() {
        Thread.sleep(Long.MAX_VALUE)
    }
}
