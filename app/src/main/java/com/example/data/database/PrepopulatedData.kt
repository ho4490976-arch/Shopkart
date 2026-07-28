package com.example.data.database

import com.example.data.entity.BannerEntity
import com.example.data.entity.CategoryEntity
import com.example.data.entity.CouponEntity
import com.example.data.entity.ProductEntity
import com.example.data.entity.ReviewEntity
import com.example.data.entity.UserEntity

object PrepopulatedData {
    val initialUser = UserEntity(
        id = "user_default_101",
        name = "Rahul Sharma",
        email = "rahul.sharma@example.com",
        phone = "+91 98765 43210",
        referralCode = "RAHUL500",
        rewardPoints = 850,
        walletBalance = 250.0,
        isLoggedIn = true,
        role = "customer",
        addressName = "Rahul Sharma",
        addressStreet = "42, MG Road, Indiranagar",
        addressCity = "Bengaluru",
        addressState = "Karnataka",
        addressPincode = "560038"
    )

    val categories = listOf(
        CategoryEntity("cat_electronics", "Electronics", "ic_electronics", "Up to 50% Off Top Brands"),
        CategoryEntity("cat_fashion", "Fashion", "ic_fashion", "Trendy Wear & Sneakers"),
        CategoryEntity("cat_mobiles", "Mobiles", "ic_mobiles", "Latest 5G Smartphones"),
        CategoryEntity("cat_home", "Home & Kitchen", "ic_home", "Smart Home & Appliances"),
        CategoryEntity("cat_appliances", "Appliances", "ic_appliances", "Cooling & Kitchen Essentials"),
        CategoryEntity("cat_beauty", "Beauty & Care", "ic_beauty", "Grooming & Skincare Offers")
    )

    val banners = listOf(
        BannerEntity(
            id = "b1",
            title = "MEGA FESTIVE SALE",
            subtitle = "Up to 70% Off on Top Tech & Accessories + Bank Offers!",
            imageUrl = "img_hero_banner_1785162873080",
            categoryTarget = "cat_electronics"
        ),
        BannerEntity(
            id = "b2",
            title = "FLASH DEALS HOUR",
            subtitle = "Extra ₹500 Instant Cashback on UPI Payments!",
            imageUrl = "img_hero_banner_1785162873080",
            categoryTarget = "cat_mobiles"
        )
    )

    val coupons = listOf(
        CouponEntity("WELCOME100", 20, 100.0, 499.0, "Get 20% OFF up to ₹100 on first order!", true),
        CouponEntity("FESTIVE500", 15, 500.0, 1999.0, "15% OFF up to ₹500 on festive electronics", true),
        CouponEntity("FLAT200", 10, 200.0, 999.0, "Flat ₹200 discount on cart value above ₹999", true),
        CouponEntity("FREESHIP", 100, 50.0, 299.0, "Free delivery on orders above ₹299", true)
    )

