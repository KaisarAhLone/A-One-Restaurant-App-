package com.example.aonerestaurant.ui.screen.menu

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.filled.Restaurant
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.aonerestaurant.data.model.FoodCategory
import com.example.aonerestaurant.data.model.FoodItem
import com.example.aonerestaurant.data.repository.MenuRepository
import com.example.aonerestaurant.data.repository.RestaurantRepository
import com.example.aonerestaurant.ui.components.FloatingCartBar
import com.example.aonerestaurant.ui.screen.home.HomeBottomNavBar
import com.example.aonerestaurant.ui.screen.home.MainTab

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MenuScreen(
    cartMap: Map<String, Int>,
    onAddItem: (FoodItem) -> Unit,
    onRemoveItem: (FoodItem) -> Unit,
    onGoToCart: () -> Unit,
    onSignOut: () -> Unit,
    modifier: Modifier = Modifier,
    initialCategoryId: String? = null,
    onBackClick: (() -> Unit)? = null,
    onNavigateToTab: ((MainTab) -> Unit)? = null
) {
    var selectedCategoryId by remember(initialCategoryId) { mutableStateOf(initialCategoryId ?: "all") }

    val totalCount = cartMap.values.sum()
    val totalPrice = MenuRepository.items.sumOf { item ->
        (cartMap[item.id] ?: 0) * item.price
    }

    val allCategories = remember {
        listOf(FoodCategory("all", "All", Icons.Default.Restaurant)) + RestaurantRepository.categories
    }

    val displayItems = remember(selectedCategoryId, MenuRepository.items.size) {
        if (selectedCategoryId == "all") {
            MenuRepository.items
        } else {
            MenuRepository.items.filter { it.categoryId == selectedCategoryId }
        }
    }

    val selectedCategoryName = remember(selectedCategoryId) {
        if (selectedCategoryId == "all") "All" else RestaurantRepository.categories.find { it.id == selectedCategoryId }?.name ?: "Menu"
    }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            TopAppBar(
                title = { Text(text = "$selectedCategoryName Menu", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    if (onBackClick != null) {
                        IconButton(onClick = onBackClick) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = "Back"
                            )
                        }
                    }
                },
                actions = {
                    IconButton(onClick = onSignOut) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ExitToApp,
                            contentDescription = "Sign Out"
                        )
                    }
                }
            )
        },
        bottomBar = {
            if (onNavigateToTab != null) {
                HomeBottomNavBar(
                    currentTab = MainTab.MENU,
                    onTabSelected = onNavigateToTab
                )
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            Column(
                modifier = Modifier.fillMaxSize()
            ) {
                // Category Filter Chips
                LazyRow(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    contentPadding = PaddingValues(horizontal = 16.dp)
                ) {
                    items(allCategories) { category ->
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
                                selectedContainerColor = MaterialTheme.colorScheme.primary,
                                selectedLabelColor = MaterialTheme.colorScheme.onPrimary,
                                selectedLeadingIconColor = MaterialTheme.colorScheme.onPrimary
                            )
                        )
                    }
                }

                if (displayItems.isEmpty()) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(24.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)),
                            shape = RoundedCornerShape(16.dp)
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(24.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Text(
                                    text = "No $selectedCategoryName Items Found 🍽️",
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.primary
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(
                                    text = "No items added in this category yet. The owner can add new items from the Owner Dashboard!",
                                    fontSize = 13.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    textAlign = TextAlign.Center
                                )
                            }
                        }
                    }
                } else {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        items(displayItems) { item ->
                            val quantity = cartMap[item.id] ?: 0
                            MenuItemRow(
                                foodItem = item,
                                quantity = quantity,
                                onAdd = { onAddItem(item) },
                                onRemove = { onRemoveItem(item) }
                            )
                        }
                    }
                }
            }

            // Floating Cart popup over screen content (NO black background)
            FloatingCartBar(
                totalCount = totalCount,
                totalPrice = totalPrice,
                onViewCartClick = onGoToCart,
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(bottom = 8.dp)
            )
        }
    }
}
