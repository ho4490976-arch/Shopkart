package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Payment
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.entity.CouponEntity
import com.example.data.entity.UserEntity
import com.example.data.repository.CartWithProduct
import com.example.ui.components.RazorpayPaymentModal
import com.example.ui.theme.AmberOrangeAccent
import com.example.ui.theme.IndigoNavyPrimary
import com.example.ui.theme.SuccessGreen

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CheckoutScreen(
    user: UserEntity?,
    cartItems: List<CartWithProduct>,
    appliedCoupon: CouponEntity?,
    onUpdateAddress: (String, String, String, String, String) -> Unit,
    onPlaceOrder: (String) -> Unit
) {
    if (user == null || cartItems.isEmpty()) return

    val subtotal = cartItems.sumOf { it.product.price * it.cartItem.quantity }
    val gst = subtotal * 0.18
    var discount = 0.0
    if (appliedCoupon != null) {
        discount = (subtotal * appliedCoupon.discountPercent / 100.0).coerceAtMost(appliedCoupon.maxDiscountAmount)
    }
    val deliveryFee = if (subtotal > 499) 0.0 else 49.0
    val totalPayable = (subtotal + gst + deliveryFee - discount).coerceAtLeast(0.0)

    var showAddressSheet by remember { mutableStateOf(false) }
    var showRazorpayModal by remember { mutableStateOf(false) }
    val sheetState = rememberModalBottomSheetState()

    var nameInput by remember { mutableStateOf(user.addressName) }
    var streetInput by remember { mutableStateOf(user.addressStreet) }
    var cityInput by remember { mutableStateOf(user.addressCity) }
    var stateInput by remember { mutableStateOf(user.addressState) }
    var pincodeInput by remember { mutableStateOf(user.addressPincode) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text("Checkout & Payment", style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold))
        Spacer(modifier = Modifier.height(12.dp))

        LazyColumn(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Delivery Address Card
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.Home, contentDescription = null, tint = IndigoNavyPrimary)
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("Delivery Address", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                            }
                            IconButton(onClick = { showAddressSheet = true }) {
                                Icon(Icons.Default.Edit, contentDescription = "Edit", tint = AmberOrangeAccent)
                            }
                        }

                        Text(user.addressName, fontWeight = FontWeight.SemiBold, fontSize = 13.sp)
                        Text("${user.addressStreet}, ${user.addressCity}", fontSize = 12.sp, color = Color.Gray)
                        Text("${user.addressState} - ${user.addressPincode}", fontSize = 12.sp, color = Color.Gray)
                        Text("Ph: ${user.phone}", fontSize = 12.sp, color = Color.Gray)
                    }
                }
            }

            // Order Summary Brief Card
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text("Order Summary (${cartItems.size} Items)", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                        Spacer(modifier = Modifier.height(8.dp))

                        cartItems.forEach { item ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 4.dp),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text("${item.product.name} x${item.cartItem.quantity}", fontSize = 12.sp, modifier = Modifier.weight(1f), maxLines = 1)
                                Text("₹${(item.product.price * item.cartItem.quantity).toInt()}", fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                            }
                        }
                    }
                }
            }

            // Payment Amount Card
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("Total Payable", fontWeight = FontWeight.Bold)
                            Text("₹${String.format("%.2f", totalPayable)}", fontWeight = FontWeight.ExtraBold, fontSize = 16.sp, color = IndigoNavyPrimary)
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Trigger Razorpay Modal Payment Button
        Button(
            onClick = { showRazorpayModal = true },
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp),
            shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.buttonColors(containerColor = AmberOrangeAccent)
        ) {
            Icon(Icons.Default.Lock, contentDescription = null, tint = Color.White)
            Spacer(modifier = Modifier.width(8.dp))
            Text("PROCEED TO PAY ₹${String.format("%.2f", totalPayable)}", fontWeight = FontWeight.Bold, fontSize = 15.sp)
        }
    }

    // Edit Address Sheet
    if (showAddressSheet) {
        ModalBottomSheet(
            onDismissRequest = { showAddressSheet = false },
            sheetState = sheetState
        ) {
            Column(modifier = Modifier.padding(20.dp)) {
                Text("Edit Delivery Address", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                Spacer(modifier = Modifier.height(12.dp))

                OutlinedTextField(value = nameInput, onValueChange = { nameInput = it }, label = { Text("Full Name") }, modifier = Modifier.fillMaxWidth())
                OutlinedTextField(value = streetInput, onValueChange = { streetInput = it }, label = { Text("Flat / House / Street") }, modifier = Modifier.fillMaxWidth())
                OutlinedTextField(value = cityInput, onValueChange = { cityInput = it }, label = { Text("City") }, modifier = Modifier.fillMaxWidth())
                OutlinedTextField(value = stateInput, onValueChange = { stateInput = it }, label = { Text("State") }, modifier = Modifier.fillMaxWidth())
                OutlinedTextField(value = pincodeInput, onValueChange = { pincodeInput = it }, label = { Text("Pincode") }, modifier = Modifier.fillMaxWidth())

                Spacer(modifier = Modifier.height(16.dp))
                Button(
                    onClick = {
                        onUpdateAddress(nameInput, streetInput, cityInput, stateInput, pincodeInput)
                        showAddressSheet = false
                    },
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(containerColor = IndigoNavyPrimary)
                ) {
                    Text("SAVE ADDRESS")
                }
            }
        }
    }

    // Razorpay Gateway Sheet
    if (showRazorpayModal) {
        RazorpayPaymentModal(
            sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true),
            totalPayable = totalPayable,
            onDismiss = { showRazorpayModal = false },
            onPaymentSuccess = { paymentMethodLabel ->
                showRazorpayModal = false
                onPlaceOrder(paymentMethodLabel)
            }
        )
    }
}
