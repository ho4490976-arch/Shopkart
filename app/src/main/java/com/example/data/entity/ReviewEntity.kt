package com.example.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "reviews")
data class ReviewEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val productId: String,
    val userName: String,
    val rating: Float,
    val comment: String,
    val date: String = "Today",
    val isVerifiedPurchase: Boolean = true
)
