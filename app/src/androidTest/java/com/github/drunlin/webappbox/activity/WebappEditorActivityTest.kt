package com.github.drunlin.webappbox.activity

import android.support.test.rule.ActivityTestRule
import android.support.test.runner.AndroidJUnit4
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class WebappEditorActivityTest {
    @Rule @JvmField val rule = ActivityTestRule(WebappEditorActivity::class.java, false, false)

    @Before
    fun setUp() {
        rule.launchActivity(WebappEditorActivity.new())
    }

    @Test
    fun start() {
        Thread.sleep(Long.MAX_VALUE)
    }
}
