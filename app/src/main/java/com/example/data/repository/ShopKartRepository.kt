package com.example.data.repository

import com.example.data.database.ShopKartDatabase
import com.example.data.entity.BannerEntity
import com.example.data.entity.CartItemEntity
import com.example.data.entity.CategoryEntity
import com.example.data.entity.CouponEntity
import com.example.data.entity.NotificationEntity
import com.example.data.entity.OrderEntity
import com.example.data.entity.OrderItemEntity
import com.example.data.entity.ProductEntity
import com.example.data.entity.ReviewEntity
import com.example.data.entity.UserEntity
import com.example.data.entity.WishlistItemEntity
import kotlinx.coroutines.flow.Flow
import java.util.UUID

data class CartWithProduct(
    val cartItem: CartItemEntity,
    val product: ProductEntity
)

class ShopKartRepository(private val db: ShopKartDatabase) {

    // User
    val user: Flow<UserEntity?> = db.userDao().getUser()
    suspend fun updateUser(user: UserEntity) = db.userDao().updateUser(user)
    suspend fun addRewardPoints(userId: String, points: Int) = db.userDao().addRewardPoints(userId, points)
    suspend fun addWalletBalance(userId: String, amount: Double) = db.userDao().addWalletBalance(userId, amount)

    // Products
    val allProducts: Flow<List<ProductEntity>> = db.productDao().getAllProducts()
    val featuredProducts: Flow<List<ProductEntity>> = db.productDao().getFeaturedProducts()
    val flashSaleProducts: Flow<List<ProductEntity>> = db.productDao().getFlashSaleProducts()
    val lowStockProducts: Flow<List<ProductEntity>> = db.productDao().getLowStockProducts(5)

    fun getProductById(id: String): Flow<ProductEntity?> = db.productDao().getProductById(id)
    fun getProductsByCategory(category: String): Flow<List<ProductEntity>> = db.productDao().getProductsByCategory(category)
    fun searchProducts(query: String): Flow<List<ProductEntity>> = db.productDao().searchProducts(query)

    suspend fun insertProduct(product: ProductEntity) = db.productDao().insertProduct(product)
    suspend fun updateProduct(product: ProductEntity) = db.productDao().updateProduct(product)
    suspend fun deleteProduct(product: ProductEntity) = db.productDao().deleteProduct(product)

    // Categories & Banners
    val allCategories: Flow<List<CategoryEntity>> = db.categoryDao().getAllCategories()
    val allBanners: Flow<List<BannerEntity>> = db.bannerDao().getAllBanners()

    // Wishlist
    val wishlistItems: Flow<List<WishlistItemEntity>> = db.wishlistDao().getWishlistItems()
    fun isWishlisted(productId: String): Flow<Boolean> = db.wishlistDao().isWishlisted(productId)
    suspend fun toggleWishlist(productId: String, currentlyWishlisted: Boolean) {
        if (currentlyWishlisted) {
            db.wishlistDao().removeFromWishlist(productId)
        } else {
            db.wishlistDao().addToWishlist(WishlistItemEntity(productId))
        }
    }

    // Cart
    val cartItems: Flow<List<CartItemEntity>> = db.cartDao().getCartItems()

    suspend fun addToCart(productId: String, quantity: Int = 1) {
        val existing = db.cartDao().getCartItemByProductId(productId)
        if (existing != null) {
            db.cartDao().updateQuantity(existing.id, existing.quantity + quantity)
        } else {
            db.cartDao().insertOrUpdateCartItem(CartItemEntity(productId = productId, quantity = quantity))
        }
    }

    suspend fun updateCartQuantity(cartItemId: Long, quantity: Int) {
        if (quantity <= 0) {
            db.cartDao().deleteCartItem(cartItemId)
        } else {
            db.cartDao().updateQuantity(cartItemId, quantity)
        }
    }

    suspend fun removeCartItem(cartItemId: Long) = db.cartDao().deleteCartItem(cartItemId)
    suspend fun clearCart() = db.cartDao().clearCart()

