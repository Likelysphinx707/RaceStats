package com.example.racestats

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity

class Navigation : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.nav)

        // Set the activity to full-screen mode
        window.decorView.systemUiVisibility = (View.SYSTEM_UI_FLAG_FULLSCREEN or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION)


    }
}