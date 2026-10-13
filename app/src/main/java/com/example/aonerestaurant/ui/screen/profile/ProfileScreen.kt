package com.example.aonerestaurant.ui.screen.profile

import android.Manifest
import android.content.Context
import android.content.Intent
import android.location.Geocoder
import android.location.LocationManager
import android.net.Uri
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
import androidx.compose.material.icons.automirrored.filled.ReceiptLong
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AddPhotoAlternate
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
import androidx.compose.material3.AlertDialog
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
import androidx.compose.material3.TextButton
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
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.net.toUri
import com.example.aonerestaurant.data.model.Address
import com.example.aonerestaurant.data.repository.AddressRepository
import com.example.aonerestaurant.data.repository.UserPreferences
import com.example.aonerestaurant.ui.screen.home.HomeBottomNavBar
import com.example.aonerestaurant.ui.screen.home.MainTab
import com.example.aonerestaurant.utils.ImageUtils
import com.example.aonerestaurant.utils.ItemImageThumbnail
import com.google.firebase.auth.FirebaseAuth
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    onNavigateToTab: (MainTab) -> Unit,
    onNavigateToOrders: () -> Unit,
    onLogout: () -> Unit,
    modifier: Modifier = Modifier,
    onOwnerPortal: () -> Unit = {},
    onBackClick: (() -> Unit)? = null
) {
    val context = LocalContext.current
    val currentUser = FirebaseAuth.getInstance().currentUser
    val isDark = isSystemInDarkTheme()

    val userPhone = UserPreferences.getUserPhone(context)
    val userName = currentUser?.displayName?.ifBlank {
        UserPreferences.getUserName(context).ifBlank { "A One Food Lover" }
    } ?: UserPreferences.getUserName(context).ifBlank { "A One Food Lover" }
    val userEmail = currentUser?.email?.ifBlank { "user@aonerestaurant.com" } ?: "Guest Foodie"

    val outerBgColor = if (isDark) Color(0xFF121218) else Color(0xFFF8F9FA)
    val cardBgColor = if (isDark) Color(0xFF1E1E2A) else Color.White
    val headingColor = if (isDark) Color(0xFFFFB703) else Color(0xFF6A040F)
    val textColorPrimary = if (isDark) Color.White else Color(0xFF1D1E2C)
    val textColorSecondary = if (isDark) Color(0xFFCCCCCC) else Color(0xFF555555)

    var userProfileImage by remember {
        mutableStateOf(UserPreferences.getUserProfileImage(context))
    }

    val profilePhotoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let {
            val base64 = ImageUtils.uriToBase64Compressed(context, it)
            val finalUri = base64 ?: it.toString()
            userProfileImage = finalUri
            UserPreferences.saveUserProfileImage(context, finalUri)
            Toast.makeText(context, "Profile photo updated successfully! 📸", Toast.LENGTH_SHORT).show()
        }
    }

    var showAddressesSection by remember { mutableStateOf(false) }
    var showAddAddressForm by remember { mutableStateOf(false) }
    var showOwnerDialog by remember { mutableStateOf(false) }
    var ownerKeyInput by remember { mutableStateOf("") }
    var keyError by remember { mutableStateOf(false) }

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
            // Customer Profile Card
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
                            .border(2.5.dp, Color(0xFFFFB703), CircleShape)
                            .clickable { profilePhotoPickerLauncher.launch("image/*") },
                        contentAlignment = Alignment.Center
                    ) {
                        ItemImageThumbnail(
                            imageUri = userProfileImage,
                            contentDescription = "User Photo",
                            fallbackIcon = Icons.Default.Person,
                            modifier = Modifier.fillMaxSize()
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Row(
                        modifier = Modifier
                            .clip(RoundedCornerShape(12.dp))
                            .clickable { profilePhotoPickerLauncher.launch("image/*") }
                            .padding(horizontal = 8.dp, vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.AddPhotoAlternate,
                            contentDescription = null,
                            tint = Color(0xFF2196F3),
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "📷 Change Profile Photo",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF2196F3)
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))

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

                    if (userPhone.isNotBlank()) {
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = "📞 $userPhone",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = Color(0xFF6A040F)
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(20.dp))
                            .background(Color(0xFFFFF3E0))
                            .padding(horizontal = 14.dp, vertical = 5.dp)
                    ) {
                        Text(
                            text = "👑 VIP Biryani Lover Member",
                            fontSize = 11.5.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFFE65100)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(18.dp))

            // Action Items Card
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
                        subtitle = "View your live orders & receipts",
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

                    // Saved Addresses Content expands directly under this row
                    if (showAddressesSection) {
                        Spacer(modifier = Modifier.height(8.dp))

                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(if (isDark) Color(0xFF232332) else Color(0xFFF0F4F8), RoundedCornerShape(12.dp))
                                .padding(12.dp)
                        ) {
                            Text(
                                text = "Your Saved Addresses 📍",
                                fontWeight = FontWeight.ExtraBold,
                                fontSize = 15.sp,
                                color = headingColor
                            )

                            Spacer(modifier = Modifier.height(8.dp))

                            if (AddressRepository.savedAddresses.isEmpty()) {
                                Text(
                                    text = "No saved addresses yet.",
                                    fontSize = 12.sp,
                                    color = textColorSecondary
                                )
                            } else {
                                AddressRepository.savedAddresses.forEach { addr ->
                                    Card(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(vertical = 4.dp),
                                        shape = RoundedCornerShape(10.dp),
                                        colors = CardDefaults.cardColors(
                                            containerColor = cardBgColor
                                        )
                                    ) {
                                        Row(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .padding(10.dp),
                                            horizontalArrangement = Arrangement.SpaceBetween,
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Column(modifier = Modifier.weight(1f)) {
                                                Text(
                                                    text = "${addr.name} (${addr.phone})",
                                                    fontWeight = FontWeight.Bold,
                                                    fontSize = 14.sp,
                                                    color = textColorPrimary
                                                )
                                                if (addr.pgHostel.isNotBlank()) {
                                                    Text(
                                                        text = "PG/Hostel: ${addr.pgHostel}",
                                                        fontSize = 12.sp,
                                                        color = Color(0xFFFF9800)
                                                    )
                                                }
                                                Text(
                                                    text = addr.fullAddress,
                                                    fontSize = 12.sp,
                                                    color = Color(0xFF4CAF50)
                                                )
                                            }

                                            Row {
                                                IconButton(
                                                    onClick = {
                                                        editingAddressId = addr.id
                                                        newName = addr.name
                                                        newPhone = addr.phone
                                                        newPgHostel = addr.pgHostel
                                                        newAddress = addr.fullAddress
                                                        showAddAddressForm = true
                                                    },
                                                    modifier = Modifier.size(32.dp)
                                                ) {
                                                    Icon(
                                                        imageVector = Icons.Default.Edit,
                                                        contentDescription = "Edit Address",
                                                        tint = Color(0xFF2196F3),
                                                        modifier = Modifier.size(18.dp)
                                                    )
                                                }

                                                IconButton(
                                                    onClick = {
                                                        AddressRepository.removeAddress(addr)
                                                        Toast.makeText(context, "Address deleted!", Toast.LENGTH_SHORT).show()
                                                    },
                                                    modifier = Modifier.size(32.dp)
                                                ) {
                                                    Icon(
                                                        imageVector = Icons.Default.Delete,
                                                        contentDescription = "Delete Address",
                                                        tint = Color(0xFFD90429),
                                                        modifier = Modifier.size(18.dp)
                                                    )
                                                }
                                            }
                                        }
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(8.dp))

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
                                shape = RoundedCornerShape(10.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFE60023))
                            ) {
                                Icon(imageVector = Icons.Default.Add, contentDescription = null, tint = Color.White)
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = if (showAddAddressForm) "Close Address Form" else "➕ Add New Delivery Address",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 13.sp,
                                    color = Color.White
                                )
                            }

                            if (showAddAddressForm) {
                                Spacer(modifier = Modifier.height(10.dp))

                                OutlinedTextField(
                                    value = newName,
                                    onValueChange = { newName = it },
                                    label = { Text("Full Name *", color = textColorPrimary) },
                                    textStyle = TextStyle(fontWeight = FontWeight.Bold, color = textColorPrimary),
                                    colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = Color(0xFFE60023)),
                                    modifier = Modifier.fillMaxWidth(),
                                    singleLine = true
                                )

                                Spacer(modifier = Modifier.height(6.dp))

                                OutlinedTextField(
                                    value = newPhone,
                                    onValueChange = { newPhone = it },
                                    label = { Text("Phone Number *", color = textColorPrimary) },
                                    textStyle = TextStyle(fontWeight = FontWeight.Bold, color = textColorPrimary),
                                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                                    colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = Color(0xFFE60023)),
                                    modifier = Modifier.fillMaxWidth(),
                                    singleLine = true
                                )

                                Spacer(modifier = Modifier.height(6.dp))

                                OutlinedTextField(
                                    value = newPgHostel,
                                    onValueChange = { newPgHostel = it },
                                    label = { Text("PG / Hostel Name", color = textColorPrimary) },
                                    textStyle = TextStyle(fontWeight = FontWeight.Bold, color = textColorPrimary),
                                    colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = Color(0xFFE60023)),
                                    modifier = Modifier.fillMaxWidth(),
                                    singleLine = true
                                )

                                Spacer(modifier = Modifier.height(8.dp))

                                Button(
                                    onClick = {
                                        locationPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
                                    },
                                    modifier = Modifier.fillMaxWidth(),
                                    shape = RoundedCornerShape(10.dp),
                                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1E88E5))
                                ) {
                                    Icon(Icons.Default.MyLocation, contentDescription = null, tint = Color.White)
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text("📍 USE CURRENT LOCATION (GPS)", fontWeight = FontWeight.Bold, fontSize = 12.sp, color = Color.White)
                                }

                                Spacer(modifier = Modifier.height(6.dp))

                                OutlinedTextField(
                                    value = newAddress,
                                    onValueChange = { newAddress = it },
                                    label = { Text("Address / Room No. *", color = textColorPrimary) },
                                    textStyle = TextStyle(fontWeight = FontWeight.Bold, color = textColorPrimary),
                                    colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = Color(0xFFE60023)),
                                    modifier = Modifier.fillMaxWidth(),
                                    singleLine = true
                                )

                                Spacer(modifier = Modifier.height(10.dp))

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
                                    shape = RoundedCornerShape(10.dp),
                                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2E7D32))
                                ) {
                                    Icon(Icons.Default.Save, contentDescription = null, tint = Color.White)
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(text = if (editingAddressId != null) "💾 UPDATE ADDRESS" else "💾 SAVE ADDRESS", fontWeight = FontWeight.Bold, color = Color.White)
                                }
                            }
                        }
                    }

                    HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp))

                    ProfileOptionRow(
                        icon = Icons.Default.Phone,
                        title = "Call Restaurant Owner",
                        subtitle = "+91 9871931413 • Fast delivery support",
                        onClick = {
                            val intent = Intent(Intent.ACTION_DIAL, "tel:+919871931413".toUri())
                            context.startActivity(intent)
                        },
                        textColor = textColorPrimary,
                        subtitleColor = textColorSecondary
                    )

                    HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp))

                    ProfileOptionRow(
                        icon = Icons.Default.Notifications,
                        title = "WhatsApp Direct Support",
                        subtitle = "Chat with restaurant on WhatsApp",
                        onClick = {
                            val url = "https://api.whatsapp.com/send?phone=919871931413&text=Hello%20A%20One%20Biryani!"
                            val intent = Intent(Intent.ACTION_VIEW, url.toUri())
                            context.startActivity(intent)
                        },
                        textColor = textColorPrimary,
                        subtitleColor = textColorSecondary
                    )

                    HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp))

                    ProfileOptionRow(
                        icon = Icons.Default.Person,
                        title = "Restaurant Owner Portal 👑",
                        subtitle = "Owner admin dashboard & menu controls",
                        onClick = {
                            showOwnerDialog = true
                        },
                        textColor = textColorPrimary,
                        subtitleColor = textColorSecondary
                    )
                }
            }

            if (showOwnerDialog) {
                AlertDialog(
                    onDismissRequest = {
                        showOwnerDialog = false
                        ownerKeyInput = ""
                        keyError = false
                    },
                    title = { Text(text = "Restaurant Owner Portal 👑", fontWeight = FontWeight.Bold) },
                    text = {
                        Column {
                            Text(text = "Enter security key to access owner dashboard:")
                            Spacer(modifier = Modifier.height(12.dp))
                            OutlinedTextField(
                                value = ownerKeyInput,
                                onValueChange = {
                                    ownerKeyInput = it
                                    keyError = false
                                },
                                label = { Text(text = "Security Key") },
                                singleLine = true,
                                visualTransformation = PasswordVisualTransformation(),
                                isError = keyError,
                                supportingText = if (keyError) {
                                    { Text(text = "Incorrect Key") }
                                } else null
                            )
                        }
                    },
                    confirmButton = {
                        TextButton(
                            onClick = {
                                if (ownerKeyInput == "KAIsarAone") {
                                    showOwnerDialog = false
                                    ownerKeyInput = ""
                                    keyError = false
                                    onOwnerPortal()
                                } else {
                                    keyError = true
                                }
                            }
                        ) {
                            Text(text = "Login")
                        }
                    },
                    dismissButton = {
                        TextButton(
                            onClick = {
                                showOwnerDialog = false
                                ownerKeyInput = ""
                                keyError = false
                            }
                        ) {
                            Text(text = "Cancel")
                        }
                    }
                )
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
