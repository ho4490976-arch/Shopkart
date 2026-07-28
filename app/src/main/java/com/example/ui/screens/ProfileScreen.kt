package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AdminPanelSettings
import androidx.compose.material.icons.filled.Casino
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Code
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.ReceiptLong
import androidx.compose.material.icons.filled.Redeem
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.entity.UserEntity
import com.example.ui.theme.AmberOrangeAccent
import com.example.ui.theme.IndigoNavyPrimary
import com.example.ui.theme.SuccessGreen

@Composable
fun ProfileScreen(
    user: UserEntity?,
    isDarkTheme: Boolean,
    isAdminMode: Boolean,
    onDarkThemeToggle: () -> Unit,
    onAdminToggle: () -> Unit,
    onOrderHistoryClick: () -> Unit,
    onSpinWheelClick: () -> Unit,
    onSqlExportClick: () -> Unit
) {
    if (user == null) return

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // User Avatar & Name Card
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = IndigoNavyPrimary)
            ) {
                Row(
                    modifier = Modifier.padding(20.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(56.dp)
                            .clip(CircleShape)
                            .background(AmberOrangeAccent),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = user.name.take(1),
                            fontWeight = FontWeight.ExtraBold,
                            color = Color.White,
                            fontSize = 24.sp
                        )
                    }

                    Spacer(modifier = Modifier.width(16.dp))

                    Column {
                        Text(
                            text = user.name,
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                        )
                        Text(text = user.email, fontSize = 12.sp, color = Color.White.copy(alpha = 0.8f))
                        Text(text = user.phone, fontSize = 12.sp, color = Color.White.copy(alpha = 0.8f))
                    }
                }
            }
        }

        // Reward Points & Referral Stats Row
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                // Points Card
                Card(
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White)
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Redeem, contentDescription = null, tint = AmberOrangeAccent, modifier = Modifier.size(20.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Reward Points", fontSize = 11.sp, color = Color.Gray)
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Text("${user.rewardPoints} pts", fontWeight = FontWeight.Bold, fontSize = 18.sp, color = IndigoNavyPrimary)
                    }
                }

                // Referral Code Card
                Card(
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White)
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Share, contentDescription = null, tint = SuccessGreen, modifier = Modifier.size(20.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Referral Code", fontSize = 11.sp, color = Color.Gray)
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(user.referralCode, fontWeight = FontWeight.Bold, fontSize = 18.sp, color = SuccessGreen)
                    }
                }
            }
        }

        // Quick Actions Group
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White)
            ) {
                Column(modifier = Modifier.padding(8.dp)) {
                    ProfileOptionRow(
                        icon = Icons.Default.ReceiptLong,
                        title = "Order History & Invoices",
                        subtitle = "Track orders, view GST breakdown & download PDFs",
                        onClick = onOrderHistoryClick
                    )
                    HorizontalDivider(color = Color(0xFFEEEEEE))

                    ProfileOptionRow(
                        icon = Icons.Default.Casino,
                        title = "Daily Spin & Win Wheel",
                        subtitle = "Spin daily for instant discount points",
                        onClick = onSpinWheelClick
                    )
                    HorizontalDivider(color = Color(0xFFEEEEEE))

                    ProfileOptionRow(
                        icon = Icons.Default.Home,
                        title = "Saved Address Book",
                        subtitle = "${user.addressStreet}, ${user.addressCity}",
                        onClick = { }
                    )
                }
            }
        }

        // Developer / InfinityFree Hosting Export
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable(onClick = onSqlExportClick),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = AmberOrangeAccent.copy(alpha = 0.1f)),
                border = androidx.compose.foundation.BorderStroke(1.dp, AmberOrangeAccent)
            ) {
                Row(
                    modifier = Modifier.padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Code, contentDescription = null, tint = IndigoNavyPrimary)
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text("InfinityFree / PHP Database Dump", fontWeight = FontWeight.Bold, fontSize = 13.sp, color = IndigoNavyPrimary)
                            Text("View & export full SQL database schema + PHP code", fontSize = 11.sp, color = Color.DarkGray)
                        }
                    }
                    Icon(Icons.Default.ChevronRight, contentDescription = null, tint = IndigoNavyPrimary)
                }
            }
        }

        // Settings (Dark Theme & Admin Mode)
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White)
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Text("App Settings & Permissions", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                    Spacer(modifier = Modifier.height(10.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.DarkMode, contentDescription = null, tint = IndigoNavyPrimary)
                            Spacer(modifier = Modifier.width(10.dp))
                            Text("Dark Theme", fontWeight = FontWeight.SemiBold, fontSize = 13.sp)
                        }
                        Switch(
                            checked = isDarkTheme,
                            onCheckedChange = { onDarkThemeToggle() },
                            colors = SwitchDefaults.colors(checkedThumbColor = AmberOrangeAccent)
                        )
                    }

                    HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.AdminPanelSettings, contentDescription = null, tint = AmberOrangeAccent)
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Text("Store Manager / Admin Mode", fontWeight = FontWeight.SemiBold, fontSize = 13.sp)
                                Text("Manage products, inventory & orders", fontSize = 10.sp, color = Color.Gray)
                            }
                        }
                        Switch(
                            checked = isAdminMode,
                            onCheckedChange = { onAdminToggle() },
                            colors = SwitchDefaults.colors(checkedThumbColor = AmberOrangeAccent)
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun ProfileOptionRow(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    title: String,
    subtitle: String,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
            Icon(imageVector = icon, contentDescription = null, tint = IndigoNavyPrimary, modifier = Modifier.size(22.dp))
            Spacer(modifier = Modifier.width(12.dp))
            Column {
                Text(text = title, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                Text(text = subtitle, fontSize = 11.sp, color = Color.Gray, maxLines = 1)
            }
        }
        Icon(Icons.Default.ChevronRight, contentDescription = null, tint = Color.Gray)
    }
}
