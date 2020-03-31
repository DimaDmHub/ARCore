package com.example.arcore.ui.main

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.fragment.app.commit
import com.example.arcore.R
import com.example.arcore.ui.main.fragment.camera.CameraFragment
import com.example.arcore.ui.main.fragment.welcome.WelcomeFragment

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        handleStartDestination()
    }

    private fun handleStartDestination() {
        supportFragmentManager.commit {
            add(R.id.fcvMainActivity, WelcomeFragment())
        }
    }
}
