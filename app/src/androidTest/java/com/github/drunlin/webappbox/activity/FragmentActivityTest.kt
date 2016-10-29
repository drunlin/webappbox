package com.github.drunlin.webappbox.activity

import android.support.test.rule.ActivityTestRule
import android.support.test.runner.AndroidJUnit4
import org.hamcrest.core.AllOf.allOf
import org.hamcrest.core.IsNot.not
import org.junit.Assert.assertThat
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class FragmentActivityTest {
    @Rule @JvmField val rule = ActivityTestRule(FragmentActivity::class.java)

    @Test
    fun obtainActivityAnimation() {
        with(rule.activity) {
            assertThat(openEnterAnimation, allOf(not(0), not(android.R.anim.fade_in)))
            assertThat(openExitAnimation, allOf(not(0), not(android.R.anim.fade_out)))
            assertThat(closeEnterAnimation, allOf(not(0), not(android.R.anim.fade_in)))
            assertThat(closeExitAnimation, allOf(not(0), not(android.R.anim.fade_out)))
        }
    }
}
