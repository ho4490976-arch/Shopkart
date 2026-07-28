package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.entity.OrderEntity
import com.example.data.entity.OrderItemEntity
import com.example.ui.theme.IndigoNavyPrimary
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun InvoicePdfView(
    order: OrderEntity,
    orderItems: List<OrderItemEntity>,
    onExportPdfClick: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 3.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            // Invoice Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "TAX INVOICE",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.ExtraBold,
                            color = IndigoNavyPrimary
                        )
                    )
                    Text(
                        text = "GSTIN: 29AAAAA0000A1Z5",
                        fontSize = 11.sp,
                        color = Color.Gray
                    )
                }

                Column(horizontalAlignment = Alignment.End) {
                    Text(
                        text = order.orderNumber,
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp
                    )
                    val formattedDate = SimpleDateFormat("dd MMM yyyy, hh:mm a", Locale.getDefault())
                        .format(Date(order.date))
                    Text(
                        text = formattedDate,
                        fontSize = 10.sp,
                        color = Color.Gray
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))
            HorizontalDivider(color = Color.LightGray)
            Spacer(modifier = Modifier.height(12.dp))

            // Seller & Buyer Info Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text("Sold By:", fontWeight = FontWeight.Bold, fontSize = 11.sp, color = IndigoNavyPrimary)
                    Text("ShopKart India Pvt Ltd", fontWeight = FontWeight.SemiBold, fontSize = 12.sp)
                    Text("Outer Ring Road, Koramangala", fontSize = 10.sp, color = Color.Gray)
                    Text("Bengaluru, KA - 560034", fontSize = 10.sp, color = Color.Gray)
                }

                Column(modifier = Modifier.weight(1f), horizontalAlignment = Alignment.End) {
                    Text("Billed To:", fontWeight = FontWeight.Bold, fontSize = 11.sp, color = IndigoNavyPrimary)
                    Text(order.customerName, fontWeight = FontWeight.SemiBold, fontSize = 12.sp)
                    Text(order.deliveryAddress, fontSize = 10.sp, color = Color.Gray, maxLines = 2)
                    Text("Ph: ${order.customerPhone}", fontSize = 10.sp, color = Color.Gray)
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Itemized Table Header
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(IndigoNavyPrimary.copy(alpha = 0.1f))
                    .padding(8.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text("Item Description", fontWeight = FontWeight.Bold, fontSize = 11.sp, modifier = Modifier.weight(2f))
                Text("Qty", fontWeight = FontWeight.Bold, fontSize = 11.sp, modifier = Modifier.weight(0.5f))
                Text("Rate", fontWeight = FontWeight.Bold, fontSize = 11.sp, modifier = Modifier.weight(1f))
                Text("Total", fontWeight = FontWeight.Bold, fontSize = 11.sp, modifier = Modifier.weight(1f))
            }

            // Items List
            orderItems.forEach { item ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 8.dp, vertical = 6.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(item.productName, fontSize = 11.sp, modifier = Modifier.weight(2f), maxLines = 1)
                    Text(item.quantity.toString(), fontSize = 11.sp, modifier = Modifier.weight(0.5f))
                    Text("₹${item.productPrice.toInt()}", fontSize = 11.sp, modifier = Modifier.weight(1f))
                    Text("₹${(item.productPrice * item.quantity).toInt()}", fontSize = 11.sp, fontWeight = FontWeight.SemiBold, modifier = Modifier.weight(1f))
                }
                Divider(color = Color(0xFFEEEEEE))
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Summary Breakdown
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color(0xFFFAFAFA))
                    .padding(10.dp)
            ) {
                InvoiceRow("Subtotal:", "₹${String.format("%.2f", order.subtotalAmount)}")
                InvoiceRow("CGST + SGST (18%):", "₹${String.format("%.2f", order.gstAmount)}")
                if (order.discountAmount > 0) {
                    InvoiceRow("Coupon Discount (${order.couponCode}):", "-₹${String.format("%.2f", order.discountAmount)}", isDiscount = true)
                }
                InvoiceRow("Payment Mode:", order.paymentMethod)
                HorizontalDivider(modifier = Modifier.padding(vertical = 4.dp))
                InvoiceRow("Grand Total Payable:", "₹${String.format("%.2f", order.totalAmount)}", isBold = true)
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Action Buttons
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Button(
                    onClick = onExportPdfClick,
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(8.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = IndigoNavyPrimary)
                ) {
                    Icon(Icons.Default.Download, contentDescription = null, modifier = Modifier.padding(end = 4.dp))
                    Text("DOWNLOAD PDF", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                }

                OutlinedButton(
                    onClick = { /* Share action */ },
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Icon(Icons.Default.Share, contentDescription = null, modifier = Modifier.padding(end = 4.dp))
                    Text("SHARE INVOICE", fontSize = 12.sp)
                }
            }
        }
    }
}

@Composable
private fun InvoiceRow(label: String, value: String, isDiscount: Boolean = false, isBold: Boolean = false) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 2.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = label,
            fontSize = 11.sp,
            fontWeight = if (isBold) FontWeight.Bold else FontWeight.Normal,
            color = if (isDiscount) Color(0xFFD32F2F) else Color.Black
        )
        Text(
            text = value,
            fontSize = 11.sp,
            fontWeight = if (isBold) FontWeight.Bold else FontWeight.SemiBold,
            color = if (isDiscount) Color(0xFFD32F2F) else Color.Black
        )
    }
}
