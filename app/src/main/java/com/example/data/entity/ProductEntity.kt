package com.example.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "products")
data class ProductEntity(
    @PrimaryKey val id: String,
    val name: String,
    val category: String,
    val brand: String,
    val price: Double,
    val originalPrice: Double,
    val discountPercent: Int,
    val rating: Float,
    val reviewCount: Int,
    val stock: Int,
    val description: String,
    val imageUrl: String,
    val isFeatured: Boolean = false,
    val isFlashSale: Boolean = false,
    val gstPercentage: Double = 18.0,
    val tags: String = "" // comma separated tags
)
