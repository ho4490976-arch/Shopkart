package com.example.ui.components

import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AdminPanelSettings
import androidx.compose.material.icons.filled.Category
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.ui.theme.AmberOrangeAccent
import com.example.ui.viewmodel.Screen

@Composable
fun ShopKartBottomBar(
    currentScreen: Screen,
    cartCount: Int,
    wishlistCount: Int,
    isAdminMode: Boolean,
    onNavigate: (Screen) -> Unit
) {
    NavigationBar(
        modifier = Modifier.windowInsetsPadding(WindowInsets.navigationBars),
        containerColor = Color.White,
        tonalElevation = 8.dp
    ) {
        NavigationBarItem(
            selected = currentScreen is Screen.Home,
            onClick = { onNavigate(Screen.Home) },
            icon = { Icon(Icons.Default.Home, contentDescription = "Home") },
            label = { Text("Home", fontWeight = FontWeight.SemiBold) },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = AmberOrangeAccent,
                selectedTextColor = AmberOrangeAccent,
                indicatorColor = AmberOrangeAccent.copy(alpha = 0.15f)
            )
        )

        NavigationBarItem(
            selected = currentScreen is Screen.Categories,
            onClick = { onNavigate(Screen.Categories) },
            icon = { Icon(Icons.Default.Category, contentDescription = "Categories") },
            label = { Text("Categories", fontWeight = FontWeight.SemiBold) },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = AmberOrangeAccent,
                selectedTextColor = AmberOrangeAccent,
                indicatorColor = AmberOrangeAccent.copy(alpha = 0.15f)
            )
        )

        NavigationBarItem(
            selected = currentScreen is Screen.Wishlist,
            onClick = { onNavigate(Screen.Wishlist) },
            icon = {
                BadgedBox(
                    badge = {
                        if (wishlistCount > 0) {
                            Badge(containerColor = AmberOrangeAccent) {
                                Text(wishlistCount.toString(), color = Color.White)
                            }
                        }
                    }
                ) {
                    Icon(Icons.Default.Favorite, contentDescription = "Wishlist")
                }
            },
            label = { Text("Wishlist", fontWeight = FontWeight.SemiBold) },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = AmberOrangeAccent,
                selectedTextColor = AmberOrangeAccent,
                indicatorColor = AmberOrangeAccent.copy(alpha = 0.15f)
            )
        )

        NavigationBarItem(
            selected = currentScreen is Screen.Cart,
            onClick = { onNavigate(Screen.Cart) },
            icon = {
                BadgedBox(
                    badge = {
                        if (cartCount > 0) {
                            Badge(containerColor = AmberOrangeAccent) {
                                Text(cartCount.toString(), color = Color.White)
                            }
                        }
                    }
                ) {
                    Icon(Icons.Default.ShoppingCart, contentDescription = "Cart")
                }
            },
            label = { Text("Cart", fontWeight = FontWeight.SemiBold) },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = AmberOrangeAccent,
                selectedTextColor = AmberOrangeAccent,
                indicatorColor = AmberOrangeAccent.copy(alpha = 0.15f)
            )
        )

        NavigationBarItem(
            selected = currentScreen is Screen.Profile,
            onClick = { onNavigate(Screen.Profile) },
            icon = { Icon(Icons.Default.Person, contentDescription = "Profile") },
            label = { Text("Account", fontWeight = FontWeight.SemiBold) },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = AmberOrangeAccent,
                selectedTextColor = AmberOrangeAccent,
                indicatorColor = AmberOrangeAccent.copy(alpha = 0.15f)
            )
        )

        if (isAdminMode) {
            NavigationBarItem(
                selected = currentScreen is Screen.AdminDashboard,
                onClick = { onNavigate(Screen.AdminDashboard) },
                icon = { Icon(Icons.Default.AdminPanelSettings, contentDescription = "Admin") },
                label = { Text("Admin", fontWeight = FontWeight.Bold) },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = AmberOrangeAccent,
                    selectedTextColor = AmberOrangeAccent,
                    indicatorColor = AmberOrangeAccent.copy(alpha = 0.15f)
                )
            )
        }
    }
}
