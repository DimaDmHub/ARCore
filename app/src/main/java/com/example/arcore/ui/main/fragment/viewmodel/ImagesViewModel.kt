package com.example.arcore.ui.main.fragment.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import androidx.room.Room
import com.example.arcore.data.local.Database
import com.example.arcore.data.local.Database.Companion.NAME
import com.example.arcore.data.local.dao.ImagesDao
import com.example.arcore.data.local.entity.ImageEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class ImagesViewModel constructor(application: Application) : AndroidViewModel(application) {

    private val imagesDao: ImagesDao = Room.databaseBuilder(application, Database::class.java, NAME)
        .build()
        .getImagesDao()

    val imagesData: LiveData<List<ImageEntity>> get() = imagesDao.getImages()

    fun addImage(image: ImageEntity) {
        viewModelScope.launch(Dispatchers.IO) {
            imagesDao.insert(image)
        }
    }

    fun updateImage(image: ImageEntity) {
        viewModelScope.launch(Dispatchers.IO) {
            imagesDao.update(image)
        }
    }

    fun deleteImage(image: ImageEntity) {
        viewModelScope.launch(Dispatchers.IO) {
            imagesDao.delete(image)
        }
    }
}