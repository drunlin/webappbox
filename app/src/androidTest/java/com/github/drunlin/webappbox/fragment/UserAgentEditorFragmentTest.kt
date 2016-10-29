package com.github.drunlin.webappbox.fragment

import android.support.test.rule.ActivityTestRule
import android.support.test.runner.AndroidJUnit4
import android.support.v7.app.AppCompatActivity
import com.github.drunlin.webappbox.common.show
import com.github.drunlin.webappbox.fragmentManager
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class UserAgentEditorFragmentTest {
    @Rule @JvmField val rule = ActivityTestRule(AppCompatActivity::class.java)

    @Before
    fun setUp() {
        UserAgentEditorFragment().show(rule.fragmentManager)
    }

    @Test
    fun start() {
        Thread.sleep(Long.MAX_VALUE)
    }
}
