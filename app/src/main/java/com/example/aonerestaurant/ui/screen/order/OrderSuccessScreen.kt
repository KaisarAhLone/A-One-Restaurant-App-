package com.example.aonerestaurant.ui.screen.order

import android.content.Intent
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Map
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.net.toUri
import com.example.aonerestaurant.data.repository.OrderRepository
import com.example.aonerestaurant.data.repository.RestaurantRepository
import com.example.aonerestaurant.ui.components.CustomButton
import com.example.aonerestaurant.utils.ItemImageThumbnail

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OrderSuccessScreen(
    onBackToMenu: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val latestOrder = OrderRepository.latestOrder ?: OrderRepository.orders.firstOrNull()
    val ownerPhone = "+919871931413"

    val isDelivered = latestOrder?.status == "Delivered ✅"
    val isDark = isSystemInDarkTheme()

    val outerBgColor = if (isDark) Color(0xFF121218) else Color(0xFFF8F9FA)
    val cardBgColor = if (isDark) Color(0xFF1E1E2A) else Color.White
    val infoCardBgColor = if (isDark) Color(0xFF2A2238) else Color(0xFFF3E5F5)
    val headingColor = if (isDark) Color(0xFFFFB703) else Color(0xFF4A148C)
    val textColorPrimary = if (isDark) Color.White else Color(0xFF1D1E2C)

    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            TopAppBar(
                title = { Text(text = "Order Details 📄", fontWeight = FontWeight.Bold, color = Color.White) },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color(0xFF6A040F)),
                navigationIcon = {
                    IconButton(onClick = onBackToMenu) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = Color.White
                        )
                    }
                }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(outerBgColor)
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = cardBgColor),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Ordered Items:",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = headingColor
                        )

                        Box(
                            modifier = Modifier
                                .background(if (isDelivered) Color(0xFFE8F5E9) else Color(0xFFFFF3E0), RoundedCornerShape(20.dp))
                                .padding(horizontal = 10.dp, vertical = 4.dp)
                        ) {
                            Text(
                                text = latestOrder?.status ?: "Preparing 🍳",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (isDelivered) Color(0xFF2E7D32) else Color(0xFFE65100)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    val itemsText = latestOrder?.itemsSummary ?: "1x AOne Special Feast"
                    itemsText.split("\n").forEach { itemLine ->
                        if (itemLine.isNotBlank()) {
                            val cleanName = itemLine.substringAfter("x ").substringBefore(" (").trim()
                            val matchedFoodItem = RestaurantRepository.foodItems.find { it.name.contains(cleanName, ignoreCase = true) }

                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 6.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                ItemImageThumbnail(
                                    imageUri = matchedFoodItem?.imageUri,
                                    contentDescription = cleanName,
                                    modifier = Modifier
                                        .size(50.dp)
                                        .clip(RoundedCornerShape(10.dp))
                                )

                                Spacer(modifier = Modifier.width(12.dp))

                                Text(
                                    text = itemLine,
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = textColorPrimary
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))
                    HorizontalDivider()
                    Spacer(modifier = Modifier.height(12.dp))

                    // Grand Total
                    Text(
                        text = "💰 Grand Total: ₹${latestOrder?.totalAmount?.toInt() ?: 0}",
                        fontSize = 19.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = Color(0xFF4CAF50)
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    Text(
                        text = "Delivery Fee: ₹0 (FREE) • Platform Fee: ₹0 (FREE)",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFFFF9800)
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // Customer & Delivery Info Card
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = infoCardBgColor)
                    ) {
                        Column(
                            modifier = Modifier.padding(14.dp)
                        ) {
                            Text(
                                text = "Customer & Delivery Info:",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = headingColor
                            )

                            Spacer(modifier = Modifier.height(10.dp))

                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.Person,
                                    contentDescription = null,
                                    modifier = Modifier.size(18.dp),
                                    tint = if (isDark) Color(0xFFFFB703) else Color(0xFF6A040F)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "Name: ${latestOrder?.customerName?.ifBlank { "Customer" }}",
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = textColorPrimary
                                )
                            }

                            Spacer(modifier = Modifier.height(6.dp))

                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.Call,
                                    contentDescription = null,
                                    modifier = Modifier.size(18.dp),
                                    tint = if (isDark) Color(0xFFFFB703) else Color(0xFF6A040F)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "Phone: ${latestOrder?.customerPhone?.ifBlank { ownerPhone }}",
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = textColorPrimary
                                )
                            }

                            Spacer(modifier = Modifier.height(6.dp))

                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.Home,
                                    contentDescription = null,
                                    modifier = Modifier.size(18.dp),
                                    tint = if (isDark) Color(0xFFFF9800) else Color(0xFFE65100)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "PG/Hostel: ${latestOrder?.pgHostel?.ifBlank { "N/A" }}",
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = if (isDark) Color(0xFFFF9800) else Color(0xFFE65100)
                                )
                            }

                            Spacer(modifier = Modifier.height(6.dp))

                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.LocationOn,
                                    contentDescription = null,
                                    modifier = Modifier.size(18.dp),
                                    tint = Color(0xFF4CAF50)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "Address: ${latestOrder?.deliveryAddress?.ifBlank { "Law Gate, LPU Campus" }}",
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = Color(0xFF4CAF50)
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Mark as Delivered Button
                    if (latestOrder != null && !isDelivered) {
                        Button(
                            onClick = {
                                OrderRepository.updateOrderStatus(latestOrder.id, "Delivered ✅")
                                Toast.makeText(context, "Order marked as Delivered!", Toast.LENGTH_SHORT).show()
                            },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2E7D32))
                        ) {
                            Icon(imageVector = Icons.Default.CheckCircle, contentDescription = null, tint = Color.White)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(text = "Mark as Delivered ✅", fontWeight = FontWeight.Bold, color = Color.White)
                        }

                        Spacer(modifier = Modifier.height(16.dp))
                    }

                    // Action Buttons (Call & Get Location)
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Button(
                            onClick = {
                                val callPhone = latestOrder?.customerPhone?.ifBlank { ownerPhone } ?: ownerPhone
                                val intent = Intent(Intent.ACTION_DIAL, "tel:$callPhone".toUri())
                                context.startActivity(intent)
                            },
                            modifier = Modifier
                                .weight(1f)
                                .height(48.dp),
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF673AB7))
                        ) {
                            Icon(imageVector = Icons.Default.Call, contentDescription = null, modifier = Modifier.size(18.dp), tint = Color.White)
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(text = "Call", fontWeight = FontWeight.Bold, color = Color.White)
                        }

                        Button(
                            onClick = {
                                val address = latestOrder?.deliveryAddress ?: "Law Gate, LPU Campus"
                                val mapsUrl = "https://www.google.com/maps/search/?api=1&query=${address.replace(" ", "+")}"
                                val intent = Intent(Intent.ACTION_VIEW, mapsUrl.toUri())
                                context.startActivity(intent)
                            },
                            modifier = Modifier
                                .weight(1f)
                                .height(48.dp),
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2196F3))
                        ) {
                            Icon(imageVector = Icons.Default.Map, contentDescription = null, modifier = Modifier.size(18.dp), tint = Color.White)
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(text = "Get Location", fontWeight = FontWeight.Bold, color = Color.White)
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            CustomButton(
                text = "Back to Menu 🍽️",
                onClick = onBackToMenu
            )
        }
    }
}