    // Coupons
    val activeCoupons: Flow<List<CouponEntity>> = db.couponDao().getActiveCoupons()
    suspend fun validateCoupon(code: String): CouponEntity? = db.couponDao().getCouponByCode(code.uppercase().trim())
    suspend fun insertCoupon(coupon: CouponEntity) = db.couponDao().insertCoupon(coupon)

    // Reviews
    fun getReviewsForProduct(productId: String): Flow<List<ReviewEntity>> = db.reviewDao().getReviewsForProduct(productId)
    suspend fun addReview(review: ReviewEntity) = db.reviewDao().insertReview(review)

    // Orders
    val allOrders: Flow<List<OrderEntity>> = db.orderDao().getAllOrders()
    fun getOrderById(orderId: String): Flow<OrderEntity?> = db.orderDao().getOrderById(orderId)
    fun getOrderItems(orderId: String): Flow<List<OrderItemEntity>> = db.orderDao().getOrderItems(orderId)

    suspend fun placeOrder(
        items: List<CartWithProduct>,
        subtotal: Double,
        gst: Double,
        discount: Double,
        couponCode: String,
        totalPayable: Double,
        paymentMethod: String,
        customerName: String,
        customerPhone: String,
        deliveryAddress: String
    ): String {
        val orderId = "ORD-" + System.currentTimeMillis().toString().takeLast(8)
        val orderNum = "#SK-" + (10000..99999).random()

        val newOrder = OrderEntity(
            orderId = orderId,
            orderNumber = orderNum,
            totalAmount = totalPayable,
            subtotalAmount = subtotal,
            gstAmount = gst,
            discountAmount = discount,
            couponCode = couponCode,
            paymentMethod = paymentMethod,
            paymentStatus = "SUCCESS",
            orderStatus = "Placed",
            trackingStep = 1,
            deliveryAddress = deliveryAddress,
            customerName = customerName,
            customerPhone = customerPhone
        )

        db.orderDao().insertOrder(newOrder)

        val orderItems = items.map {
            OrderItemEntity(
                orderId = orderId,
                productId = it.product.id,
                productName = it.product.name,
                productPrice = it.product.price,
                quantity = it.cartItem.quantity,
                imageUrl = it.product.imageUrl
            )
        }
        db.orderDao().insertOrderItems(orderItems)

        // Decrease stock for each ordered item
        items.forEach {
            db.productDao().decreaseStock(it.product.id, it.cartItem.quantity)
        }

        // Add reward points (1 point per 10 rupees)
        val earnedPoints = (totalPayable / 10).toInt()
        val userSync = db.userDao().getUserSync()
        if (userSync != null) {
            db.userDao().addRewardPoints(userSync.id, earnedPoints)
        }

        // Clear cart
        db.cartDao().clearCart()

        // Send Notification
        db.notificationDao().insertNotification(
            NotificationEntity(
                title = "Order Confirmed $orderNum",
                message = "Your order worth ₹${String.format("%.2f", totalPayable)} has been placed successfully via $paymentMethod!"
            )
        )

        return orderId
    }

    suspend fun updateOrderStatus(orderId: String, status: String, step: Int) {
        db.orderDao().updateOrderStatus(orderId, status, step)
        db.notificationDao().insertNotification(
            NotificationEntity(
                title = "Order Status Updated",
                message = "Order $orderId status is now: $status"
            )
        )
    }

    // Notifications
    val notifications: Flow<List<NotificationEntity>> = db.notificationDao().getAllNotifications()
    suspend fun markNotificationsRead() = db.notificationDao().markAllAsRead()

