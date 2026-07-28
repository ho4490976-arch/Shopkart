package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.ConfirmationNumber
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material.icons.filled.ShoppingCart
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
import androidx.compose.material3.TextButton
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
import com.example.data.entity.CouponEntity
import com.example.data.repository.CartWithProduct
import com.example.ui.theme.AmberOrangeAccent
import com.example.ui.theme.DiscountRed
import com.example.ui.theme.IndigoNavyPrimary
import com.example.ui.theme.SuccessGreen

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CartScreen(
    cartItems: List<CartWithProduct>,
    appliedCoupon: CouponEntity?,
    couponError: String?,
    availableCoupons: List<CouponEntity>,
    onUpdateQuantity: (Long, Int) -> Unit,
    onRemoveItem: (Long) -> Unit,
    onApplyCoupon: (String) -> Unit,
    onRemoveCoupon: () -> Unit,
    onCheckoutClick: () -> Unit,
    onContinueShoppingClick: () -> Unit
) {
    var showCouponSheet by remember { mutableStateOf(false) }
    var couponCodeInput by remember { mutableStateOf("") }

    if (cartItems.isEmpty()) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Icon(
                    imageVector = Icons.Default.ShoppingCart,
                    contentDescription = null,
                    tint = Color.LightGray,
                    modifier = Modifier.size(64.dp)
                )
                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    text = "Your Shopping Cart is Empty",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                )
                Text(
                    text = "Explore top items & add them to cart!",
                    fontSize = 12.sp,
                    color = Color.Gray
                )
                Spacer(modifier = Modifier.height(16.dp))
                Button(
                    onClick = onContinueShoppingClick,
                    colors = ButtonDefaults.buttonColors(containerColor = AmberOrangeAccent)
                ) {
                    Text("START SHOPPING")
                }
            }
        }
        return
    }

    val subtotal = cartItems.sumOf { it.product.price * it.cartItem.quantity }
    val gst = subtotal * 0.18

    var discountAmount = 0.0
    if (appliedCoupon != null) {
        discountAmount = (subtotal * appliedCoupon.discountPercent / 100.0).coerceAtMost(appliedCoupon.maxDiscountAmount)
    }

    val deliveryFee = if (subtotal > 499) 0.0 else 49.0
    val grandTotal = (subtotal + gst + deliveryFee - discountAmount).coerceAtLeast(0.0)

    Box(modifier = Modifier.fillMaxSize()) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(bottom = 80.dp),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            item {
                Text(
                    text = "Shopping Cart (${cartItems.size} Items)",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                )
            }

            // Cart Item Rows
            items(cartItems, key = { it.cartItem.id }) { item ->
                CartItemCard(
                    cartWithProduct = item,
                    onUpdateQuantity = { qty -> onUpdateQuantity(item.cartItem.id, qty) },
                    onRemove = { onRemoveItem(item.cartItem.id) }
                )
            }

            // Coupon Banner Row
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { showCouponSheet = true },
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    border = androidx.compose.foundation.BorderStroke(1.dp, AmberOrangeAccent)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(14.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.ConfirmationNumber, contentDescription = null, tint = AmberOrangeAccent)
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Text(
                                    text = if (appliedCoupon != null) "COUPON APPLIED: ${appliedCoupon.code}" else "Apply Coupon / Promo Code",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 13.sp,
                                    color = if (appliedCoupon != null) SuccessGreen else IndigoNavyPrimary
                                )
                                Text(
                                    text = if (appliedCoupon != null) "Saved ₹${discountAmount.toInt()} on this order!" else "Tap to see available offers",
                                    fontSize = 11.sp,
                                    color = Color.Gray
                                )
                            }
                        }

                        if (appliedCoupon != null) {
                            TextButton(onClick = onRemoveCoupon) {
                                Text("REMOVE", color = DiscountRed, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            }
                        } else {
                            Text("APPLY", color = AmberOrangeAccent, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                        }
                    }
                }
            }

            // Bill Breakdown Card
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text("Price Details & Taxes", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                        Spacer(modifier = Modifier.height(10.dp))

                        BillRow("Total Items Price (Excl. Tax)", "₹${subtotal.toInt()}")
                        BillRow("GST (18% Govt Tax)", "+₹${gst.toInt()}", isHighlight = true)
                        BillRow("Delivery Fee", if (deliveryFee == 0.0) "FREE" else "₹${deliveryFee.toInt()}")

                        if (appliedCoupon != null) {
                            BillRow("Coupon Discount", "-₹${discountAmount.toInt()}", isDiscount = true)
                        }

                        HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text("Total Payable Amount", fontWeight = FontWeight.Bold, fontSize = 15.sp)
                            Text(
                                text = "₹${grandTotal.toInt()}",
                                fontWeight = FontWeight.ExtraBold,
                                fontSize = 18.sp,
                                color = IndigoNavyPrimary
                            )
                        }
                    }
                }
            }
        }

        // Sticky Checkout Bottom Bar
        Surface(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth(),
            shadowElevation = 12.dp,
            color = Color.White
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text("Total Amount", fontSize = 11.sp, color = Color.Gray)
                    Text("₹${grandTotal.toInt()}", fontWeight = FontWeight.ExtraBold, fontSize = 18.sp, color = IndigoNavyPrimary)
                }

                Button(
                    onClick = onCheckoutClick,
                    modifier = Modifier.height(48.dp),
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = AmberOrangeAccent)
                ) {
                    Text("PROCEED TO CHECKOUT", fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.width(6.dp))
                    Icon(Icons.Default.ArrowForward, contentDescription = null, modifier = Modifier.size(16.dp))
                }
            }
        }
    }

    // Coupons Bottom Sheet Modal
    if (showCouponSheet) {
        ModalBottomSheet(onDismissRequest = { showCouponSheet = false }) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp)
            ) {
                Text("Apply Coupon Code", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                Spacer(modifier = Modifier.height(10.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    OutlinedTextField(
                        value = couponCodeInput,
                        onValueChange = { couponCodeInput = it },
                        placeholder = { Text("Enter Code (e.g. WELCOME100)") },
                        modifier = Modifier.weight(1f),
                        singleLine = true
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Button(
                        onClick = {
                            if (couponCodeInput.isNotBlank()) {
                                onApplyCoupon(couponCodeInput)
                                showCouponSheet = false
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = IndigoNavyPrimary)
                    ) {
                        Text("APPLY")
                    }
                }

                couponError?.let { err ->
                    Text(err, color = DiscountRed, fontSize = 11.sp, modifier = Modifier.padding(top = 4.dp))
                }

                Spacer(modifier = Modifier.height(16.dp))
                Text("Available Coupons for You:", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                Spacer(modifier = Modifier.height(8.dp))

                availableCoupons.forEach { coupon ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp)
                            .clickable {
                                onApplyCoupon(coupon.code)
                                showCouponSheet = false
                            },
                        colors = CardDefaults.cardColors(containerColor = Color(0xFFFAFAFA)),
                        border = androidx.compose.foundation.BorderStroke(1.dp, Color.LightGray)
                    ) {
                        Row(
                            modifier = Modifier.padding(12.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(coupon.code, fontWeight = FontWeight.Bold, color = AmberOrangeAccent)
                                Text(coupon.description, fontSize = 11.sp, color = Color.Gray)
                            }
                            Text("TAP TO APPLY", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = IndigoNavyPrimary)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun CartItemCard(
    cartWithProduct: CartWithProduct,
    onUpdateQuantity: (Int) -> Unit,
    onRemove: () -> Unit
) {
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
                    .data(cartWithProduct.product.imageUrl)
                    .crossfade(true)
                    .build(),
                contentDescription = cartWithProduct.product.name,
                modifier = Modifier
                    .size(72.dp)
                    .clip(RoundedCornerShape(8.dp)),
                contentScale = ContentScale.Crop
            )

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = cartWithProduct.product.name,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 13.sp,
                    maxLines = 1
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = "₹${cartWithProduct.product.price.toInt()} each",
                    fontWeight = FontWeight.Bold,
                    color = IndigoNavyPrimary,
                    fontSize = 14.sp
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Quantity Stepper Controls
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(28.dp)
                            .clip(CircleShape)
                            .background(Color(0xFFEEEEEE))
                            .clickable { onUpdateQuantity(cartWithProduct.cartItem.quantity - 1) },
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = if (cartWithProduct.cartItem.quantity == 1) Icons.Default.Delete else Icons.Default.Remove,
                            contentDescription = "Decrease",
                            modifier = Modifier.size(14.dp),
                            tint = if (cartWithProduct.cartItem.quantity == 1) DiscountRed else Color.Black
                        )
                    }

                    Text(
                        text = cartWithProduct.cartItem.quantity.toString(),
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 12.dp),
                        fontSize = 13.sp
                    )

                    Box(
                        modifier = Modifier
                            .size(28.dp)
                            .clip(CircleShape)
                            .background(Color(0xFFEEEEEE))
                            .clickable { onUpdateQuantity(cartWithProduct.cartItem.quantity + 1) },
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Icons.Default.Add, contentDescription = "Increase", modifier = Modifier.size(14.dp))
                    }
                }
            }

            IconButton(onClick = onRemove) {
                Icon(Icons.Default.Delete, contentDescription = "Remove", tint = DiscountRed)
            }
        }
    }
}

@Composable
fun BillRow(label: String, value: String, isHighlight: Boolean = false, isDiscount: Boolean = false) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 3.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(text = label, fontSize = 12.sp, color = Color.DarkGray)
        Text(
            text = value,
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold,
            color = when {
                isDiscount -> SuccessGreen
                isHighlight -> IndigoNavyPrimary
                else -> Color.Black
            }
        )
    }
}
