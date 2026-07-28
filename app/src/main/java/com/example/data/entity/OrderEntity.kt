package com.example.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "orders")
data class OrderEntity(
    @PrimaryKey val orderId: String,
    val orderNumber: String,
    val date: Long = System.currentTimeMillis(),
    val totalAmount: Double,
    val subtotalAmount: Double,
    val gstAmount: Double,
    val discountAmount: Double,
    val couponCode: String = "",
    val paymentMethod: String, // "Razorpay UPI/Card" or "Cash On Delivery"
    val paymentStatus: String = "SUCCESS", // "SUCCESS", "PENDING"
    val orderStatus: String = "Placed", // "Placed", "Packed", "Shipped", "Out for Delivery", "Delivered"
    val trackingStep: Int = 1, // 1: Placed, 2: Packed, 3: Shipped, 4: Delivered
    val deliveryAddress: String,
    val customerName: String,
    val customerPhone: String
)
