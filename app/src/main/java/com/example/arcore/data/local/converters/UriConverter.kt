package com.example.arcore.data.local.converters

import android.net.Uri
import androidx.room.TypeConverter

class UriConverter {

    @TypeConverter
    fun fromUri(uri: Uri): String {
        return uri.toString()
    }

    @TypeConverter
    fun toUri(data: String): Uri {
        return Uri.parse(data)
    }
}