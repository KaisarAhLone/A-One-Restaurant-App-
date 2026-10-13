package com.example.aonerestaurant.ui.screen.menu

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Restaurant
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
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
import androidx.compose.ui.text.font.FontWeight
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
    var searchQuery by remember { mutableStateOf("") }

    val totalCount = cartMap.values.sum()
    val totalPrice = MenuRepository.items.sumOf { item ->
        (cartMap[item.id] ?: 0) * item.price
    }

    val allCategories = remember {
        listOf(FoodCategory("all", "All Dishes", Icons.Default.Restaurant)) + RestaurantRepository.categories
    }

    val filteredItems = remember(selectedCategoryId, searchQuery, MenuRepository.items.size) {
        val byCat = if (selectedCategoryId == "all") {
            MenuRepository.items
        } else {
            MenuRepository.items.filter { it.categoryId == selectedCategoryId }
        }

        if (searchQuery.isBlank()) {
            byCat
        } else {
            byCat.filter {
                it.name.contains(searchQuery, ignoreCase = true) ||
                it.description.contains(searchQuery, ignoreCase = true)
            }
        }
    }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text = "A One Biryani Menu",
                            fontWeight = FontWeight.Bold,
                            fontSize = 18.sp,
                            color = Color.White
                        )
                        Text(
                            text = "Tasty • Fresh • Hygienic",
                            fontSize = 11.sp,
                            color = Color(0xFFFFD54F)
                        )
                    }
                },
                navigationIcon = {
                    if (onBackClick != null) {
                        IconButton(onClick = onBackClick) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = "Back",
                                tint = Color.White
                            )
                        }
                    }
                },
                actions = {
                    IconButton(onClick = onGoToCart) {
                        BadgedBox(
                            badge = {
                                if (totalCount > 0) {
                                    Badge(
                                        containerColor = Color(0xFFF9B828),
                                        contentColor = Color(0xFF1E0204)
                                    ) {
                                        Text(text = totalCount.toString(), fontWeight = FontWeight.Bold)
                                    }
                                }
                            }
                        ) {
                            Icon(
                                imageVector = Icons.Default.ShoppingCart,
                                contentDescription = "Cart",
                                tint = Color.White
                            )
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color(0xFF6A040F)
                )
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
                .background(Color(0xFFF8F9FA))
        ) {
            Column(
                modifier = Modifier.fillMaxSize()
            ) {
                // Search Input inside Menu
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp)
                ) {
                    OutlinedTextField(
                        value = searchQuery,
                        onValueChange = { searchQuery = it },
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(Color.White, RoundedCornerShape(22.dp)),
                        placeholder = {
                            Text(text = "Search chicken biryani, curries...", fontSize = 13.5.sp, color = Color.Gray)
                        },
                        leadingIcon = {
                            Icon(
                                imageVector = Icons.Default.Search,
                                contentDescription = null,
                                tint = Color(0xFF6A040F),
                                modifier = Modifier.size(20.dp)
                            )
                        },
                        singleLine = true,
                        shape = RoundedCornerShape(22.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = Color(0xFF6A040F),
                            unfocusedBorderColor = Color(0xFFE0E0E0),
                            focusedContainerColor = Color.White,
                            unfocusedContainerColor = Color.White
                        )
                    )
                }

                // Category Filter Pills
                LazyRow(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 8.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    contentPadding = PaddingValues(horizontal = 16.dp)
                ) {
                    items(allCategories) { category ->
                        val isSelected = selectedCategoryId == category.id
                        val count = if (category.id == "all") {
                            MenuRepository.items.size
                        } else {
                            MenuRepository.items.count { it.categoryId == category.id }
                        }

                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(20.dp))
                                .background(if (isSelected) Color(0xFFD90429) else Color.White)
                                .clickable { selectedCategoryId = category.id }
                                .padding(horizontal = 14.dp, vertical = 8.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = category.name,
                                    fontSize = 12.5.sp,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                    color = if (isSelected) Color.White else Color(0xFF1D1E2C)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Box(
                                    modifier = Modifier
                                        .clip(CircleShape)
                                        .background(if (isSelected) Color.White.copy(alpha = 0.25f) else Color(0xFFEEEEEE))
                                        .padding(horizontal = 6.dp, vertical = 1.dp)
                                ) {
                                    Text(
                                        text = count.toString(),
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = if (isSelected) Color.White else Color(0xFF555555)
                                    )
                                }
                            }
                        }
                    }
                }

                // Food Items List
                LazyColumn(
                    modifier = Modifier.weight(1f),
                    contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 4.dp, bottom = if (totalCount > 0) 80.dp else 16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(filteredItems) { item ->
                        val qty = cartMap[item.id] ?: 0
                        MenuItemRow(
                            foodItem = item,
                            quantity = qty,
                            onAdd = { onAddItem(item) },
                            onRemove = { onRemoveItem(item) }
                        )
                    }
                }
            }

            // Floating Cart Bar over menu
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