    val products = listOf(
        ProductEntity(
            id = "p101",
            name = "AudioPro Wireless ANC Headphones Pro 5",
            category = "Electronics",
            brand = "AudioPro",
            price = 2499.0,
            originalPrice = 4999.0,
            discountPercent = 50,
            rating = 4.7f,
            reviewCount = 1420,
            stock = 25,
            description = "Active Noise Cancellation (ANC), 40 Hours playtime, Bluetooth 5.3 with ultra-low latency gaming mode, dual mic crystal clear calls, dynamic bass boost driver.",
            imageUrl = "https://images.unsplash.com/photo-1505740420928-5e560c06d30e?w=500&auto=format&fit=crop&q=60",
            isFeatured = true,
            isFlashSale = true,
            gstPercentage = 18.0,
            tags = "headphones, audio, bluetooth, wireless, anc"
        ),
        ProductEntity(
            id = "p102",
            name = "UltraSmart Watch X Series 8 - Amoled Display",
            category = "Electronics",
            brand = "UltraSmart",
            price = 1999.0,
            originalPrice = 3999.0,
            discountPercent = 50,
            rating = 4.6f,
            reviewCount = 980,
            stock = 14,
            description = "1.96-inch AMOLED Always-On display, BT Calling, 100+ Sports Modes, SpO2 & Heart Rate monitor, IP68 water resistance, 7-day battery life.",
            imageUrl = "https://images.unsplash.com/photo-1523275335684-37898b6baf30?w=500&auto=format&fit=crop&q=60",
            isFeatured = true,
            isFlashSale = true,
            gstPercentage = 18.0,
            tags = "smartwatch, fitness, wearable, amoled"
        ),
        ProductEntity(
            id = "p103",
            name = "AeroGlide Air Cushion Running Shoes",
            category = "Fashion",
            brand = "AeroGlide",
            price = 1499.0,
            originalPrice = 2999.0,
            discountPercent = 50,
            rating = 4.5f,
            reviewCount = 650,
            stock = 18,
            description = "Lightweight breathable mesh upper, anti-skid rubber sole, high-rebound cushioning foam for jogging, gym workouts and daily street style wear.",
            imageUrl = "https://images.unsplash.com/photo-1542291026-7eec264c27ff?w=500&auto=format&fit=crop&q=60",
            isFeatured = true,
            isFlashSale = false,
            gstPercentage = 12.0,
            tags = "shoes, sneakers, running, sports, fashion"
        ),
        ProductEntity(
            id = "p104",
            name = "Flagship 5G Smartphone Ultra (12GB RAM, 256GB)",
            category = "Mobiles",
            brand = "NovaTech",
            price = 29999.0,
            originalPrice = 34999.0,
            discountPercent = 14,
            rating = 4.8f,
            reviewCount = 3120,
            stock = 8,
            description = "Snapdragon 8 Gen 2 flagship processor, 108MP OIS triple camera system, 120Hz curved AMOLED display, 67W Turbo Charger included in box.",
            imageUrl = "https://images.unsplash.com/photo-1511707171634-5f897ff02aa9?w=500&auto=format&fit=crop&q=60",
            isFeatured = true,
            isFlashSale = true,
            gstPercentage = 18.0,
            tags = "mobile, phone, 5g, smartphone, android"
        ),
        ProductEntity(
            id = "p105",
            name = "Ergonomic Mesh Office Chair with Lumbar Support",
            category = "Home & Kitchen",
            brand = "FlexiSpace",
            price = 4499.0,
            originalPrice = 7999.0,
            discountPercent = 43,
            rating = 4.4f,
            reviewCount = 410,
            stock = 3, // Low stock demo!
            description = "Adjustable headrest and lumbar support, breathable high-density mesh backrest, heavy-duty metal chrome base with smooth rolling caster wheels.",
            imageUrl = "https://images.unsplash.com/photo-1580481072645-022f9a6d1282?w=500&auto=format&fit=crop&q=60",
            isFeatured = false,
            isFlashSale = false,
            gstPercentage = 18.0,
            tags = "furniture, chair, office, home, ergonomic"
        ),
        ProductEntity(
            id = "p106",
            name = "Digital Air Fryer 4.5L with 8 Presets",
            category = "Appliances",
            brand = "ChefMaster",
            price = 3299.0,
            originalPrice = 5999.0,
            discountPercent = 45,
            rating = 4.7f,
            reviewCount = 890,
            stock = 12,
            description = "360-degree rapid heat air circulation, 90% less oil cooking, touch panel display with auto shutdown, non-stick dishwasher safe basket.",
            imageUrl = "https://images.unsplash.com/photo-1585515320310-259814833e62?w=500&auto=format&fit=crop&q=60",
            isFeatured = true,
            isFlashSale = true,
            gstPercentage = 18.0,
            tags = "airfryer, kitchen, appliances, cooking, healthy"
        ),
        ProductEntity(
            id = "p107",
            name = "Pure Herbal Vitamin C Facial Serum (30ml)",
            category = "Beauty & Care",
            brand = "GlowBotanica",
            price = 499.0,
            originalPrice = 999.0,
            discountPercent = 50,
            rating = 4.6f,
            reviewCount = 520,
            stock = 45,
            description = "10% Pure Vitamin C + Hyaluronic Acid + Ferulic Acid. Brightens skin tone, reduces dark spots and fine lines. Paraben and sulphate free.",
            imageUrl = "https://images.unsplash.com/photo-1620916566398-39f1143ab7be?w=500&auto=format&fit=crop&q=60",
            isFeatured = false,
            isFlashSale = false,
            gstPercentage = 18.0,
            tags = "skincare, serum, beauty, vitamin c, face"
        ),
        ProductEntity(
            id = "p108",
            name = "Leather Slim Minimalist RFID Blocking Wallet",
            category = "Fashion",
            brand = "UrbanCraft",
            price = 699.0,
            originalPrice = 1499.0,
            discountPercent = 53,
            rating = 4.3f,
            reviewCount = 310,
            stock = 2, // Low stock demo!
            description = "Genuine full-grain leather, holds up to 8 cards + cash slot, built-in RFID shielding protection against digital theft.",
            imageUrl = "https://images.unsplash.com/photo-1627123424574-724758594e93?w=500&auto=format&fit=crop&q=60",
            isFeatured = false,
            isFlashSale = false,
            gstPercentage = 12.0,
            tags = "wallet, leather, fashion, accessories"
        )
    )

    val sampleReviews = listOf(
        ReviewEntity(
            productId = "p101",
            userName = "Ankit Verma",
            rating = 5.0f,
            comment = "Superb sound quality and deep bass! Active Noise Cancellation works surprisingly well for this price point.",
            date = "2 days ago"
        ),
        ReviewEntity(
            productId = "p101",
            userName = "Priya Sundaram",
            rating = 4.5f,
            comment = "Battery backup easily lasts 35+ hours. Very comfortable cushioning for long work meetings.",
            date = "1 week ago"
        ),
        ReviewEntity(
            productId = "p102",
            userName = "Vikram Patel",
            rating = 5.0f,
            comment = "The AMOLED display is super bright outdoors. Bluetooth calling is crystal clear!",
            date = "3 days ago"
        )
    )
}
