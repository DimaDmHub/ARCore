package com.example.arcore.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.arcore.data.local.converters.UriConverter
import com.example.arcore.data.local.dao.ImagesDao
import com.example.arcore.data.local.entity.ImageEntity

private const val VERSION = 1

@Database(
    version = VERSION,
    entities = [ImageEntity::class],
    exportSchema = false
)
@TypeConverters(UriConverter::class)
abstract class Database : RoomDatabase() {

    companion object {

        internal const val NAME = "database"
    }

    abstract fun getImagesDao(): ImagesDao
}