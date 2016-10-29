package com.github.drunlin.webappbox.model

import org.junit.Assert.assertNotNull
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.RuntimeEnvironment

@RunWith(RobolectricTestRunner::class)
class IconLoaderTest {
    private lateinit var loader: IconLoader

    @Before
    fun setUp() {
        loader = IconLoader()
        loader.context = RuntimeEnvironment.application
    }

    @Test
    fun load() {
        assertNotNull(loader.load("https://www.baidu.com/"))
    }
}
