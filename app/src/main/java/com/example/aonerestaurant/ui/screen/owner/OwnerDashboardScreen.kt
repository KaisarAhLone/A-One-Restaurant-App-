package com.example.aonerestaurant.ui.screen.owner

import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
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
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AddPhotoAlternate
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.LocalOffer
import androidx.compose.material.icons.filled.Restaurant
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Whatshot
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.aonerestaurant.data.model.FoodItem
import com.example.aonerestaurant.data.model.SpecialCombo
import com.example.aonerestaurant.data.repository.RestaurantRepository
import com.example.aonerestaurant.utils.ImageUtils
import com.example.aonerestaurant.utils.ItemImageThumbnail

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun OwnerDashboardScreen(
    onLogout: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current

    var activeTab by remember { mutableStateOf(0) } // 0 = Single Dish, 1 = Special Combo Package

    // Single Dish Form State
    var itemName by remember { mutableStateOf("") }
    var itemPrice by remember { mutableStateOf("") }
    var itemDesc by remember { mutableStateOf("") }
    var selectedCategoryId by remember { mutableStateOf("1") }
    var selectedImageUri by remember { mutableStateOf<String?>(null) }

    // Special Combo Package Builder State
    var comboName by remember { mutableStateOf("") }
    var comboPrice by remember { mutableStateOf("") }
    var comboNotes by remember { mutableStateOf("") }
    var comboImageUri by remember { mutableStateOf<String?>(null) }
    val selectedComboDishIds = remember { mutableStateListOf<String>() }

    // Dialog States for Popular & Today's Special Selection
    var showPopularDialog by remember { mutableStateOf(false) }
    var showSpecialDialog by remember { mutableStateOf(false) }

    var successMessage by remember { mutableStateOf("") }

    val photoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let {
            val base64Compressed = ImageUtils.uriToBase64Compressed(context, it)
            if (activeTab == 0) {
                selectedImageUri = base64Compressed ?: it.toString()
            } else {
                comboImageUri = base64Compressed ?: it.toString()
            }
        }
    }

    val selectedCategory = RestaurantRepository.categories.find { it.id == selectedCategoryId }
    val isFormValid = itemName.isNotBlank() && itemPrice.isNotBlank()
    val isComboFormValid = comboName.isNotBlank() && comboPrice.isNotBlank() && selectedComboDishIds.isNotEmpty()

    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Owner Live App Dashboard",
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color(0xFF6A040F)
                ),
                actions = {
                    IconButton(onClick = onLogout) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ExitToApp,
                            contentDescription = "Logout",
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
                .background(Color(0xFFF8F9FA))
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Mode Selector Tabs (Single Dish vs Combo Package)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Button(
                    onClick = { activeTab = 0 },
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (activeTab == 0) Color(0xFF6A040F) else Color(0xFFE0E0E0),
                        contentColor = if (activeTab == 0) Color.White else Color.Black
                    )
                ) {
                    Text(text = "🍽️ Add / Select Dish", fontWeight = FontWeight.Bold)
                }

                Button(
                    onClick = { activeTab = 1 },
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (activeTab == 1) Color(0xFF2E7D32) else Color(0xFFE0E0E0),
                        contentColor = if (activeTab == 1) Color.White else Color.Black
                    )
                ) {
                    Text(text = "🍱 Combo Builder", fontWeight = FontWeight.Bold)
                }
            }

            if (activeTab == 0) {
                // TAB 0: Single Dish Form & Quick Select Buttons
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.Restaurant,
                                contentDescription = null,
                                tint = Color(0xFF6A040F),
                                modifier = Modifier.size(28.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Add Real Item to Customer App",
                                fontSize = 19.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF1D1E2C)
                            )
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        OutlinedTextField(
                            value = itemName,
                            onValueChange = { itemName = it },
                            label = { Text(text = "Item Name (e.g., Chicken Biryani, Butter Curry)") },
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        OutlinedTextField(
                            value = itemPrice,
                            onValueChange = { itemPrice = it },
                            label = { Text(text = "Price in ₹ (e.g., 220)") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        OutlinedTextField(
                            value = itemDesc,
                            onValueChange = { itemDesc = it },
                            label = { Text(text = "Description (Taste / Quantity)") },
                            modifier = Modifier.fillMaxWidth()
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        Text(
                            text = "1. Select Category (Biryani, Curry, Grilled, Fried)",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF6A040F)
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        FlowRow(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            RestaurantRepository.categories.forEach { category ->
                                val isSelected = selectedCategoryId == category.id
                                FilterChip(
                                    selected = isSelected,
                                    onClick = { selectedCategoryId = category.id },
                                    label = { Text(text = category.name) },
                                    leadingIcon = {
                                        Icon(
                                            imageVector = category.icon,
                                            contentDescription = null,
                                            modifier = Modifier.size(18.dp)
                                        )
                                    },
                                    colors = FilterChipDefaults.filterChipColors(
                                        selectedContainerColor = Color(0xFF6A040F),
                                        selectedLabelColor = Color.White,
                                        selectedLeadingIconColor = Color.White
                                    )
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        Text(
                            text = "2. Add Item Photo (Cross-Device Compressed)",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF6A040F)
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        if (selectedImageUri == null) {
                            OutlinedButton(
                                onClick = { photoPickerLauncher.launch("image/*") },
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(12.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.AddPhotoAlternate,
                                    contentDescription = null
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(text = "Choose Photo from Phone Gallery")
                            }
                        } else {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(150.dp)
                                    .clip(RoundedCornerShape(12.dp))
                                    .border(1.dp, Color.Gray, RoundedCornerShape(12.dp))
                            ) {
                                ItemImageThumbnail(
                                    imageUri = selectedImageUri,
                                    contentDescription = "Item Photo",
                                    modifier = Modifier.fillMaxSize()
                                )

                                IconButton(
                                    onClick = { selectedImageUri = null },
                                    modifier = Modifier
                                        .align(Alignment.TopEnd)
                                        .padding(8.dp)
                                        .background(Color.Black.copy(alpha = 0.6f), CircleShape)
                                        .size(32.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Close,
                                        contentDescription = "Remove photo",
                                        tint = Color.White,
                                        modifier = Modifier.size(18.dp)
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(20.dp))

                        Text(
                            text = "3. Add New Item or Manage Special Sections:",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF6A040F)
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        Button(
                            onClick = {
                                val priceVal = itemPrice.toDoubleOrNull() ?: 0.0
                                val newItem = FoodItem(
                                    id = System.currentTimeMillis().toString(),
                                    name = itemName,
                                    price = priceVal,
                                    description = itemDesc,
                                    categoryId = selectedCategoryId,
                                    imageUri = selectedImageUri
                                )
                                RestaurantRepository.addFoodItem(newItem)
                                successMessage = "Added '$itemName' to ${selectedCategory?.name} Category!"
                                itemName = ""; itemPrice = ""; itemDesc = ""; selectedImageUri = null
                            },
                            enabled = isFormValid,
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1D1E2C))
                        ) {
                            Icon(imageVector = Icons.Default.Restaurant, contentDescription = null)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(text = "➕ Add to ${selectedCategory?.name ?: "Category"} Menu", fontWeight = FontWeight.Bold)
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        // SELECT POPULAR ITEMS BUTTON
                        Button(
                            onClick = {
                                showPopularDialog = true
                            },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFD90429))
                        ) {
                            Icon(imageVector = Icons.Default.Star, contentDescription = null)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(text = "👑 Select Popular Items from Live Menu", fontWeight = FontWeight.Bold)
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        // SELECT TODAY'S SPECIAL BUTTON
                        Button(
                            onClick = {
                                showSpecialDialog = true
                            },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFE65100))
                        ) {
                            Icon(imageVector = Icons.Default.Whatshot, contentDescription = null)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(text = "🔥 Select Today's Special Offer from Menu", fontWeight = FontWeight.Bold)
                        }
                    }
                }
            } else {
                // TAB 1: Special Combo Builder (Group Existing Menu Items)
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.LocalOffer,
                                contentDescription = null,
                                tint = Color(0xFF2E7D32),
                                modifier = Modifier.size(28.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Build Special Combo Package 🍱",
                                fontSize = 19.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF1D1E2C)
                            )
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        OutlinedTextField(
                            value = comboName,
                            onValueChange = { comboName = it },
                            label = { Text(text = "Combo Package Name (e.g., Mega Feast Deal)") },
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        OutlinedTextField(
                            value = comboPrice,
                            onValueChange = { comboPrice = it },
                            label = { Text(text = "Combo Discount Offer Price in ₹ (e.g., 299)") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        OutlinedTextField(
                            value = comboNotes,
                            onValueChange = { comboNotes = it },
                            label = { Text(text = "Combo Notes (e.g., Free Cold Drink & Chutney)") },
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        Text(
                            text = "Select Items from Live Menu to Include in Combo:",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF2E7D32)
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        val selectedDishesList = RestaurantRepository.foodItems.filter { selectedComboDishIds.contains(it.id) }
                        val actualTotalSum = selectedDishesList.sumOf { it.price.toInt() }

                        if (selectedDishesList.isNotEmpty()) {
                            Text(
                                text = "Original Actual Total Price of Selected Dishes: ₹$actualTotalSum",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = Color(0xFFD90429)
                            )
                            Spacer(modifier = Modifier.height(6.dp))
                        }

                        if (RestaurantRepository.foodItems.isEmpty()) {
                            Text(
                                text = "No menu items available yet! Please add menu dishes first in Tab 1.",
                                fontSize = 13.sp,
                                color = Color.Red,
                                fontWeight = FontWeight.SemiBold
                            )
                        } else {
                            RestaurantRepository.foodItems.forEach { foodItem ->
                                val isChecked = selectedComboDishIds.contains(foodItem.id)
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clickable {
                                            if (isChecked) selectedComboDishIds.remove(foodItem.id)
                                            else selectedComboDishIds.add(foodItem.id)
                                        }
                                        .padding(vertical = 6.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Checkbox(
                                        checked = isChecked,
                                        onCheckedChange = { checked ->
                                            if (checked) selectedComboDishIds.add(foodItem.id)
                                            else selectedComboDishIds.remove(foodItem.id)
                                        },
                                        colors = CheckboxDefaults.colors(checkedColor = Color(0xFF2E7D32))
                                    )

                                    Spacer(modifier = Modifier.width(8.dp))

                                    Column {
                                        Text(
                                            text = foodItem.name,
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 15.sp,
                                            color = Color(0xFF1D1E2C)
                                        )
                                        Text(
                                            text = "₹${foodItem.price.toInt()} • ${foodItem.description}",
                                            fontSize = 12.sp,
                                            color = Color.Gray
                                        )
                                    }
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        Text(
                            text = "Add Combo Cover Photo (Optional):",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF2E7D32)
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        if (comboImageUri == null) {
                            OutlinedButton(
                                onClick = { photoPickerLauncher.launch("image/*") },
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(12.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.AddPhotoAlternate,
                                    contentDescription = null
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(text = "Choose Photo for Combo")
                            }
                        } else {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(150.dp)
                                    .clip(RoundedCornerShape(12.dp))
                                    .border(1.dp, Color.Gray, RoundedCornerShape(12.dp))
                            ) {
                                ItemImageThumbnail(
                                    imageUri = comboImageUri,
                                    contentDescription = "Combo Photo",
                                    modifier = Modifier.fillMaxSize()
                                )

                                IconButton(
                                    onClick = { comboImageUri = null },
                                    modifier = Modifier
                                        .align(Alignment.TopEnd)
                                        .padding(8.dp)
                                        .background(Color.Black.copy(alpha = 0.6f), CircleShape)
                                        .size(32.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Close,
                                        contentDescription = "Remove photo",
                                        tint = Color.White,
                                        modifier = Modifier.size(18.dp)
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(20.dp))

                        // Publish Combo Button
                        Button(
                            onClick = {
                                val offerPriceVal = comboPrice.toIntOrNull() ?: 0
                                val selectedDishes = RestaurantRepository.foodItems.filter { selectedComboDishIds.contains(it.id) }
                                val selectedDishNames = selectedDishes.joinToString(" + ") { it.name }
                                val sumActualPrice = selectedDishes.sumOf { it.price.toInt() }

                                val finalComboDesc = if (comboNotes.isNotBlank()) {
                                    "• $selectedDishNames ($comboNotes)"
                                } else {
                                    "• $selectedDishNames"
                                }

                                // Auto-use image of first selected dish if comboImageUri is null
                                val finalImage = comboImageUri ?: selectedDishes.firstOrNull()?.imageUri

                                val newCombo = SpecialCombo(
                                    id = "combo_" + System.currentTimeMillis().toString(),
                                    name = comboName,
                                    description = finalComboDesc,
                                    price = offerPriceVal,
                                    originalPrice = if (sumActualPrice > offerPriceVal) sumActualPrice else (offerPriceVal * 1.25).toInt(),
                                    imageUri = finalImage
                                )

                                RestaurantRepository.addSpecialCombo(newCombo)
                                successMessage = "Published '$comboName' Combo Package ($selectedDishNames) for ₹$offerPriceVal (Actual: ₹$sumActualPrice)!"
                                comboName = ""; comboPrice = ""; comboNotes = ""; comboImageUri = null
                                selectedComboDishIds.clear()
                            },
                            enabled = isComboFormValid,
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2E7D32))
                        ) {
                            Icon(imageVector = Icons.Default.LocalOffer, contentDescription = null, tint = Color.White)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(text = "🍱 Publish Special Combo Deal", fontWeight = FontWeight.Bold, color = Color.White)
                        }
                    }
                }
            }

            if (successMessage.isNotBlank()) {
                Spacer(modifier = Modifier.height(16.dp))
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFE8F5E9))
                ) {
                    Row(
                        modifier = Modifier.padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Check,
                            contentDescription = null,
                            tint = Color(0xFF2E7D32)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = successMessage,
                            color = Color(0xFF2E7D32),
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Section to view & manage currently live menu items and combos
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp)
                ) {
                    Text(
                        text = "Live App Menu (${RestaurantRepository.foodItems.size} dishes, ${RestaurantRepository.specialCombos.size} combos)",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF1D1E2C)
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    Text(
                        text = "Dishes:",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF6A040F)
                    )

                    Spacer(modifier = Modifier.height(6.dp))

                    RestaurantRepository.foodItems.forEachIndexed { index, item ->
                        val catName = RestaurantRepository.categories.find { it.id == item.categoryId }?.name ?: "Biryani"

                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 8.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(
                                modifier = Modifier.weight(1f),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                ItemImageThumbnail(
                                    imageUri = item.imageUri,
                                    contentDescription = item.name,
                                    modifier = Modifier
                                        .size(48.dp)
                                        .clip(RoundedCornerShape(8.dp))
                                )

                                Spacer(modifier = Modifier.width(12.dp))

                                Column {
                                    Text(
                                        text = item.name,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 15.sp
                                    )
                                    Text(
                                        text = "Category: $catName • ₹${item.price.toInt()}",
                                        fontSize = 12.sp,
                                        color = Color.Gray
                                    )
                                    FlowRow(
                                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                                    ) {
                                        if (item.isPopular) {
                                            Text(
                                                text = "👑 Popular",
                                                fontSize = 10.sp,
                                                color = Color(0xFFD90429),
                                                fontWeight = FontWeight.Bold
                                            )
                                        }
                                        if (item.isTodaysSpecial) {
                                            Text(
                                                text = "🔥 Special",
                                                fontSize = 10.sp,
                                                color = Color(0xFFFFB703),
                                                fontWeight = FontWeight.Bold
                                            )
                                        }
                                    }
                                }
                            }

                            IconButton(
                                onClick = {
                                    RestaurantRepository.removeFoodItem(item)
                                    successMessage = "Removed '${item.name}' from live app menu"
                                }
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Delete,
                                    contentDescription = "Delete item",
                                    tint = Color.Red
                                )
                            }
                        }

                        if (index < RestaurantRepository.foodItems.size - 1) {
                            HorizontalDivider()
                        }
                    }

                    if (RestaurantRepository.specialCombos.isNotEmpty()) {
                        Spacer(modifier = Modifier.height(16.dp))
                        HorizontalDivider(thickness = 2.dp, color = Color(0xFF6A040F))
                        Spacer(modifier = Modifier.height(12.dp))

                        Text(
                            text = "Special Combos:",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF2E7D32)
                        )

                        Spacer(modifier = Modifier.height(6.dp))

                        RestaurantRepository.specialCombos.forEachIndexed { index, combo ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 8.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(
                                    modifier = Modifier.weight(1f),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    ItemImageThumbnail(
                                        imageUri = combo.imageUri,
                                        contentDescription = combo.name,
                                        modifier = Modifier
                                            .size(48.dp)
                                            .clip(RoundedCornerShape(8.dp))
                                    )

                                    Spacer(modifier = Modifier.width(12.dp))

                                    Column {
                                        Text(
                                            text = combo.name,
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 15.sp
                                        )
                                        Text(
                                            text = "${combo.description} • ₹${combo.price} (Was ₹${combo.originalPrice})",
                                            fontSize = 12.sp,
                                            color = Color.Gray
                                        )
                                    }
                                }

                                IconButton(
                                    onClick = {
                                        RestaurantRepository.removeSpecialCombo(combo)
                                        successMessage = "Removed combo '${combo.name}'"
                                    }
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Delete,
                                        contentDescription = "Delete combo",
                                        tint = Color.Red
                                    )
                                }
                            }

                            if (index < RestaurantRepository.specialCombos.size - 1) {
                                HorizontalDivider()
                            }
                        }
                    }
                }
            }
        }
    }

    // DIALOG 1: SELECT POPULAR ITEMS
    if (showPopularDialog) {
        AlertDialog(
            onDismissRequest = { showPopularDialog = false },
            title = { Text("Select Popular Items 👑", fontWeight = FontWeight.Bold, color = Color(0xFFD90429)) },
            text = {
                Column(
                    modifier = Modifier.verticalScroll(rememberScrollState())
                ) {
                    Text("Check the dishes you want to feature in the 'Popular Items' section on Home Screen:", fontSize = 13.sp, color = Color.Gray)
                    Spacer(modifier = Modifier.height(10.dp))

                    RestaurantRepository.foodItems.forEach { foodItem ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    RestaurantRepository.togglePopularStatus(foodItem, !foodItem.isPopular)
                                }
                                .padding(vertical = 6.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Checkbox(
                                checked = foodItem.isPopular,
                                onCheckedChange = { checked ->
                                    RestaurantRepository.togglePopularStatus(foodItem, checked)
                                },
                                colors = CheckboxDefaults.colors(checkedColor = Color(0xFFD90429))
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Column {
                                Text(foodItem.name, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                                Text("₹${foodItem.price.toInt()}", fontSize = 12.sp, color = Color.Gray)
                            }
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        showPopularDialog = false
                        Toast.makeText(context, "Popular Items updated! 👑", Toast.LENGTH_SHORT).show()
                    }
                ) {
                    Text("Done", fontWeight = FontWeight.Bold, color = Color(0xFFD90429))
                }
            }
        )
    }

    // DIALOG 2: SELECT TODAY'S SPECIAL
    if (showSpecialDialog) {
        AlertDialog(
            onDismissRequest = { showSpecialDialog = false },
            title = { Text("Select Today's Special Offer 🔥", fontWeight = FontWeight.Bold, color = Color(0xFFE65100)) },
            text = {
                Column(
                    modifier = Modifier.verticalScroll(rememberScrollState())
                ) {
                    Text("Select ONE dish to feature in the main hero banner on Home Screen:", fontSize = 13.sp, color = Color.Gray)
                    Spacer(modifier = Modifier.height(10.dp))

                    RestaurantRepository.foodItems.forEach { foodItem ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    RestaurantRepository.setTodaysSpecialItem(foodItem)
                                    showSpecialDialog = false
                                    Toast.makeText(context, "Set '${foodItem.name}' as Today's Special! 🔥", Toast.LENGTH_SHORT).show()
                                }
                                .padding(vertical = 6.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            RadioButton(
                                selected = foodItem.isTodaysSpecial,
                                onClick = {
                                    RestaurantRepository.setTodaysSpecialItem(foodItem)
                                    showSpecialDialog = false
                                    Toast.makeText(context, "Set '${foodItem.name}' as Today's Special! 🔥", Toast.LENGTH_SHORT).show()
                                },
                                colors = RadioButtonDefaults.colors(selectedColor = Color(0xFFE65100))
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Column {
                                Text(foodItem.name, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                                Text("₹${foodItem.price.toInt()} • ${foodItem.description}", fontSize = 12.sp, color = Color.Gray)
                            }
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(
                    onClick = { showSpecialDialog = false }
                ) {
                    Text("Cancel", fontWeight = FontWeight.Bold)
                }
            }
        )
    }
}
