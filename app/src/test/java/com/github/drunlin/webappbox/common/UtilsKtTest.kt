package com.github.drunlin.webappbox.common

import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class UtilsKtTest {
    @Test
    fun isValidUrl() {
        assertTrue("http://localhost:8888".isValidUrl())
    }
}
