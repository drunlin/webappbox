package com.github.drunlin.webappbox.activity

import android.os.Build
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.View

abstract class TranslucentStatusActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
            window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
    }
}
