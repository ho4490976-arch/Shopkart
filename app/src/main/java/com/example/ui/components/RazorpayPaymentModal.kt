package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBalance
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.CreditCard
import androidx.compose.material.icons.filled.LocalShipping
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.QrCode
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
import androidx.compose.material3.SheetState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.AmberOrangeAccent
import com.example.ui.theme.IndigoNavyPrimary
import com.example.ui.theme.SuccessGreen
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RazorpayPaymentModal(
    sheetState: SheetState,
    totalPayable: Double,
    onDismiss: () -> Unit,
    onPaymentSuccess: (String) -> Unit
) {
    var selectedMethod by remember { mutableStateOf("UPI") } // "UPI", "CARD", "NETBANKING", "COD"
    var upiId by remember { mutableStateOf("rahul@okaxis") }
    var isProcessing by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = Color.White
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp)
        ) {
            // Razorpay Header Branding
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(32.dp)
                            .clip(CircleShape)
                            .background(IndigoNavyPrimary),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("R", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Column {
                        Text(
                            text = "Razorpay Secure Gateway",
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                            color = IndigoNavyPrimary
                        )
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Lock,
                                contentDescription = null,
                                tint = SuccessGreen,
                                modifier = Modifier.size(12.dp)
                            )
                            Spacer(modifier = Modifier.width(2.dp))
                            Text("256-Bit Encrypted Payment", fontSize = 10.sp, color = Color.Gray)
                        }
                    }
                }

                Surface(
                    color = AmberOrangeAccent.copy(alpha = 0.15f),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text(
                        text = "₹${String.format("%.2f", totalPayable)}",
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                        fontWeight = FontWeight.Bold,
                        color = AmberOrangeAccent,
                        fontSize = 15.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text("Select Payment Option:", fontWeight = FontWeight.Bold, fontSize = 14.sp)
            Spacer(modifier = Modifier.height(10.dp))

            // Option 1: UPI GPay / PhonePe / Paytm
            PaymentOptionRow(
                title = "UPI (Google Pay / PhonePe / Paytm)",
                subtitle = "Instant Zero-Fee UPI Transfer",
                icon = Icons.Default.QrCode,
                selected = selectedMethod == "UPI",
                onClick = { selectedMethod = "UPI" }
            )

            if (selectedMethod == "UPI") {
                OutlinedTextField(
                    value = upiId,
                    onValueChange = { upiId = it },
                    label = { Text("Enter Virtual Payment Address (VPA)") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = 12.dp, top = 6.dp, bottom = 6.dp),
                    singleLine = true
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Option 2: Cards
            PaymentOptionRow(
                title = "Credit / Debit / ATM Card",
                subtitle = "Visa, Mastercard, RuPay, Maestro",
                icon = Icons.Default.CreditCard,
                selected = selectedMethod == "CARD",
                onClick = { selectedMethod = "CARD" }
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Option 3: NetBanking
            PaymentOptionRow(
                title = "NetBanking",
                subtitle = "HDFC, SBI, ICICI, Axis & 50+ Banks",
                icon = Icons.Default.AccountBalance,
                selected = selectedMethod == "NETBANKING",
                onClick = { selectedMethod = "NETBANKING" }
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Option 4: Cash on Delivery
            PaymentOptionRow(
                title = "Cash On Delivery (COD)",
                subtitle = "Pay cash at your doorstep upon delivery",
                icon = Icons.Default.LocalShipping,
                selected = selectedMethod == "COD",
                onClick = { selectedMethod = "COD" }
            )

            Spacer(modifier = Modifier.height(20.dp))

            // Pay Action Button
            Button(
                onClick = {
                    scope.launch {
                        isProcessing = true
                        delay(1500) // Simulate Razorpay Gateway Auth processing
                        isProcessing = false
                        val methodLabel = when (selectedMethod) {
                            "UPI" -> "Razorpay UPI ($upiId)"
                            "CARD" -> "Razorpay Card"
                            "NETBANKING" -> "Razorpay NetBanking"
                            else -> "Cash On Delivery"
                        }
                        onPaymentSuccess(methodLabel)
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = IndigoNavyPrimary),
                enabled = !isProcessing
            ) {
                if (isProcessing) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        CircularProgressIndicator(
                            color = Color.White,
                            modifier = Modifier.size(20.dp),
                            strokeWidth = 2.dp
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Authenticating with Razorpay...", color = Color.White)
                    }
                } else {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.CheckCircle, contentDescription = null, tint = AmberOrangeAccent)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = if (selectedMethod == "COD") "CONFIRM COD ORDER" else "PAY ₹${String.format("%.2f", totalPayable)} VIA RAZORPAY",
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

@Composable
fun PaymentOptionRow(
    title: String,
    subtitle: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    selected: Boolean,
    onClick: () -> Unit
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(10.dp))
            .border(
                width = if (selected) 2.dp else 1.dp,
                color = if (selected) AmberOrangeAccent else Color.LightGray,
                shape = RoundedCornerShape(10.dp)
            )
            .clickable(onClick = onClick),
        color = if (selected) AmberOrangeAccent.copy(alpha = 0.05f) else Color.White
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            RadioButton(
                selected = selected,
                onClick = onClick,
                colors = RadioButtonDefaults.colors(selectedColor = AmberOrangeAccent)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Icon(imageVector = icon, contentDescription = null, tint = IndigoNavyPrimary)
            Spacer(modifier = Modifier.width(10.dp))
            Column {
                Text(text = title, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                Text(text = subtitle, fontSize = 11.sp, color = Color.Gray)
            }
        }
    }
}
