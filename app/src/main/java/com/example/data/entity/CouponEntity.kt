package com.example.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "coupons")
data class CouponEntity(
    @PrimaryKey val code: String,
    val discountPercent: Int,
    val maxDiscountAmount: Double,
    val minOrderAmount: Double,
    val description: String,
    val isActive: Boolean = true
)
