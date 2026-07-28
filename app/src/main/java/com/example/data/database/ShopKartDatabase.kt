package com.example.data.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.data.dao.BannerDao
import com.example.data.dao.CartDao
import com.example.data.dao.CategoryDao
import com.example.data.dao.CouponDao
import com.example.data.dao.NotificationDao
import com.example.data.dao.OrderDao
import com.example.data.dao.ProductDao
import com.example.data.dao.ReviewDao
import com.example.data.dao.UserDao
import com.example.data.dao.WishlistDao
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
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Database(
    entities = [
        UserEntity::class,
        ProductEntity::class,
        CategoryEntity::class,
        CartItemEntity::class,
        WishlistItemEntity::class,
        OrderEntity::class,
        OrderItemEntity::class,
        CouponEntity::class,
        ReviewEntity::class,
        BannerEntity::class,
        NotificationEntity::class
    ],
    version = 1,
    exportSchema = false
)
abstract class ShopKartDatabase : RoomDatabase() {
    abstract fun userDao(): UserDao
    abstract fun productDao(): ProductDao
    abstract fun categoryDao(): CategoryDao
    abstract fun cartDao(): CartDao
    abstract fun wishlistDao(): WishlistDao
    abstract fun orderDao(): OrderDao
    abstract fun couponDao(): CouponDao
    abstract fun reviewDao(): ReviewDao
    abstract fun bannerDao(): BannerDao
    abstract fun notificationDao(): NotificationDao

    companion object {
        @Volatile
        private var INSTANCE: ShopKartDatabase? = null

        fun getDatabase(context: Context, scope: CoroutineScope): ShopKartDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    ShopKartDatabase::class.java,
                    "shopkart_database.db"
                )
                .addCallback(DatabaseCallback(scope))
                .fallbackToDestructiveMigration()
                .build()
                INSTANCE = instance
                instance
            }
        }

        private class DatabaseCallback(
            private val scope: CoroutineScope
        ) : RoomDatabase.Callback() {
            override fun onCreate(db: SupportSQLiteDatabase) {
                super.onCreate(db)
                INSTANCE?.let { database ->
                    scope.launch(Dispatchers.IO) {
                        populateDatabase(database)
                    }
                }
            }

            suspend fun populateDatabase(database: ShopKartDatabase) {
                database.userDao().insertUser(PrepopulatedData.initialUser)
                database.categoryDao().insertCategories(PrepopulatedData.categories)
                database.bannerDao().insertBanners(PrepopulatedData.banners)
                database.couponDao().insertCoupons(PrepopulatedData.coupons)
                database.productDao().insertProducts(PrepopulatedData.products)
                database.reviewDao().insertReviews(PrepopulatedData.sampleReviews)
                
                // Insert initial welcome notification
                database.notificationDao().insertNotification(
                    NotificationEntity(
                        title = "Welcome to ShopKart! 🎉",
                        message = "Enjoy up to 50% discount on top electronic products. Use coupon WELCOME100 for extra savings!"
                    )
                )
            }
        }
    }
}
