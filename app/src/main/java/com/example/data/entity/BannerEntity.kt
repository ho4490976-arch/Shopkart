package com.example.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "banners")
data class BannerEntity(
    @PrimaryKey val id: String,
    val title: String,
    val subtitle: String,
    val imageUrl: String,
    val categoryTarget: String = ""
)
