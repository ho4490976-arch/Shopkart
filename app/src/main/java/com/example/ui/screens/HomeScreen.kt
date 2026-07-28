package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.Casino
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.data.entity.BannerEntity
import com.example.data.entity.CategoryEntity
import com.example.data.entity.ProductEntity
import com.example.ui.components.ProductCard
import com.example.ui.theme.AmberOrangeAccent
import com.example.ui.theme.DiscountRed
import com.example.ui.theme.IndigoNavyPrimary
import com.example.ui.theme.SuccessGreen
import com.example.ui.viewmodel.Screen
import kotlinx.coroutines.delay

@Composable
fun HomeScreen(
    banners: List<BannerEntity>,
    categories: List<CategoryEntity>,
    flashSaleProducts: List<ProductEntity>,
    featuredProducts: List<ProductEntity>,
    wishlistProductIds: Set<String>,
    onCategoryClick: (String) -> Unit,
    onProductClick: (String) -> Unit,
    onWishlistToggle: (String, Boolean) -> Unit,
    onAddToCart: (String) -> Unit,
    onSpinWheelClick: () -> Unit,
    onSeeAllClick: () -> Unit
) {
    // Live countdown timer state for Flash Sale (04h : 12m : 45s)
    var secondsLeft by remember { mutableStateOf(15165) }
    LaunchedEffect(Unit) {
        while (secondsLeft > 0) {
            delay(1000)
            secondsLeft--
        }
    }

    val hours = secondsLeft / 3600
    val minutes = (secondsLeft % 3600) / 60
    val seconds = secondsLeft % 60

    LazyVerticalGrid(
        columns = GridCells.Fixed(2),
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // Banner Slider Header Item
        item(span = { GridItemSpan(2) }) {
            if (banners.isNotEmpty()) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(150.dp),
                    shape = RoundedCornerShape(12.dp),
                    elevation = CardDefaults.cardElevation(defaultElevation = 3.dp)
                ) {
                    Box(modifier = Modifier.fillMaxSize()) {
                        AsyncImage(
                            model = ImageRequest.Builder(LocalContext.current)
                                .data(banners[0].imageUrl)
                                .crossfade(true)
                                .build(),
                            contentDescription = "Promo Banner",
                            contentScale = ContentScale.Crop,
                            modifier = Modifier.fillMaxSize()
                        )
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(Color.Black.copy(alpha = 0.35f))
                                .padding(16.dp),
                            contentAlignment = Alignment.BottomStart
                        ) {
                            Column {
                                Surface(
                                    color = AmberOrangeAccent,
                                    shape = RoundedCornerShape(4.dp)
                                ) {
                                    Text(
                                        text = banners[0].title,
                                        color = Color.White,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 11.sp,
                                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                    )
                                }
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = banners[0].subtitle,
                                    color = Color.White,
                                    fontWeight = FontWeight.ExtraBold,
                                    fontSize = 15.sp,
                                    maxLines = 2
                                )
                            }
                        }
                    }
                }
            }
        }

        // Categories Scroll Bar Item
        item(span = { GridItemSpan(2) }) {
            Column {
                Text(
                    text = "Shop by Category",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                    modifier = Modifier.padding(vertical = 4.dp)
                )

                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    contentPadding = PaddingValues(vertical = 6.dp)
                ) {
                    items(categories) { cat ->
                        CategoryChip(category = cat, onClick = { onCategoryClick(cat.name) })
                    }
                }
            }
        }

        // Daily Spin Wheel Banner Trigger
        item(span = { GridItemSpan(2) }) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable(onClick = onSpinWheelClick),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = IndigoNavyPrimary)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .clip(CircleShape)
                                .background(AmberOrangeAccent),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.Default.Casino, contentDescription = null, tint = Color.White)
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(
                                text = "Daily Spin & Win Rewards!",
                                fontWeight = FontWeight.Bold,
                                color = Color.White,
                                fontSize = 14.sp
                            )
                            Text(
                                text = "Win up to 1000 reward points instantly",
                                color = Color.White.copy(alpha = 0.8f),
                                fontSize = 11.sp
                            )
                        }
                    }

                    Icon(Icons.Default.ChevronRight, contentDescription = null, tint = Color.White)
                }
            }
        }

        // Flash Sale Countdown Section Header
        item(span = { GridItemSpan(2) }) {
            Column(modifier = Modifier.padding(top = 8.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Bolt, contentDescription = null, tint = DiscountRed)
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "FLASH SALE",
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.ExtraBold,
                                color = DiscountRed
                            )
                        )
                        Spacer(modifier = Modifier.width(10.dp))

                        // Countdown Timer Chips
                        Surface(
                            color = DiscountRed.copy(alpha = 0.1f),
                            shape = RoundedCornerShape(6.dp)
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(Icons.Default.Timer, contentDescription = null, tint = DiscountRed, modifier = Modifier.size(12.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = String.format("%02dh : %02dm : %02ds", hours, minutes, seconds),
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 11.sp,
                                    color = DiscountRed
                                )
                            }
                        }
                    }

                    Text(
                        text = "See All",
                        color = AmberOrangeAccent,
                        fontWeight = FontWeight.Bold,
                        fontSize = 12.sp,
                        modifier = Modifier.clickable(onClick = onSeeAllClick)
                    )
                }

                // Flash Sale Horizontal List
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    contentPadding = PaddingValues(vertical = 10.dp)
                ) {
                    items(flashSaleProducts) { product ->
                        ProductCard(
                            product = product,
                            isWishlisted = wishlistProductIds.contains(product.id),
                            onProductClick = { onProductClick(product.id) },
                            onWishlistToggle = { onWishlistToggle(product.id, wishlistProductIds.contains(product.id)) },
                            onAddToCart = { onAddToCart(product.id) },
                            modifier = Modifier.width(160.dp)
                        )
                    }
                }
            }
        }

        // Featured Products Title
        item(span = { GridItemSpan(2) }) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Trending Products for You",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                )
                Text(
                    text = "View All",
                    color = AmberOrangeAccent,
                    fontWeight = FontWeight.Bold,
                    fontSize = 12.sp,
                    modifier = Modifier.clickable(onClick = onSeeAllClick)
                )
            }
        }

        // Featured Products Grid Items
        items(featuredProducts) { product ->
            ProductCard(
                product = product,
                isWishlisted = wishlistProductIds.contains(product.id),
                onProductClick = { onProductClick(product.id) },
                onWishlistToggle = { onWishlistToggle(product.id, wishlistProductIds.contains(product.id)) },
                onAddToCart = { onAddToCart(product.id) }
            )
        }
    }
}

@Composable
fun CategoryChip(
    category: CategoryEntity,
    onClick: () -> Unit
) {
    Surface(
        modifier = Modifier
            .clip(RoundedCornerShape(20.dp))
            .clickable(onClick = onClick),
        color = Color.White,
        shadowElevation = 2.dp
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(28.dp)
                    .clip(CircleShape)
                    .background(IndigoNavyPrimary.copy(alpha = 0.1f)),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = category.name.take(1),
                    fontWeight = FontWeight.Bold,
                    color = IndigoNavyPrimary,
                    fontSize = 13.sp
                )
            }
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = category.name,
                fontWeight = FontWeight.SemiBold,
                fontSize = 12.sp
            )
        }
    }
}
