package com.github.drunlin.webappbox.fragment

import android.support.test.rule.ActivityTestRule
import android.support.test.runner.AndroidJUnit4
import com.github.drunlin.webappbox.activity.FragmentActivity
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class ManualFragmentTest {
    @Rule @JvmField val rule = ActivityTestRule(FragmentActivity::class.java)

    @Before
    fun setUp() {
        rule.activity.setContentFragment(ManualFragment())
    }

    @Test
    fun start() {
        Thread.sleep(Long.MAX_VALUE)
    }
}
