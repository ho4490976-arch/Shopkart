package com.example.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "wishlist")
data class WishlistItemEntity(
    @PrimaryKey val productId: String,
    val addedAt: Long = System.currentTimeMillis()
)
