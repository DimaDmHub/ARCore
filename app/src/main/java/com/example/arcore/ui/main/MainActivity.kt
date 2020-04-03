package com.example.arcore.ui.main

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.commit
import com.example.arcore.R
import com.example.arcore.ui.main.fragment.camera.CameraFragment
import com.example.arcore.ui.main.fragment.list.ImagesListFragment
import com.example.arcore.ui.main.fragment.welcome.WelcomeFragment

class MainActivity : AppCompatActivity(), MainNavigationActivity {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        handleStartDestination()
    }

    override fun navigateToImagesList() {
        supportFragmentManager.commit {
            replace(R.id.fcvMainActivity, ImagesListFragment())
        }
    }

    override fun navigateToCamera() {
        supportFragmentManager.commit {
            addToBackStack(null)
            replace(R.id.fcvMainActivity, CameraFragment())
        }
    }

    private fun handleStartDestination() {
        supportFragmentManager.commit {
            add(R.id.fcvMainActivity, WelcomeFragment())
        }
    }
}
