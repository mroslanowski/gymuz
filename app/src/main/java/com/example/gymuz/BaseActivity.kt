package com.example.gymuz

import android.content.Context
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity

open class BaseActivity : AppCompatActivity() {

    override fun attachBaseContext(newBase: Context) {
        // 1. Language is applied here, before the Activity is created.
        super.attachBaseContext(LocaleHelper.onAttach(newBase))
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        // 2. Theme is applied here, before the UI is inflated (setContentView).
        ThemeHelper.applyTheme(this)
        super.onCreate(savedInstanceState)
    }
}