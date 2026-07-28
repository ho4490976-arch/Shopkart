package com.example.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.database.ShopKartDatabase
import com.example.data.entity.BannerEntity
import com.example.data.entity.CartItemEntity
import com.example.data.entity.CategoryEntity
import com.example.data.entity.CouponEntity
import com.example.data.entity.OrderEntity
import com.example.data.entity.OrderItemEntity
import com.example.data.entity.ProductEntity
import com.example.data.entity.ReviewEntity
import com.example.data.entity.UserEntity
import com.example.data.repository.CartWithProduct
import com.example.data.repository.ShopKartRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

sealed class Screen {
    object Home : Screen()
    object Categories : Screen()
    data class ProductDetail(val productId: String) : Screen()
    object Wishlist : Screen()
    object Cart : Screen()
    object Checkout : Screen()
    data class OrderConfirmed(val orderId: String) : Screen()
    object OrderHistory : Screen()
    data class OrderDetail(val orderId: String) : Screen()
    object Profile : Screen()
    object SpinWheel : Screen()
    object AdminDashboard : Screen()
    object SqlExport : Screen()
}

class MainViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: ShopKartRepository

    init {
        val database = ShopKartDatabase.getDatabase(application, viewModelScope)
        repository = ShopKartRepository(database)
    }

    // Navigation state
    private val _currentScreen = MutableStateFlow<Screen>(Screen.Home)
    val currentScreen: StateFlow<Screen> = _currentScreen.asStateFlow()

    fun navigateTo(screen: Screen) {
        _currentScreen.value = screen
    }

    // Dark theme state
    private val _isDarkTheme = MutableStateFlow(false)
    val isDarkTheme: StateFlow<Boolean> = _isDarkTheme.asStateFlow()

    fun toggleDarkTheme() {
        _isDarkTheme.value = !_isDarkTheme.value
    }

    // Admin mode state
    private val _isAdminMode = MutableStateFlow(false)
    val isAdminMode: StateFlow<Boolean> = _isAdminMode.asStateFlow()

    fun toggleAdminMode() {
        _isAdminMode.value = !_isAdminMode.value
    }

    // User Data
    val user: StateFlow<UserEntity?> = repository.user.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = null
    )

    fun updateUserAddress(name: String, street: String, city: String, state: String, pincode: String) {
        viewModelScope.launch {
            val currentUser = user.value ?: return@launch
            val updated = currentUser.copy(
                addressName = name,
                addressStreet = street,
                addressCity = city,
                addressState = state,
                addressPincode = pincode
            )
            repository.updateUser(updated)
        }
    }

    // Products Flow
    val products: StateFlow<List<ProductEntity>> = repository.allProducts.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    val featuredProducts: StateFlow<List<ProductEntity>> = repository.featuredProducts.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    val flashSaleProducts: StateFlow<List<ProductEntity>> = repository.flashSaleProducts.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    val lowStockProducts: StateFlow<List<ProductEntity>> = repository.lowStockProducts.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    // Search and Filter State
    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _selectedCategory = MutableStateFlow("All")
    val selectedCategory: StateFlow<String> = _selectedCategory.asStateFlow()

    private val _selectedSortOption = MutableStateFlow("Popularity") // "Popularity", "Price Low to High", "Price High to Low", "Rating"
    val selectedSortOption: StateFlow<String> = _selectedSortOption.asStateFlow()

    fun setSearchQuery(query: String) {
        _searchQuery.value = query
    }

    fun setSelectedCategory(category: String) {
        _selectedCategory.value = category
    }

    fun setSortOption(option: String) {
        _selectedSortOption.value = option
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    val filteredProducts: StateFlow<List<ProductEntity>> = combine(
        products,
        _searchQuery,
        _selectedCategory,
        _selectedSortOption
    ) { allProds, query, cat, sort ->
        var result = allProds

        if (cat != "All") {
            result = result.filter { it.category.equals(cat, ignoreCase = true) }
        }

        if (query.isNotBlank()) {
            result = result.filter {
                it.name.contains(query, ignoreCase = true) ||
                it.description.contains(query, ignoreCase = true) ||
                it.brand.contains(query, ignoreCase = true)
            }
        }

        when (sort) {
            "Price Low to High" -> result.sortedBy { it.price }
            "Price High to Low" -> result.sortedByDescending { it.price }
            "Rating" -> result.sortedByDescending { it.rating }
            else -> result.sortedByDescending { it.reviewCount }
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    // Categories & Banners
    val categories: StateFlow<List<CategoryEntity>> = repository.allCategories.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    val banners: StateFlow<List<BannerEntity>> = repository.allBanners.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    // Wishlist
    val wishlist: StateFlow<List<ProductEntity>> = combine(
        products,
        repository.wishlistItems
    ) { allProds, wishEntities ->
        val wishIds = wishEntities.map { it.productId }.toSet()
        allProds.filter { wishIds.contains(it.id) }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    fun toggleWishlist(productId: String, isCurrentlyWishlisted: Boolean) {
        viewModelScope.launch {
            repository.toggleWishlist(productId, isCurrentlyWishlisted)
        }
    }

    // Cart State
    val cartWithProducts: StateFlow<List<CartWithProduct>> = combine(
        repository.cartItems,
        products
    ) { cartEntities, allProds ->
        val prodMap = allProds.associateBy { it.id }
        cartEntities.mapNotNull { cartItem ->
            val product = prodMap[cartItem.productId]
            if (product != null) CartWithProduct(cartItem, product) else null
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    // Applied Coupon Code
    private val _appliedCoupon = MutableStateFlow<CouponEntity?>(null)
    val appliedCoupon: StateFlow<CouponEntity?> = _appliedCoupon.asStateFlow()

    private val _couponError = MutableStateFlow<String?>(null)
    val couponError: StateFlow<String?> = _couponError.asStateFlow()

    fun addToCart(productId: String, quantity: Int = 1) {
        viewModelScope.launch {
            repository.addToCart(productId, quantity)
        }
    }

    fun updateCartQuantity(cartItemId: Long, quantity: Int) {
        viewModelScope.launch {
            repository.updateCartQuantity(cartItemId, quantity)
        }
    }

    fun removeCartItem(cartItemId: Long) {
        viewModelScope.launch {
            repository.removeCartItem(cartItemId)
        }
    }

    fun applyCoupon(code: String) {
        viewModelScope.launch {
            _couponError.value = null
            val coupon = repository.validateCoupon(code)
            if (coupon != null && coupon.isActive) {
                val subtotal = cartWithProducts.value.sumOf { it.product.price * it.cartItem.quantity }
                if (subtotal >= coupon.minOrderAmount) {
                    _appliedCoupon.value = coupon
                } else {
                    _couponError.value = "Min order amount ₹${coupon.minOrderAmount.toInt()} required for code ${coupon.code}"
                }
            } else {
                _couponError.value = "Invalid or expired coupon code"
            }
        }
    }

    fun removeCoupon() {
        _appliedCoupon.value = null
        _couponError.value = null
    }

    // Available Coupons
    val availableCoupons: StateFlow<List<CouponEntity>> = repository.activeCoupons.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    // Orders
    val orders: StateFlow<List<OrderEntity>> = repository.allOrders.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    fun getOrderById(orderId: String): Flow<OrderEntity?> = repository.getOrderById(orderId)
    fun getOrderItems(orderId: String): Flow<List<OrderItemEntity>> = repository.getOrderItems(orderId)

    // Checkout & Payment Placement
    private val _isPlacingOrder = MutableStateFlow(false)
    val isPlacingOrder: StateFlow<Boolean> = _isPlacingOrder.asStateFlow()

    fun placeOrder(paymentMethod: String, onSuccess: (String) -> Unit) {
        val cartItemsList = cartWithProducts.value
        if (cartItemsList.isEmpty()) return

        val currentUser = user.value ?: return

        _isPlacingOrder.value = true
        viewModelScope.launch {
            val subtotal = cartItemsList.sumOf { it.product.price * it.cartItem.quantity }
            val gst = subtotal * 0.18 // 18% GST standard

            var discount = 0.0
            val coupon = _appliedCoupon.value
            if (coupon != null) {
                discount = (subtotal * coupon.discountPercent / 100.0).coerceAtMost(coupon.maxDiscountAmount)
            }

            val totalPayable = (subtotal + gst - discount).coerceAtLeast(0.0)

            val fullAddress = "${currentUser.addressStreet}, ${currentUser.addressCity}, ${currentUser.addressState} - ${currentUser.addressPincode}"

            val orderId = repository.placeOrder(
                items = cartItemsList,
                subtotal = subtotal,
                gst = gst,
                discount = discount,
                couponCode = coupon?.code ?: "",
                totalPayable = totalPayable,
                paymentMethod = paymentMethod,
                customerName = currentUser.name,
                customerPhone = currentUser.phone,
                deliveryAddress = fullAddress
            )

            _isPlacingOrder.value = false
            _appliedCoupon.value = null
            onSuccess(orderId)
        }
    }

    // Admin Functions
    fun addNewProduct(product: ProductEntity) {
        viewModelScope.launch {
            repository.insertProduct(product)
        }
    }

    fun updateProduct(product: ProductEntity) {
        viewModelScope.launch {
            repository.updateProduct(product)
        }
    }

    fun deleteProduct(product: ProductEntity) {
        viewModelScope.launch {
            repository.deleteProduct(product)
        }
    }

    fun updateOrderStatus(orderId: String, status: String, step: Int) {
        viewModelScope.launch {
            repository.updateOrderStatus(orderId, status, step)
        }
    }

    // Spin Wheel Rewards
    fun addSpinWheelReward(points: Int, rewardTitle: String) {
        viewModelScope.launch {
            val currentUser = user.value ?: return@launch
            repository.addRewardPoints(currentUser.id, points)
        }
    }

    // Add Product Review
    fun addProductReview(productId: String, rating: Float, comment: String) {
        viewModelScope.launch {
            val currentUser = user.value ?: return@launch
            val review = ReviewEntity(
                productId = productId,
                userName = currentUser.name,
                rating = rating,
                comment = comment,
                date = "Just now"
            )
            repository.addReview(review)
        }
    }

    fun getReviewsForProduct(productId: String): Flow<List<ReviewEntity>> {
        return repository.getReviewsForProduct(productId)
    }

    fun getSqlDump(): String {
        return repository.generateDatabaseSqlDump()
    }
}
