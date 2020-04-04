package com.example.arcore.ui.main

import com.example.arcore.data.local.entity.ImageEntity

interface MainNavigationActivity {

    fun navigateToImagesList()

    fun navigateToCamera(images: ArrayList<ImageEntity>)
}