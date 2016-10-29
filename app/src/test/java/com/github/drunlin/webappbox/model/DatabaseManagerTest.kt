package com.github.drunlin.webappbox.model

import android.os.Build
import com.github.drunlin.webappbox.BuildConfig
import org.junit.After
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricGradleTestRunner
import org.robolectric.RuntimeEnvironment
import org.robolectric.annotation.Config

@Config(constants = BuildConfig::class, sdk = intArrayOf(Build.VERSION_CODES.LOLLIPOP))
@RunWith(RobolectricGradleTestRunner::class)
class DatabaseManagerTest {
    lateinit var databaseManager: DatabaseManager

    @Before
    fun setUp() {
        databaseManager = DatabaseManager(RuntimeEnvironment.application)
    }

    @After
    fun teardown() {
        databaseManager.close()
    }

    @Test
    fun onCreate() {
        assertNotNull(databaseManager.readableDatabase)
        assertTrue(databaseManager.getShortcuts().size > 0)
    }
}
