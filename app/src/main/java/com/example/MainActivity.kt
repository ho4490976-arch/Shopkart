package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.animation.Crossfade
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import com.example.ui.components.ShopKartBottomBar
import com.example.ui.components.ShopKartTopBar
import com.example.ui.components.SpinWheelCanvas
import com.example.ui.screens.AdminDashboardScreen
import com.example.ui.screens.CartScreen
import com.example.ui.screens.CategoriesScreen
import com.example.ui.screens.CheckoutScreen
import com.example.ui.screens.HomeScreen
import com.example.ui.screens.OrderConfirmedScreen
import com.example.ui.screens.OrderDetailScreen
import com.example.ui.screens.OrderHistoryScreen
import com.example.ui.screens.ProductDetailScreen
import com.example.ui.screens.ProfileScreen
import com.example.ui.screens.SqlExportScreen
import com.example.ui.screens.WishlistScreen
import com.example.ui.theme.ShopKartTheme
import com.example.ui.viewmodel.MainViewModel
import com.example.ui.viewmodel.Screen

class MainActivity : ComponentActivity() {

    private val viewModel: MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            val isDarkTheme by viewModel.isDarkTheme.collectAsState()

            ShopKartTheme(darkTheme = isDarkTheme) {
                ShopKartApp(viewModel = viewModel)
            }
        }
    }
}

