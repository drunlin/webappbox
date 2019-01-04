package com.github.drunlin.webappbox.model

import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.After
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RuntimeEnvironment

@RunWith(AndroidJUnit4::class)
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
