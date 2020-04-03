package com.example.arcore.data.local.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import com.example.arcore.data.local.entity.ImageEntity

@Dao
interface ImagesDao {

    @Query("SELECT * FROM images")
    fun getImages(): LiveData<List<ImageEntity>>

    @Insert
    fun insert(image: ImageEntity)

    @Update(onConflict = OnConflictStrategy.REPLACE)
    fun update(image: ImageEntity)

    @Delete
    fun delete(image: ImageEntity)
}