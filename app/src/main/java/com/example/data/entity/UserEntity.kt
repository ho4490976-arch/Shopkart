package com.example.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "users")
data class UserEntity(
    @PrimaryKey val id: String = "user_default_101",
    val name: String = "Rahul Sharma",
    val email: String = "rahul.sharma@example.com",
    val phone: String = "+91 98765 43210",
    val referralCode: String = "RAHUL500",
    val rewardPoints: Int = 1250,
    val walletBalance: Double = 350.0,
    val isLoggedIn: Boolean = true,
    val role: String = "customer", // "customer" or "admin"
    val addressName: String = "Rahul Sharma",
    val addressStreet: String = "42, MG Road, Koramangala",
    val addressCity: String = "Bengaluru",
    val addressState: String = "Karnataka",
    val addressPincode: String = "560034"
)
