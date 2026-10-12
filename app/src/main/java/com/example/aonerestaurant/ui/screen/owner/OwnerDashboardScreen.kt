package com.example.aonerestaurant.ui.screen.owner

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.material.icons.filled.AddPhotoAlternate
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.LocalOffer
import androidx.compose.material.icons.filled.Restaurant
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Whatshot
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
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

    var itemName by remember { mutableStateOf("") }
    var itemPrice by remember { mutableStateOf("") }
    var itemDesc by remember { mutableStateOf("") }

    // Category Selection: default Biryani ("1")
    var selectedCategoryId by remember { mutableStateOf("1") }

    // Photo Selection (Base64 JPEG string for cross-phone Firebase sync)
    var selectedImageUri by remember { mutableStateOf<String?>(null) }

    var successMessage by remember { mutableStateOf("") }

    val photoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let {
            val base64Compressed = ImageUtils.uriToBase64Compressed(context, it)
            selectedImageUri = base64Compressed ?: it.toString()
        }
    }

    val selectedCategory = RestaurantRepository.categories.find { it.id == selectedCategoryId }
    val isFormValid = itemName.isNotBlank() && itemPrice.isNotBlank()

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

                    // 1. Category Selection
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

                    // 2. Photo Upload Option
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

                    // 3. Dedicated Direct Action Buttons
                    Text(
                        text = "3. Choose Where to Publish Item in App:",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF6A040F)
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    // Button 1: Add Category Dish
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

                    Spacer(modifier = Modifier.height(8.dp))

                    // Button 2: Add Popular Item
                    Button(
                        onClick = {
                            val priceVal = itemPrice.toDoubleOrNull() ?: 0.0
                            val newItem = FoodItem(
                                id = System.currentTimeMillis().toString(),
                                name = itemName,
                                price = priceVal,
                                description = itemDesc,
                                categoryId = selectedCategoryId,
                                isPopular = true,
                                imageUri = selectedImageUri
                            )
                            RestaurantRepository.addFoodItem(newItem)
                            successMessage = "Added '$itemName' to Popular Items section!"
                            itemName = ""; itemPrice = ""; itemDesc = ""; selectedImageUri = null
                        },
                        enabled = isFormValid,
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFD90429))
                    ) {
                        Icon(imageVector = Icons.Default.Star, contentDescription = null)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(text = "👑 Add to Popular Items Section", fontWeight = FontWeight.Bold)
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    // Button 3: Add Today's Special
                    Button(
                        onClick = {
                            val priceVal = itemPrice.toDoubleOrNull() ?: 0.0
                            val newItem = FoodItem(
                                id = System.currentTimeMillis().toString(),
                                name = itemName,
                                price = priceVal,
                                description = itemDesc,
                                categoryId = selectedCategoryId,
                                isTodaysSpecial = true,
                                imageUri = selectedImageUri
                            )
                            RestaurantRepository.addFoodItem(newItem)
                            successMessage = "Set '$itemName' as Today's Special Offer!"
                            itemName = ""; itemPrice = ""; itemDesc = ""; selectedImageUri = null
                        },
                        enabled = isFormValid,
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFE65100))
                    ) {
                        Icon(imageVector = Icons.Default.Whatshot, contentDescription = null)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(text = "🔥 Set as Today's Special Offer", fontWeight = FontWeight.Bold)
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    // Button 4: Add Special Combo
                    Button(
                        onClick = {
                            val priceVal = itemPrice.toIntOrNull() ?: (itemPrice.toDoubleOrNull()?.toInt() ?: 0)
                            val newCombo = SpecialCombo(
                                id = "combo_" + System.currentTimeMillis().toString(),
                                name = itemName + " Combo",
                                description = itemDesc.ifBlank { "$itemName Combo Offer" },
                                price = priceVal,
                                imageUri = selectedImageUri
                            )
                            RestaurantRepository.addSpecialCombo(newCombo)
                            successMessage = "Published '$itemName Combo' in Special Combos section!"
                            itemName = ""; itemPrice = ""; itemDesc = ""; selectedImageUri = null
                        },
                        enabled = isFormValid,
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2E7D32))
                    ) {
                        Icon(imageVector = Icons.Default.LocalOffer, contentDescription = null)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(text = "🍱 Add to Special Combos Section", fontWeight = FontWeight.Bold)
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    // Button 5: Add Everywhere (Category + Popular + Today's Special + Combo)
                    Button(
                        onClick = {
                            val priceVal = itemPrice.toDoubleOrNull() ?: 0.0
                            val newItem = FoodItem(
                                id = System.currentTimeMillis().toString(),
                                name = itemName,
                                price = priceVal,
                                description = itemDesc,
                                categoryId = selectedCategoryId,
                                isPopular = true,
                                isTodaysSpecial = true,
                                isCombo = true,
                                imageUri = selectedImageUri
                            )
                            RestaurantRepository.addFoodItem(newItem)

                            val comboItem = SpecialCombo(
                                id = "combo_" + System.currentTimeMillis().toString(),
                                name = itemName + " Special Combo",
                                description = itemDesc.ifBlank { "$itemName Combo Deal" },
                                price = priceVal.toInt(),
                                imageUri = selectedImageUri
                            )
                            RestaurantRepository.addSpecialCombo(comboItem)

                            successMessage = "Published '$itemName' across All Sections (Category + Popular + Special + Combos)!"
                            itemName = ""; itemPrice = ""; itemDesc = ""; selectedImageUri = null
                        },
                        enabled = isFormValid,
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF6A040F))
                    ) {
                        Text(text = "🚀 Publish Everywhere (All Sections)", fontWeight = FontWeight.Bold)
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
                                            text = "${combo.description} • ₹${combo.price}",
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
}
