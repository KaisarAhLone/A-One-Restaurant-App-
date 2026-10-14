package com.example.aonerestaurant.ui.screen.cart

import android.Manifest
import android.content.Context
import android.content.Intent
import android.location.Geocoder
import android.location.LocationManager
import android.provider.Settings
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.MyLocation
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Save
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.net.toUri
import com.example.aonerestaurant.data.model.Address
import com.example.aonerestaurant.data.model.CartItem
import com.example.aonerestaurant.data.model.FoodItem
import com.example.aonerestaurant.data.model.Order
import com.example.aonerestaurant.data.repository.AddressRepository
import com.example.aonerestaurant.data.repository.OrderRepository
import com.example.aonerestaurant.data.repository.UserPreferences
import com.example.aonerestaurant.ui.components.CustomButton
import com.google.firebase.auth.FirebaseAuth
import java.net.URLEncoder
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CartScreen(
    cartItems: List<CartItem>,
    onAddItem: (FoodItem) -> Unit,
    onRemoveItem: (FoodItem) -> Unit,
    onPlaceOrder: () -> Unit,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val isDark = isSystemInDarkTheme()

    // Vibrant high-contrast theme colors for perfect visibility in Light & Dark mode
    val headingColor = if (isDark) Color(0xFFFFB703) else Color(0xFF6A040F)
    val cardBgColor = if (isDark) Color(0xFF222232) else Color.White
    val cardSelectedBgColor = if (isDark) Color(0xFF3B2D20) else Color(0xFFFFF3E0)
    val textColorPrimary = if (isDark) Color.White else Color(0xFF1D1E2C)
    val textColorSecondary = if (isDark) Color(0xFFCCCCCC) else Color(0xFF555555)

    var selectedAddressId by remember { mutableStateOf<String?>(AddressRepository.savedAddresses.firstOrNull()?.id) }

    var customerName by remember { mutableStateOf(AddressRepository.savedAddresses.firstOrNull()?.name ?: "") }
    var customerPhone by remember { mutableStateOf(AddressRepository.savedAddresses.firstOrNull()?.phone ?: "") }
    var pgHostel by remember { mutableStateOf(AddressRepository.savedAddresses.firstOrNull()?.pgHostel ?: "") }
    var deliveryAddress by remember { mutableStateOf(AddressRepository.savedAddresses.firstOrNull()?.fullAddress ?: "") }

    var showAddNewAddressForm by remember { mutableStateOf(AddressRepository.savedAddresses.isEmpty()) }

    val subtotal = cartItems.sumOf { it.quantity * it.foodItem.price }
    val deliveryFee = if (cartItems.isNotEmpty()) 0.0 else 0.0
    val total = subtotal + deliveryFee

    fun getQuantityEmoji(qty: Int): String {
        return when (qty) {
            1 -> "1️⃣"
            2 -> "2️⃣"
            3 -> "3️⃣"
            4 -> "4️⃣"
            5 -> "5️⃣"
            6 -> "6️⃣"
            7 -> "7️⃣"
            8 -> "8️⃣"
            9 -> "9️⃣"
            10 -> "🔟"
            else -> "${qty}️⃣"
        }
    }

    fun fetchCurrentLocationAndSetAddress() {
        val locationManager = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager
        val isGpsEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) || locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)

        if (!isGpsEnabled) {
            Toast.makeText(context, "Location/GPS is OFF. Opening Settings to enable GPS...", Toast.LENGTH_LONG).show()
            context.startActivity(Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS))
            return
        }

        try {
            val lastLocation = try {
                locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER)
                    ?: locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER)
            } catch (se: SecurityException) {
                null
            }

            if (lastLocation != null) {
                val geocoder = Geocoder(context, Locale.getDefault())
                @Suppress("DEPRECATION")
                val addresses = geocoder.getFromLocation(lastLocation.latitude, lastLocation.longitude, 1)
                val fullAddr = addresses?.firstOrNull()?.getAddressLine(0) ?: "Lat: ${lastLocation.latitude}, Lng: ${lastLocation.longitude}"
                deliveryAddress = fullAddr
                Toast.makeText(context, "Location fetched successfully!", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(context, "Fetching GPS location, please retry in a moment...", Toast.LENGTH_SHORT).show()
            }
        } catch (e: Exception) {
            Toast.makeText(context, "Error reading location: ${e.message}", Toast.LENGTH_SHORT).show()
        }
    }

    val locationPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            fetchCurrentLocationAndSetAddress()
        } else {
            Toast.makeText(context, "Location permission required to fetch address", Toast.LENGTH_SHORT).show()
        }
    }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            TopAppBar(
                title = { Text(text = "My Cart 🛒", fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = null
                        )
                    }
                }
            )
        }
    ) { innerPadding ->
        if (cartItems.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "Your Cart is Empty 🛒",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .padding(16.dp)
            ) {
                LazyColumn(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(cartItems) { item ->
                        CartItemRow(
                            cartItem = item,
                            onAdd = { onAddItem(item.foodItem) },
                            onRemove = { onRemoveItem(item.foodItem) }
                        )
                    }

                    item {
                        Spacer(modifier = Modifier.height(16.dp))

                        // 1. Saved Delivery Address Section Title
                        Text(
                            text = "Select Delivery Address 📍",
                            fontWeight = FontWeight.ExtraBold,
                            fontSize = 18.sp,
                            color = headingColor
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        // List of Saved Addresses
                        if (AddressRepository.savedAddresses.isNotEmpty()) {
                            AddressRepository.savedAddresses.forEach { addr ->
                                val isSelected = selectedAddressId == addr.id
                                Card(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 4.dp)
                                        .clickable {
                                            selectedAddressId = addr.id
                                            customerName = addr.name
                                            customerPhone = addr.phone
                                            pgHostel = addr.pgHostel
                                            deliveryAddress = addr.fullAddress
                                            showAddNewAddressForm = false
                                        },
                                    shape = RoundedCornerShape(12.dp),
                                    colors = CardDefaults.cardColors(
                                        containerColor = if (isSelected) cardSelectedBgColor else cardBgColor
                                    ),
                                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                                ) {
                                    Row(
                                        modifier = Modifier.padding(12.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        RadioButton(
                                            selected = isSelected,
                                            onClick = {
                                                selectedAddressId = addr.id
                                                customerName = addr.name
                                                customerPhone = addr.phone
                                                pgHostel = addr.pgHostel
                                                deliveryAddress = addr.fullAddress
                                                showAddNewAddressForm = false
                                            },
                                            colors = RadioButtonDefaults.colors(selectedColor = Color(0xFFE60023))
                                        )

                                        Spacer(modifier = Modifier.width(8.dp))

                                        Column(modifier = Modifier.weight(1f)) {
                                            Text(
                                                text = "${addr.name} (${addr.phone})",
                                                fontWeight = FontWeight.ExtraBold,
                                                fontSize = 15.sp,
                                                color = textColorPrimary
                                            )
                                            if (addr.pgHostel.isNotBlank()) {
                                                Text(
                                                    text = "PG/Hostel: ${addr.pgHostel}",
                                                    fontSize = 13.sp,
                                                    fontWeight = FontWeight.Bold,
                                                    color = Color(0xFFFF9800)
                                                )
                                            }
                                            Text(
                                                text = addr.fullAddress,
                                                fontSize = 13.sp,
                                                fontWeight = FontWeight.SemiBold,
                                                color = Color(0xFF4CAF50)
                                            )
                                        }
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(8.dp))
                        }

                        // Add New Address Button placed below list of addresses
                        Button(
                            onClick = {
                                showAddNewAddressForm = !showAddNewAddressForm
                                if (showAddNewAddressForm) {
                                    selectedAddressId = null
                                    customerName = ""
                                    customerPhone = ""
                                    pgHostel = ""
                                    deliveryAddress = ""
                                }
                            },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFE60023))
                        ) {
                            Icon(imageVector = Icons.Default.Add, contentDescription = null, tint = Color.White)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = if (showAddNewAddressForm) "Cancel New Address Form" else "➕ Add New Delivery Address",
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                        }

                        // Add New Address Form
                        if (showAddNewAddressForm) {
                            Spacer(modifier = Modifier.height(12.dp))

                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(16.dp),
                                colors = CardDefaults.cardColors(containerColor = cardBgColor),
                                elevation = CardDefaults.cardElevation(defaultElevation = 3.dp)
                            ) {
                                Column(modifier = Modifier.padding(16.dp)) {
                                    Text(
                                        text = "Enter New Delivery Details:",
                                        fontWeight = FontWeight.ExtraBold,
                                        fontSize = 16.sp,
                                        color = headingColor
                                    )

                                    Spacer(modifier = Modifier.height(10.dp))

                                    OutlinedTextField(
                                        value = customerName,
                                        onValueChange = { customerName = it },
                                        label = { Text(text = "Full Name *", fontWeight = FontWeight.Bold, color = textColorPrimary) },
                                        placeholder = { Text(text = "e.g., John Doe", color = textColorSecondary) },
                                        textStyle = TextStyle(fontWeight = FontWeight.Bold, color = textColorPrimary, fontSize = 15.sp),
                                        colors = OutlinedTextFieldDefaults.colors(
                                            focusedBorderColor = Color(0xFFE60023),
                                            unfocusedBorderColor = Color.Gray
                                        ),
                                        modifier = Modifier.fillMaxWidth(),
                                        singleLine = true,
                                        leadingIcon = { Icon(imageVector = Icons.Default.Person, contentDescription = null, tint = headingColor) }
                                    )

                                    Spacer(modifier = Modifier.height(8.dp))

                                    OutlinedTextField(
                                        value = customerPhone,
                                        onValueChange = { customerPhone = it },
                                        label = { Text(text = "Phone Number *", fontWeight = FontWeight.Bold, color = textColorPrimary) },
                                        placeholder = { Text(text = "e.g., 9871931413", color = textColorSecondary) },
                                        textStyle = TextStyle(fontWeight = FontWeight.Bold, color = textColorPrimary, fontSize = 15.sp),
                                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                                        colors = OutlinedTextFieldDefaults.colors(
                                            focusedBorderColor = Color(0xFFE60023),
                                            unfocusedBorderColor = Color.Gray
                                        ),
                                        modifier = Modifier.fillMaxWidth(),
                                        singleLine = true,
                                        leadingIcon = { Icon(imageVector = Icons.Default.Phone, contentDescription = null, tint = headingColor) }
                                    )

                                    Spacer(modifier = Modifier.height(8.dp))

                                    OutlinedTextField(
                                        value = pgHostel,
                                        onValueChange = { pgHostel = it },
                                        label = { Text(text = "PG / Hostel Name", fontWeight = FontWeight.Bold, color = textColorPrimary) },
                                        placeholder = { Text(text = "e.g., Green PG / Block A", color = textColorSecondary) },
                                        textStyle = TextStyle(fontWeight = FontWeight.Bold, color = textColorPrimary, fontSize = 15.sp),
                                        colors = OutlinedTextFieldDefaults.colors(
                                            focusedBorderColor = Color(0xFFE60023),
                                            unfocusedBorderColor = Color.Gray
                                        ),
                                        modifier = Modifier.fillMaxWidth(),
                                        singleLine = true,
                                        leadingIcon = { Icon(imageVector = Icons.Default.Home, contentDescription = null, tint = headingColor) }
                                    )

                                    Spacer(modifier = Modifier.height(12.dp))

                                    // VIBRANT COLORED "Use Current Location" Button
                                    Button(
                                        onClick = {
                                            locationPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
                                        },
                                        modifier = Modifier.fillMaxWidth(),
                                        shape = RoundedCornerShape(12.dp),
                                        colors = ButtonDefaults.buttonColors(
                                            containerColor = Color(0xFF1E88E5), // Vibrant Ocean Blue
                                            contentColor = Color.White
                                        )
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.MyLocation,
                                            contentDescription = null,
                                            tint = Color.White,
                                            modifier = Modifier.size(20.dp)
                                        )
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Text(
                                            text = "📍 USE CURRENT LOCATION (GPS)",
                                            fontSize = 13.sp,
                                            fontWeight = FontWeight.Black,
                                            color = Color.White
                                        )
                                    }

                                    Spacer(modifier = Modifier.height(8.dp))

                                    OutlinedTextField(
                                        value = deliveryAddress,
                                        onValueChange = { deliveryAddress = it },
                                        label = { Text(text = "Address / Room No. *", fontWeight = FontWeight.Bold, color = textColorPrimary) },
                                        placeholder = { Text(text = "Enter manually or click 'Use Current Location'", color = textColorSecondary) },
                                        textStyle = TextStyle(fontWeight = FontWeight.Bold, color = textColorPrimary, fontSize = 15.sp),
                                        colors = OutlinedTextFieldDefaults.colors(
                                            focusedBorderColor = Color(0xFFE60023),
                                            unfocusedBorderColor = Color.Gray
                                        ),
                                        modifier = Modifier.fillMaxWidth(),
                                        singleLine = true
                                    )

                                    Spacer(modifier = Modifier.height(12.dp))

                                    // 💾 SAVE ADDRESS BUTTON
                                    Button(
                                        onClick = {
                                            if (customerName.isNotBlank() && customerPhone.isNotBlank() && deliveryAddress.isNotBlank()) {
                                                val newAddr = Address(
                                                    id = System.currentTimeMillis().toString(),
                                                    name = customerName,
                                                    phone = customerPhone,
                                                    pgHostel = pgHostel,
                                                    fullAddress = deliveryAddress
                                                )
                                                AddressRepository.saveAddress(newAddr)
                                                selectedAddressId = newAddr.id
                                                showAddNewAddressForm = false
                                                Toast.makeText(context, "Address saved successfully! 📍", Toast.LENGTH_SHORT).show()
                                            } else {
                                                Toast.makeText(context, "Please fill Name, Phone and Address to save!", Toast.LENGTH_SHORT).show()
                                            }
                                        },
                                        modifier = Modifier.fillMaxWidth(),
                                        shape = RoundedCornerShape(12.dp),
                                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2E7D32))
                                    ) {
                                        Icon(imageVector = Icons.Default.Save, contentDescription = null, tint = Color.White)
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Text(text = "💾 SAVE ADDRESS", fontWeight = FontWeight.Bold, color = Color.White)
                                    }
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        // Bill Details Summary Card
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.cardColors(
                                containerColor = cardBgColor
                            ),
                            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                        ) {
                            Column(
                                modifier = Modifier.padding(16.dp)
                            ) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text(text = "Items Subtotal", fontWeight = FontWeight.Bold, color = textColorPrimary)
                                    Text(text = "₹${subtotal.toInt()}", fontWeight = FontWeight.Bold, color = textColorPrimary)
                                }

                                Spacer(modifier = Modifier.height(8.dp))

                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text(text = "Delivery Fee", fontWeight = FontWeight.Bold, color = Color(0xFF4CAF50))
                                    Text(text = "₹${deliveryFee.toInt()} (FREE)", fontWeight = FontWeight.Bold, color = Color(0xFF4CAF50))
                                }

                                HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp))

                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text(
                                        text = "Total Amount",
                                        fontWeight = FontWeight.ExtraBold,
                                        fontSize = 18.sp,
                                        color = textColorPrimary
                                    )
                                    Text(
                                        text = "₹${total.toInt()}",
                                        fontWeight = FontWeight.ExtraBold,
                                        fontSize = 18.sp,
                                        color = Color(0xFFE60023)
                                    )
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                CustomButton(
                    text = "Place Order • ₹${total.toInt()}",
                    onClick = {
                        // Ensure address is saved
                        if (customerName.isNotBlank() && customerPhone.isNotBlank() && deliveryAddress.isNotBlank()) {
                            val newAddress = Address(
                                id = selectedAddressId ?: System.currentTimeMillis().toString(),
                                name = customerName,
                                phone = customerPhone,
                                pgHostel = pgHostel,
                                fullAddress = deliveryAddress
                            )
                            AddressRepository.saveAddress(newAddress)
                        }

                        val summary = cartItems.joinToString("\n") {
                            val qtyEmoji = getQuantityEmoji(it.quantity)
                            "🔴 $qtyEmoji x ${it.foodItem.name} (₹${(it.quantity * it.foodItem.price).toInt()})"
                        }
                        val currentUser = FirebaseAuth.getInstance().currentUser
                        val currentUserId = currentUser?.uid ?: ""
                        val currentEmail = currentUser?.email ?: ""

                        UserPreferences.saveUserPhone(context, customerPhone)
                        if (customerName.isNotBlank()) {
                            UserPreferences.saveUserName(context, customerName)
                        }
                        if (currentEmail.isNotBlank()) {
                            UserPreferences.saveUserEmail(context, currentEmail)
                        }

                        val newOrder = Order(
                            id = "ORD_" + System.currentTimeMillis().toString(),
                            userId = currentUserId,
                            customerEmail = currentEmail,
                            customerName = customerName,
                            customerPhone = customerPhone,
                            pgHostel = pgHostel,
                            deliveryAddress = deliveryAddress,
                            itemsSummary = summary,
                            totalAmount = total,
                            timestamp = System.currentTimeMillis(),
                            status = "Preparing 🍳"
                        )
                        OrderRepository.placeOrder(newOrder)

                        // Format WhatsApp order message with requested item line style (🔴 2️⃣ x hhj (₹0)) and chicken leg emoji 🍗
                        try {
                            val mapsUrl = "https://www.google.com/maps/search/?api=1&query=${URLEncoder.encode(deliveryAddress, "UTF-8")}"

                            val waMsg = """
                                🍗 *NEW ORDER RECEIVED - AOne Restaurant* 🍗
reload
                                *Ordered Items:*
                                $summary

                                💰 *Grand Total: ₹${total.toInt()}*

                                *Customer & Delivery Info:*
                                👤 *Name:* $customerName
                                📞 *Phone:* $customerPhone
                                🏠 *PG/Hostel:* ${pgHostel.ifBlank { "N/A" }}
                                📍 *Address:* $deliveryAddress
                                🗺️ *Location Link:* $mapsUrl
                            """.trimIndent()

                            val encodedMsg = URLEncoder.encode(waMsg, "UTF-8")
                            val whatsappUrl = "https://api.whatsapp.com/send?phone=919871931413&text=$encodedMsg"
                            val waIntent = Intent(Intent.ACTION_VIEW, whatsappUrl.toUri())
                            context.startActivity(waIntent)
                        } catch (e: Exception) {
                            // Fallback if WhatsApp is not installed
                        }

                        onPlaceOrder()
                    },
                    enabled = customerName.isNotBlank() && customerPhone.isNotBlank() && deliveryAddress.isNotBlank()
                )
            }
        }
    }
}
