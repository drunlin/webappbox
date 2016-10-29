package com.github.drunlin.webappbox

import android.support.test.rule.ActivityTestRule
import android.support.v4.app.FragmentManager
import android.support.v7.app.AppCompatActivity

val ActivityTestRule<out AppCompatActivity>.fragmentManager: FragmentManager
    get() = activity.supportFragmentManager
