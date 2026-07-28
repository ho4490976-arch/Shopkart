package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AdminPanelSettings
import androidx.compose.material.icons.filled.AttachMoney
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Inventory
import androidx.compose.material.icons.filled.LocalMall
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.data.entity.OrderEntity
import com.example.data.entity.ProductEntity
import com.example.ui.theme.AmberOrangeAccent
import com.example.ui.theme.DiscountRed
import com.example.ui.theme.IndigoNavyPrimary
import com.example.ui.theme.SuccessGreen
import java.util.UUID

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminDashboardScreen(
    products: List<ProductEntity>,
    orders: List<OrderEntity>,
    lowStockProducts: List<ProductEntity>,
    onAddProduct: (ProductEntity) -> Unit,
    onUpdateProduct: (ProductEntity) -> Unit,
    onDeleteProduct: (ProductEntity) -> Unit,
    onUpdateOrderStatus: (String, String, Int) -> Unit
) {
    var showAddProductSheet by remember { mutableStateOf(false) }
    var editingProduct by remember { mutableStateOf<ProductEntity?>(null) }

    // Form inputs
    var nameInput by remember { mutableStateOf("") }
    var brandInput by remember { mutableStateOf("") }
    var categoryInput by remember { mutableStateOf("Electronics") }
    var priceInput by remember { mutableStateOf("") }
    var origPriceInput by remember { mutableStateOf("") }
    var stockInput by remember { mutableStateOf("") }
    var imageInput by remember { mutableStateOf("") }
    var descInput by remember { mutableStateOf("") }

    val totalRevenue = orders.sumOf { it.totalAmount }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // Admin Header Title
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.AdminPanelSettings, contentDescription = null, tint = AmberOrangeAccent, modifier = Modifier.size(28.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Admin Store Panel", style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold))
                }

                Button(
                    onClick = {
                        editingProduct = null
                        nameInput = ""
                        brandInput = ""
                        categoryInput = "Electronics"
                        priceInput = ""
                        origPriceInput = ""
                        stockInput = "10"
                        imageInput = "https://picsum.photos/400/400"
                        descInput = ""
                        showAddProductSheet = true
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = AmberOrangeAccent),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Add Product", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                }
            }
        }

        // Metrics Grid (4 Stat Cards)
        item {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    MetricCard(
                        title = "Total Revenue",
                        value = "₹${String.format("%.2f", totalRevenue)}",
                        icon = Icons.Default.AttachMoney,
                        color = SuccessGreen,
                        modifier = Modifier.weight(1f)
                    )
                    MetricCard(
                        title = "Total Orders",
                        value = orders.size.toString(),
                        icon = Icons.Default.LocalMall,
                        color = IndigoNavyPrimary,
                        modifier = Modifier.weight(1f)
                    )
                }
                Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    MetricCard(
                        title = "Active Products",
                        value = products.size.toString(),
                        icon = Icons.Default.Inventory,
                        color = AmberOrangeAccent,
                        modifier = Modifier.weight(1f)
                    )
                    MetricCard(
                        title = "Low Stock Alert",
                        value = "${lowStockProducts.size} Items",
                        icon = Icons.Default.Warning,
                        color = DiscountRed,
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }

        // Order Fulfillment Management Section
        item {
            Text("Order Fulfillment & Status Updates", fontWeight = FontWeight.Bold, fontSize = 15.sp)
        }

        items(orders) { order ->
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White)
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(order.orderNumber, fontWeight = FontWeight.Bold, color = IndigoNavyPrimary)
                        Text("₹${order.totalAmount.toInt()}", fontWeight = FontWeight.ExtraBold, color = SuccessGreen)
                    }
                    Text("Customer: ${order.customerName}", fontSize = 12.sp, color = Color.Gray)
                    Text("Current Status: ${order.orderStatus} (Step ${order.trackingStep}/4)", fontSize = 12.sp, fontWeight = FontWeight.Bold)

                    Spacer(modifier = Modifier.height(8.dp))

                    // Status Step Updater Buttons
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        StatusStepChip("1. Placed", order.trackingStep == 1) { onUpdateOrderStatus(order.orderId, "Placed", 1) }
                        StatusStepChip("2. Packed", order.trackingStep == 2) { onUpdateOrderStatus(order.orderId, "Packed", 2) }
                        StatusStepChip("3. Shipped", order.trackingStep == 3) { onUpdateOrderStatus(order.orderId, "Shipped", 3) }
                        StatusStepChip("4. Delivered", order.trackingStep == 4) { onUpdateOrderStatus(order.orderId, "Delivered", 4) }
                    }
                }
            }
        }

        // Product Catalog Management Section
        item {
            Spacer(modifier = Modifier.height(10.dp))
            Text("Product Inventory Management", fontWeight = FontWeight.Bold, fontSize = 15.sp)
        }

        items(products) { prod ->
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White)
            ) {
                Row(
                    modifier = Modifier.padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    AsyncImage(
                        model = ImageRequest.Builder(LocalContext.current)
                            .data(prod.imageUrl)
                            .crossfade(true)
                            .build(),
                        contentDescription = prod.name,
                        modifier = Modifier
                            .size(54.dp)
                            .clip(RoundedCornerShape(8.dp)),
                        contentScale = ContentScale.Crop
                    )

                    Spacer(modifier = Modifier.width(12.dp))

                    Column(modifier = Modifier.weight(1f)) {
                        Text(prod.name, fontWeight = FontWeight.SemiBold, fontSize = 13.sp, maxLines = 1)
                        Text("₹${prod.price.toInt()} • Stock: ${prod.stock}", fontSize = 12.sp, color = IndigoNavyPrimary)
                    }

                    IconButton(
                        onClick = {
                            editingProduct = prod
                            nameInput = prod.name
                            brandInput = prod.brand
                            categoryInput = prod.category
                            priceInput = prod.price.toString()
                            origPriceInput = prod.originalPrice.toString()
                            stockInput = prod.stock.toString()
                            imageInput = prod.imageUrl
                            descInput = prod.description
                            showAddProductSheet = true
                        }
                    ) {
                        Icon(Icons.Default.Edit, contentDescription = "Edit", tint = AmberOrangeAccent)
                    }

                    IconButton(onClick = { onDeleteProduct(prod) }) {
                        Icon(Icons.Default.Delete, contentDescription = "Delete", tint = DiscountRed)
                    }
                }
            }
        }
    }

    // Add / Edit Product Sheet
    if (showAddProductSheet) {
        ModalBottomSheet(
            onDismissRequest = { showAddProductSheet = false },
            sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Text(if (editingProduct != null) "Edit Product" else "Add New Product", fontWeight = FontWeight.Bold, fontSize = 16.sp)

                OutlinedTextField(value = nameInput, onValueChange = { nameInput = it }, label = { Text("Product Title") }, modifier = Modifier.fillMaxWidth(), singleLine = true)
                OutlinedTextField(value = brandInput, onValueChange = { brandInput = it }, label = { Text("Brand Name") }, modifier = Modifier.fillMaxWidth(), singleLine = true)
                OutlinedTextField(value = categoryInput, onValueChange = { categoryInput = it }, label = { Text("Category (Electronics, Fashion...)") }, modifier = Modifier.fillMaxWidth(), singleLine = true)

                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(value = priceInput, onValueChange = { priceInput = it }, label = { Text("Price (₹)") }, modifier = Modifier.weight(1f), singleLine = true)
                    OutlinedTextField(value = origPriceInput, onValueChange = { origPriceInput = it }, label = { Text("M.R.P Price") }, modifier = Modifier.weight(1f), singleLine = true)
                    OutlinedTextField(value = stockInput, onValueChange = { stockInput = it }, label = { Text("Stock Qty") }, modifier = Modifier.weight(1f), singleLine = true)
                }

                OutlinedTextField(value = imageInput, onValueChange = { imageInput = it }, label = { Text("Image URL") }, modifier = Modifier.fillMaxWidth(), singleLine = true)
                OutlinedTextField(value = descInput, onValueChange = { descInput = it }, label = { Text("Product Description") }, modifier = Modifier.fillMaxWidth(), minLines = 2)

                Spacer(modifier = Modifier.height(10.dp))

                Button(
                    onClick = {
                        val price = priceInput.toDoubleOrNull() ?: 999.0
                        val origPrice = origPriceInput.toDoubleOrNull() ?: 1499.0
                        val stock = stockInput.toIntOrNull() ?: 10
                        val discPercent = (((origPrice - price) / origPrice) * 100).toInt().coerceAtLeast(0)

                        val newProduct = ProductEntity(
                            id = editingProduct?.id ?: "prod_${UUID.randomUUID().toString().take(8)}",
                            name = nameInput.ifBlank { "New Product" },
                            brand = brandInput.ifBlank { "Generic" },
                            category = categoryInput.ifBlank { "Electronics" },
                            price = price,
                            originalPrice = origPrice,
                            discountPercent = discPercent,
                            stock = stock,
                            rating = editingProduct?.rating ?: 4.5f,
                            reviewCount = editingProduct?.reviewCount ?: 12,
                            imageUrl = imageInput.ifBlank { "https://picsum.photos/400/400" },
                            description = descInput.ifBlank { "High quality product from ShopKart." },
                            isFeatured = true,
                            isFlashSale = false
                        )

                        if (editingProduct != null) {
                            onUpdateProduct(newProduct)
                        } else {
                            onAddProduct(newProduct)
                        }
                        showAddProductSheet = false
                    },
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(containerColor = IndigoNavyPrimary)
                ) {
                    Text("SAVE PRODUCT")
                }
            }
        }
    }
}

@Composable
fun MetricCard(
    title: String,
    value: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    color: Color,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(imageVector = icon, contentDescription = null, tint = color, modifier = Modifier.size(18.dp))
                Spacer(modifier = Modifier.width(6.dp))
                Text(title, fontSize = 11.sp, color = Color.Gray)
            }
            Spacer(modifier = Modifier.height(4.dp))
            Text(value, fontWeight = FontWeight.Bold, fontSize = 16.sp, color = color)
        }
    }
}

@Composable
fun StatusStepChip(label: String, isSelected: Boolean, onClick: () -> Unit) {
    Surface(
        modifier = Modifier
            .clip(RoundedCornerShape(6.dp))
            .clickable(onClick = onClick),
        color = if (isSelected) AmberOrangeAccent else Color(0xFFEEEEEE)
    ) {
        Text(
            text = label,
            fontSize = 10.sp,
            fontWeight = FontWeight.Bold,
            color = if (isSelected) Color.White else Color.Black,
            modifier = Modifier.padding(horizontal = 6.dp, vertical = 4.dp)
        )
    }
}
