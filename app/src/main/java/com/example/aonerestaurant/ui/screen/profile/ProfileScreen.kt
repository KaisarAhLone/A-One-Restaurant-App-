package com.example.aonerestaurant.ui.screen.profile

import android.Manifest
import android.content.Context
import android.content.Intent
import android.location.Geocoder
import android.location.LocationManager
import android.provider.Settings
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.automirrored.filled.Help
import androidx.compose.material.icons.automirrored.filled.ReceiptLong
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.MyLocation
import androidx.compose.material.icons.filled.Notifications
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
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.aonerestaurant.data.model.Address
import com.example.aonerestaurant.data.repository.AddressRepository
import com.example.aonerestaurant.ui.screen.home.HomeBottomNavBar
import com.example.aonerestaurant.ui.screen.home.MainTab
import com.google.firebase.auth.FirebaseAuth
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    onNavigateToTab: (MainTab) -> Unit,
    onNavigateToOrders: () -> Unit,
    onLogout: () -> Unit,
    modifier: Modifier = Modifier,
    onBackClick: (() -> Unit)? = null
) {
    val context = LocalContext.current
    val currentUser = FirebaseAuth.getInstance().currentUser
    val isDark = isSystemInDarkTheme()

    val userName = currentUser?.displayName?.ifBlank { "AOne Food Lover" } ?: "AOne Food Lover"
    val userEmail = currentUser?.email?.ifBlank { "user@aonerestaurant.com" } ?: "Guest User"
    val photoUrl = currentUser?.photoUrl?.toString()

    val outerBgColor = if (isDark) Color(0xFF121218) else Color(0xFFF8F9FA)
    val cardBgColor = if (isDark) Color(0xFF1E1E2A) else Color.White
    val headingColor = if (isDark) Color(0xFFFFB703) else Color(0xFF6A040F)
    val textColorPrimary = if (isDark) Color.White else Color(0xFF1D1E2C)
    val textColorSecondary = if (isDark) Color(0xFFCCCCCC) else Color(0xFF555555)

    var showAddressesSection by remember { mutableStateOf(false) }
    var showAddAddressForm by remember { mutableStateOf(false) }

    var editingAddressId by remember { mutableStateOf<String?>(null) }
    var newName by remember { mutableStateOf("") }
    var newPhone by remember { mutableStateOf("") }
    var newPgHostel by remember { mutableStateOf("") }
    var newAddress by remember { mutableStateOf("") }

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
                newAddress = fullAddr
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
                title = { Text(text = "My Profile 👤", fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface) },
                navigationIcon = {
                    if (onBackClick != null) {
                        IconButton(onClick = onBackClick) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = "Back"
                            )
                        }
                    }
                }
            )
        },
        bottomBar = {
            HomeBottomNavBar(
                currentTab = MainTab.PROFILE,
                onTabSelected = onNavigateToTab
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
            // Profile Info Header Card
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = cardBgColor),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Box(
                        modifier = Modifier
                            .size(90.dp)
                            .clip(CircleShape)
                            .background(Color(0xFF6A040F))
                            .border(2.dp, Color(0xFFFFB703), CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        if (!photoUrl.isNullOrBlank()) {
                            AsyncImage(
                                model = photoUrl,
                                contentDescription = "User Photo",
                                modifier = Modifier.fillMaxSize()
                            )
                        } else {
                            Icon(
                                imageVector = Icons.Default.Person,
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier.size(50.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Text(
                        text = userName,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = textColorPrimary
                    )

                    Spacer(modifier = Modifier.height(2.dp))

                    Text(
                        text = userEmail,
                        fontSize = 13.sp,
                        color = textColorSecondary
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(20.dp))
                            .background(Color(0xFFFFF3E0))
                            .padding(horizontal = 12.dp, vertical = 4.dp)
                    ) {
                        Text(
                            text = "👑 Premium Gourmet Member",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFFE65100)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Action Items
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = cardBgColor),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp)
                ) {
                    ProfileOptionRow(
                        icon = Icons.AutoMirrored.Filled.ReceiptLong,
                        title = "My Orders",
                        subtitle = "View order history & live status",
                        onClick = onNavigateToOrders,
                        textColor = textColorPrimary,
                        subtitleColor = textColorSecondary
                    )

                    HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp))

                    ProfileOptionRow(
                        icon = Icons.Default.LocationOn,
                        title = "Saved Delivery Addresses (${AddressRepository.savedAddresses.size})",
                        subtitle = "View, edit & add flat / house addresses",
                        onClick = {
                            showAddressesSection = !showAddressesSection
                        },
                        textColor = textColorPrimary,
                        subtitleColor = textColorSecondary
                    )

                    HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp))

                    ProfileOptionRow(
                        icon = Icons.Default.Notifications,
                        title = "Notification Preferences",
                        subtitle = "Offers, discounts & order updates",
                        onClick = {
                            Toast.makeText(context, "Notifications Enabled", Toast.LENGTH_SHORT).show()
                        },
                        textColor = textColorPrimary,
                        subtitleColor = textColorSecondary
                    )

                    HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp))

                    ProfileOptionRow(
                        icon = Icons.AutoMirrored.Filled.Help,
                        title = "Customer Support & Help",
                        subtitle = "Contact AOne Restaurant support",
                        onClick = {
                            Toast.makeText(context, "AOne Support: support@aonerestaurant.com", Toast.LENGTH_LONG).show()
                        },
                        textColor = textColorPrimary,
                        subtitleColor = textColorSecondary
                    )
                }
            }

            // Saved Addresses Management Card Section
            if (showAddressesSection) {
                Spacer(modifier = Modifier.height(16.dp))

                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = cardBgColor),
                    elevation = CardDefaults.cardElevation(defaultElevation = 3.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp)
                    ) {
                        Text(
                            text = "Saved Delivery Addresses 📍",
                            fontWeight = FontWeight.ExtraBold,
                            fontSize = 17.sp,
                            color = headingColor
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        if (AddressRepository.savedAddresses.isEmpty()) {
                            Text(
                                text = "No saved addresses yet. Click below to add a new address!",
                                fontSize = 13.sp,
                                color = textColorSecondary,
                                fontWeight = FontWeight.SemiBold
                            )
                            Spacer(modifier = Modifier.height(10.dp))
                        } else {
                            AddressRepository.savedAddresses.forEach { addr ->
                                Card(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 4.dp),
                                    shape = RoundedCornerShape(12.dp),
                                    colors = CardDefaults.cardColors(
                                        containerColor = if (isDark) Color(0xFF2A2A3A) else Color(0xFFF8F9FA)
                                    )
                                ) {
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(12.dp),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
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

                                        Row {
                                            // Edit Button ✏️
                                            IconButton(
                                                onClick = {
                                                    editingAddressId = addr.id
                                                    newName = addr.name
                                                    newPhone = addr.phone
                                                    newPgHostel = addr.pgHostel
                                                    newAddress = addr.fullAddress
                                                    showAddAddressForm = true
                                                }
                                            ) {
                                                Icon(
                                                    imageVector = Icons.Default.Edit,
                                                    contentDescription = "Edit Address",
                                                    tint = Color(0xFF2196F3)
                                                )
                                            }

                                            // Delete Button 🗑️
                                            IconButton(
                                                onClick = {
                                                    AddressRepository.removeAddress(addr)
                                                    Toast.makeText(context, "Address deleted!", Toast.LENGTH_SHORT).show()
                                                }
                                            ) {
                                                Icon(
                                                    imageVector = Icons.Default.Delete,
                                                    contentDescription = "Delete Address",
                                                    tint = Color(0xFFD90429)
                                                )
                                            }
                                        }
                                    }
                                }
                            }
                            Spacer(modifier = Modifier.height(10.dp))
                        }

                        Button(
                            onClick = {
                                showAddAddressForm = !showAddAddressForm
                                if (showAddAddressForm) {
                                    editingAddressId = null
                                    newName = ""
                                    newPhone = ""
                                    newPgHostel = ""
                                    newAddress = ""
                                }
                            },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFE60023))
                        ) {
                            Icon(imageVector = Icons.Default.Add, contentDescription = null, tint = Color.White)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = if (showAddAddressForm) "Close Address Form" else "➕ Add New Delivery Address",
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                        }

                        if (showAddAddressForm) {
                            Spacer(modifier = Modifier.height(12.dp))

                            Column {
                                Text(
                                    text = if (editingAddressId != null) "Edit Address Details:" else "Enter Address Details:",
                                    fontWeight = FontWeight.ExtraBold,
                                    fontSize = 15.sp,
                                    color = headingColor
                                )

                                Spacer(modifier = Modifier.height(8.dp))

                                OutlinedTextField(
                                    value = newName,
                                    onValueChange = { newName = it },
                                    label = { Text("Full Name *", fontWeight = FontWeight.Bold, color = textColorPrimary) },
                                    textStyle = TextStyle(fontWeight = FontWeight.Bold, color = textColorPrimary),
                                    colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = Color(0xFFE60023)),
                                    modifier = Modifier.fillMaxWidth(),
                                    singleLine = true,
                                    leadingIcon = { Icon(Icons.Default.Person, contentDescription = null, tint = headingColor) }
                                )

                                Spacer(modifier = Modifier.height(8.dp))

                                OutlinedTextField(
                                    value = newPhone,
                                    onValueChange = { newPhone = it },
                                    label = { Text("Phone Number *", fontWeight = FontWeight.Bold, color = textColorPrimary) },
                                    textStyle = TextStyle(fontWeight = FontWeight.Bold, color = textColorPrimary),
                                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                                    colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = Color(0xFFE60023)),
                                    modifier = Modifier.fillMaxWidth(),
                                    singleLine = true,
                                    leadingIcon = { Icon(Icons.Default.Phone, contentDescription = null, tint = headingColor) }
                                )

                                Spacer(modifier = Modifier.height(8.dp))

                                OutlinedTextField(
                                    value = newPgHostel,
                                    onValueChange = { newPgHostel = it },
                                    label = { Text("PG / Hostel Name", fontWeight = FontWeight.Bold, color = textColorPrimary) },
                                    textStyle = TextStyle(fontWeight = FontWeight.Bold, color = textColorPrimary),
                                    colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = Color(0xFFE60023)),
                                    modifier = Modifier.fillMaxWidth(),
                                    singleLine = true,
                                    leadingIcon = { Icon(Icons.Default.Home, contentDescription = null, tint = headingColor) }
                                )

                                Spacer(modifier = Modifier.height(10.dp))

                                Button(
                                    onClick = {
                                        locationPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
                                    },
                                    modifier = Modifier.fillMaxWidth(),
                                    shape = RoundedCornerShape(12.dp),
                                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1E88E5))
                                ) {
                                    Icon(Icons.Default.MyLocation, contentDescription = null, tint = Color.White)
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text("📍 USE CURRENT LOCATION (GPS)", fontWeight = FontWeight.Black, color = Color.White)
                                }

                                Spacer(modifier = Modifier.height(8.dp))

                                OutlinedTextField(
                                    value = newAddress,
                                    onValueChange = { newAddress = it },
                                    label = { Text("Address / Room No. *", fontWeight = FontWeight.Bold, color = textColorPrimary) },
                                    textStyle = TextStyle(fontWeight = FontWeight.Bold, color = textColorPrimary),
                                    colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = Color(0xFFE60023)),
                                    modifier = Modifier.fillMaxWidth(),
                                    singleLine = true
                                )

                                Spacer(modifier = Modifier.height(12.dp))

                                Button(
                                    onClick = {
                                        if (newName.isNotBlank() && newPhone.isNotBlank() && newAddress.isNotBlank()) {
                                            val saved = Address(
                                                id = editingAddressId ?: System.currentTimeMillis().toString(),
                                                name = newName,
                                                phone = newPhone,
                                                pgHostel = newPgHostel,
                                                fullAddress = newAddress
                                            )
                                            AddressRepository.saveAddress(saved)
                                            editingAddressId = null
                                            newName = ""
                                            newPhone = ""
                                            newPgHostel = ""
                                            newAddress = ""
                                            showAddAddressForm = false
                                            Toast.makeText(context, "Address saved to Profile! 📍", Toast.LENGTH_SHORT).show()
                                        } else {
                                            Toast.makeText(context, "Please enter Name, Phone and Address!", Toast.LENGTH_SHORT).show()
                                        }
                                    },
                                    modifier = Modifier.fillMaxWidth(),
                                    shape = RoundedCornerShape(12.dp),
                                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2E7D32))
                                ) {
                                    Icon(Icons.Default.Save, contentDescription = null, tint = Color.White)
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(text = if (editingAddressId != null) "💾 UPDATE ADDRESS" else "💾 SAVE ADDRESS TO PROFILE", fontWeight = FontWeight.Bold, color = Color.White)
                                }
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Logout Button
            OutlinedButton(
                onClick = {
                    FirebaseAuth.getInstance().signOut()
                    Toast.makeText(context, "Logged out successfully!", Toast.LENGTH_SHORT).show()
                    onLogout()
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.outlinedButtonColors(
                    contentColor = Color(0xFFD90429)
                )
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ExitToApp,
                    contentDescription = "Logout"
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Sign Out / Logout",
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp
                )
            }
        }
    }
}

@Composable
fun ProfileOptionRow(
    icon: ImageVector,
    title: String,
    subtitle: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    textColor: Color = Color(0xFF1D1E2C),
    subtitleColor: Color = Color.Gray
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(Color(0xFF6A040F).copy(alpha = 0.15f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = Color(0xFFE60023),
                    modifier = Modifier.size(20.dp)
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column {
                Text(
                    text = title,
                    fontWeight = FontWeight.Bold,
                    fontSize = 15.sp,
                    color = textColor
                )
                Text(
                    text = subtitle,
                    fontSize = 12.sp,
                    color = subtitleColor
                )
            }
        }

        Icon(
            imageVector = Icons.Default.ChevronRight,
            contentDescription = null,
            tint = subtitleColor
        )
    }
}
