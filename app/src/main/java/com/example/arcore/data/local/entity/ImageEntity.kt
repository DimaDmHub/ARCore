package com.example.arcore.data.local.entity

import android.net.Uri
import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.android.parcel.Parcelize

@Parcelize
@Entity(tableName = "images")
data class ImageEntity constructor(
    val image: Uri,
    val name: String? = null,
    @PrimaryKey(autoGenerate = true) var id: Int = 0
) : Parcelable