@Composable
fun ShopKartApp(viewModel: MainViewModel) {
    val currentScreen by viewModel.currentScreen.collectAsState()
    val searchQuery by viewModel.searchQuery.collectAsState()
    val cartWithProducts by viewModel.cartWithProducts.collectAsState()
    val wishlistProducts by viewModel.wishlist.collectAsState()
    val isAdminMode by viewModel.isAdminMode.collectAsState()
    val isDarkTheme by viewModel.isDarkTheme.collectAsState()

    val wishlistProductIds = wishlistProducts.map { it.id }.toSet()
    val cartCount = cartWithProducts.sumOf { it.cartItem.quantity }

    // Screens where top & bottom bars should be displayed
    val showTopBar = currentScreen is Screen.Home || currentScreen is Screen.Categories || currentScreen is Screen.Wishlist || currentScreen is Screen.Cart || currentScreen is Screen.Profile
    val showBottomBar = currentScreen is Screen.Home || currentScreen is Screen.Categories || currentScreen is Screen.Wishlist || currentScreen is Screen.Cart || currentScreen is Screen.Profile || currentScreen is Screen.AdminDashboard

    Scaffold(
        topBar = {
            if (showTopBar) {
                ShopKartTopBar(
                    searchQuery = searchQuery,
                    onSearchQueryChange = { query ->
                        viewModel.setSearchQuery(query)
                        if (currentScreen !is Screen.Categories) {
                            viewModel.navigateTo(Screen.Categories)
                        }
                    },
                    cartCount = cartCount,
                    wishlistCount = wishlistProducts.size,
                    isAdminMode = isAdminMode,
                    onAdminToggle = { viewModel.toggleAdminMode() },
                    onWishlistClick = { viewModel.navigateTo(Screen.Wishlist) },
                    onCartClick = { viewModel.navigateTo(Screen.Cart) },
                    onNotificationsClick = { }
                )
            }
        },
        bottomBar = {
            if (showBottomBar) {
                ShopKartBottomBar(
                    currentScreen = currentScreen,
                    cartCount = cartCount,
                    wishlistCount = wishlistProducts.size,
                    isAdminMode = isAdminMode,
                    onNavigate = { screen -> viewModel.navigateTo(screen) }
                )
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            Crossfade(targetState = currentScreen, label = "ScreenTransition") { screen ->
                when (screen) {
                    is Screen.Home -> {
                        val banners by viewModel.banners.collectAsState()
                        val categories by viewModel.categories.collectAsState()
                        val flashSaleProducts by viewModel.flashSaleProducts.collectAsState()
                        val featuredProducts by viewModel.featuredProducts.collectAsState()

                        HomeScreen(
                            banners = banners,
                            categories = categories,
                            flashSaleProducts = flashSaleProducts,
                            featuredProducts = featuredProducts,
                            wishlistProductIds = wishlistProductIds,
                            onCategoryClick = { catName ->
                                viewModel.setSelectedCategory(catName)
                                viewModel.navigateTo(Screen.Categories)
                            },
                            onProductClick = { prodId -> viewModel.navigateTo(Screen.ProductDetail(prodId)) },
                            onWishlistToggle = { prodId, isWishlisted -> viewModel.toggleWishlist(prodId, isWishlisted) },
                            onAddToCart = { prodId -> viewModel.addToCart(prodId) },
                            onSpinWheelClick = { viewModel.navigateTo(Screen.SpinWheel) },
                            onSeeAllClick = { viewModel.navigateTo(Screen.Categories) }
                        )
                    }

                    is Screen.Categories -> {
                        val categories by viewModel.categories.collectAsState()
                        val filteredProducts by viewModel.filteredProducts.collectAsState()
                        val selectedCategory by viewModel.selectedCategory.collectAsState()
                        val selectedSortOption by viewModel.selectedSortOption.collectAsState()

                        CategoriesScreen(
                            categories = categories,
                            products = filteredProducts,
                            selectedCategory = selectedCategory,
                            selectedSortOption = selectedSortOption,
                            wishlistProductIds = wishlistProductIds,
                            onCategorySelect = { cat -> viewModel.setSelectedCategory(cat) },
                            onSortOptionSelect = { sort -> viewModel.setSortOption(sort) },
                            onProductClick = { prodId -> viewModel.navigateTo(Screen.ProductDetail(prodId)) },
                            onWishlistToggle = { prodId, isWishlisted -> viewModel.toggleWishlist(prodId, isWishlisted) },
                            onAddToCart = { prodId -> viewModel.addToCart(prodId) }
                        )
                    }

                    is Screen.ProductDetail -> {
                        val products by viewModel.products.collectAsState()
                        val product = products.find { it.id == screen.productId }
                        val isWishlisted = wishlistProductIds.contains(screen.productId)
                        val reviewsFlow = viewModel.getReviewsForProduct(screen.productId)

                        ProductDetailScreen(
                            product = product,
                            isWishlisted = isWishlisted,
                            reviewsFlow = reviewsFlow,
                            relatedProducts = products,
                            wishlistProductIds = wishlistProductIds,
                            onBackClick = { viewModel.navigateTo(Screen.Home) },
                            onWishlistToggle = { viewModel.toggleWishlist(screen.productId, isWishlisted) },
                            onAddToCart = { prodId -> viewModel.addToCart(prodId) },
                            onBuyNow = { prodId ->
                                viewModel.addToCart(prodId)
                                viewModel.navigateTo(Screen.Cart)
                            },
                            onAddReview = { prodId, rating, comment -> viewModel.addProductReview(prodId, rating, comment) },
                            onProductClick = { prodId -> viewModel.navigateTo(Screen.ProductDetail(prodId)) }
                        )
                    }

                    is Screen.Wishlist -> {
                        WishlistScreen(
                            wishlistProducts = wishlistProducts,
                            onProductClick = { prodId -> viewModel.navigateTo(Screen.ProductDetail(prodId)) },
                            onWishlistRemove = { prodId -> viewModel.toggleWishlist(prodId, true) },
                            onAddToCart = { prodId -> viewModel.addToCart(prodId) },
                            onExploreClick = { viewModel.navigateTo(Screen.Categories) }
                        )
                    }

                    is Screen.Cart -> {
                        val appliedCoupon by viewModel.appliedCoupon.collectAsState()
                        val couponError by viewModel.couponError.collectAsState()
                        val availableCoupons by viewModel.availableCoupons.collectAsState()

                        CartScreen(
                            cartItems = cartWithProducts,
                            appliedCoupon = appliedCoupon,
                            couponError = couponError,
                            availableCoupons = availableCoupons,
                            onUpdateQuantity = { id, qty -> viewModel.updateCartQuantity(id, qty) },
                            onRemoveItem = { id -> viewModel.removeCartItem(id) },
                            onApplyCoupon = { code -> viewModel.applyCoupon(code) },
                            onRemoveCoupon = { viewModel.removeCoupon() },
                            onCheckoutClick = { viewModel.navigateTo(Screen.Checkout) },
                            onContinueShoppingClick = { viewModel.navigateTo(Screen.Home) }
                        )
                    }

                    is Screen.Checkout -> {
                        val user by viewModel.user.collectAsState()
                        val appliedCoupon by viewModel.appliedCoupon.collectAsState()

                        CheckoutScreen(
                            user = user,
                            cartItems = cartWithProducts,
                            appliedCoupon = appliedCoupon,
                            onUpdateAddress = { name, street, city, state, pin ->
                                viewModel.updateUserAddress(name, street, city, state, pin)
                            },
                            onPlaceOrder = { paymentMethod ->
                                viewModel.placeOrder(paymentMethod) { orderId ->
                                    viewModel.navigateTo(Screen.OrderConfirmed(orderId))
                                }
                            }
                        )
                    }

                    is Screen.OrderConfirmed -> {
                        OrderConfirmedScreen(
                            orderId = screen.orderId,
                            onTrackOrderClick = { id -> viewModel.navigateTo(Screen.OrderDetail(id)) },
                            onContinueShoppingClick = { viewModel.navigateTo(Screen.Home) }
                        )
                    }

                    is Screen.OrderHistory -> {
                        val orders by viewModel.orders.collectAsState()

                        OrderHistoryScreen(
                            orders = orders,
                            onOrderClick = { id -> viewModel.navigateTo(Screen.OrderDetail(id)) },
                            onShopNowClick = { viewModel.navigateTo(Screen.Home) }
                        )
                    }

                    is Screen.OrderDetail -> {
                        OrderDetailScreen(
                            orderFlow = viewModel.getOrderById(screen.orderId),
                            orderItemsFlow = viewModel.getOrderItems(screen.orderId),
                            onBackClick = { viewModel.navigateTo(Screen.OrderHistory) },
                            onExportPdfClick = { }
                        )
                    }

                    is Screen.Profile -> {
                        val user by viewModel.user.collectAsState()

                        ProfileScreen(
                            user = user,
                            isDarkTheme = isDarkTheme,
                            isAdminMode = isAdminMode,
                            onDarkThemeToggle = { viewModel.toggleDarkTheme() },
                            onAdminToggle = { viewModel.toggleAdminMode() },
                            onOrderHistoryClick = { viewModel.navigateTo(Screen.OrderHistory) },
                            onSpinWheelClick = { viewModel.navigateTo(Screen.SpinWheel) },
                            onSqlExportClick = { viewModel.navigateTo(Screen.SqlExport) }
                        )
                    }

                    is Screen.SpinWheel -> {
                        SpinWheelCanvas(
                            onRewardClaimed = { points, title ->
                                viewModel.addSpinWheelReward(points, title)
                            }
                        )
                    }

                    is Screen.AdminDashboard -> {
                        val products by viewModel.products.collectAsState()
                        val orders by viewModel.orders.collectAsState()
                        val lowStockProducts by viewModel.lowStockProducts.collectAsState()

                        AdminDashboardScreen(
                            products = products,
                            orders = orders,
                            lowStockProducts = lowStockProducts,
                            onAddProduct = { prod -> viewModel.addNewProduct(prod) },
                            onUpdateProduct = { prod -> viewModel.updateProduct(prod) },
                            onDeleteProduct = { prod -> viewModel.deleteProduct(prod) },
                            onUpdateOrderStatus = { id, status, step -> viewModel.updateOrderStatus(id, status, step) }
                        )
                    }

                    is Screen.SqlExport -> {
                        SqlExportScreen(
                            sqlContent = viewModel.getSqlDump(),
                            onBackClick = { viewModel.navigateTo(Screen.Profile) }
                        )
                    }
                }
            }
        }
    }
}