    // Export SQL script generator for InfinityFree deployment
    fun generateDatabaseSqlDump(): String {
        return """
        -- =========================================================
        -- ShopKart E-Commerce Database Dump (MySQL / InfinityFree)
        -- Created for PHP 8+ PDO Hosting
        -- =========================================================

        CREATE DATABASE IF NOT EXISTS `shopkart_db`;
        USE `shopkart_db`;

        -- 1. Users Table
        CREATE TABLE IF NOT EXISTS `users` (
            `id` VARCHAR(50) PRIMARY KEY,
            `name` VARCHAR(100) NOT NULL,
            `email` VARCHAR(150) UNIQUE NOT NULL,
            `phone` VARCHAR(20),
            `referralCode` VARCHAR(50),
            `rewardPoints` INT DEFAULT 0,
            `walletBalance` DECIMAL(10,2) DEFAULT 0.00,
            `role` ENUM('customer', 'admin') DEFAULT 'customer',
            `addressStreet` TEXT,
            `addressCity` VARCHAR(50),
            `addressState` VARCHAR(50),
            `addressPincode` VARCHAR(10)
        ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

        -- 2. Products Table
        CREATE TABLE IF NOT EXISTS `products` (
            `id` VARCHAR(50) PRIMARY KEY,
            `name` VARCHAR(255) NOT NULL,
            `category` VARCHAR(100) NOT NULL,
            `brand` VARCHAR(100) NOT NULL,
            `price` DECIMAL(10,2) NOT NULL,
            `originalPrice` DECIMAL(10,2) NOT NULL,
            `discountPercent` INT DEFAULT 0,
            `rating` FLOAT DEFAULT 0,
            `reviewCount` INT DEFAULT 0,
            `stock` INT DEFAULT 0,
            `description` TEXT,
            `imageUrl` TEXT,
            `isFeatured` TINYINT(1) DEFAULT 0,
            `isFlashSale` TINYINT(1) DEFAULT 0,
            `gstPercentage` DECIMAL(5,2) DEFAULT 18.00
        ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

        -- 3. Categories Table
        CREATE TABLE IF NOT EXISTS `categories` (
            `id` VARCHAR(50) PRIMARY KEY,
            `name` VARCHAR(100) NOT NULL,
            `iconName` VARCHAR(100),
            `bannerText` VARCHAR(255)
        ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

        -- 4. Cart Table
        CREATE TABLE IF NOT EXISTS `cart` (
            `id` BIGINT AUTO_INCREMENT PRIMARY KEY,
            `productId` VARCHAR(50) NOT NULL,
            `quantity` INT DEFAULT 1,
            `selectedVariant` VARCHAR(50) DEFAULT 'Standard',
            FOREIGN KEY (`productId`) REFERENCES `products`(`id`) ON DELETE CASCADE
        ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

        -- 5. Orders Table
        CREATE TABLE IF NOT EXISTS `orders` (
            `orderId` VARCHAR(50) PRIMARY KEY,
            `orderNumber` VARCHAR(50) NOT NULL,
            `date` BIGINT NOT NULL,
            `totalAmount` DECIMAL(10,2) NOT NULL,
            `subtotalAmount` DECIMAL(10,2) NOT NULL,
            `gstAmount` DECIMAL(10,2) NOT NULL,
            `discountAmount` DECIMAL(10,2) NOT NULL,
            `couponCode` VARCHAR(50),
            `paymentMethod` VARCHAR(50) NOT NULL,
            `paymentStatus` VARCHAR(50) DEFAULT 'SUCCESS',
            `orderStatus` VARCHAR(50) DEFAULT 'Placed',
            `trackingStep` INT DEFAULT 1,
            `deliveryAddress` TEXT,
            `customerName` VARCHAR(100),
            `customerPhone` VARCHAR(20)
        ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

        -- Sample Data Inserts Included
        INSERT INTO `users` (`id`, `name`, `email`, `phone`, `referralCode`, `rewardPoints`, `walletBalance`, `role`) 
        VALUES ('user_default_101', 'Rahul Sharma', 'rahul@example.com', '+91 9876543210', 'RAHUL500', 850, 250.00, 'customer')
        ON DUPLICATE KEY UPDATE `name`=`name`;
        """.trimIndent()
    }
}
