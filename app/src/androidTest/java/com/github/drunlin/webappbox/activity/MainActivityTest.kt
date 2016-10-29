package com.github.drunlin.webappbox.activity

import android.support.test.rule.ActivityTestRule
import android.support.test.runner.AndroidJUnit4
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class MainActivityTest {
    @Rule @JvmField val rule = ActivityTestRule(MainActivity::class.java)

    @Test
    fun start() {
        Thread.sleep(Long.MAX_VALUE)
    }
}
