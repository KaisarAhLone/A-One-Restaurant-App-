package com.example.aonerestaurant.ui.screen.home

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.aonerestaurant.data.model.FoodItem
import com.example.aonerestaurant.data.model.SpecialCombo
import com.example.aonerestaurant.data.repository.RestaurantRepository
import com.example.aonerestaurant.ui.screen.menu.MenuItemRow
import kotlinx.coroutines.launch

@Composable
fun HomeScreen(
    cartCount: Int,
    onAddToCart: (FoodItem) -> Unit,
    onAddComboToCart: (SpecialCombo) -> Unit,
    onGoToCart: () -> Unit,
    onNavigateToTab: (MainTab) -> Unit,
    modifier: Modifier = Modifier,
    cartMap: Map<String, Int> = emptyMap(),
    onRemoveFromCart: (FoodItem) -> Unit = {},
    onCategoryClick: (String) -> Unit = {}
) {
    var searchQuery by remember { mutableStateOf("") }
    var selectedCategoryId by remember { mutableStateOf("1") }
    var activeTab by remember { mutableStateOf(MainTab.HOME) }

    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    val allFoodItems = RestaurantRepository.foodItems
    val filteredItems = remember(selectedCategoryId, searchQuery, allFoodItems.size, allFoodItems.toList()) {
        allFoodItems.filter { item ->
            val matchesCategory = item.categoryId == selectedCategoryId
            val matchesSearch = searchQuery.isBlank() || item.name.contains(searchQuery, ignoreCase = true) || item.description.contains(searchQuery, ignoreCase = true)
            matchesCategory && matchesSearch
        }
    }

    val selectedCategoryName = remember(selectedCategoryId) {
        RestaurantRepository.categories.find { it.id == selectedCategoryId }?.name ?: "Dishes"
    }

    val todaysSpecialItem = RestaurantRepository.todaysSpecials.firstOrNull() ?: RestaurantRepository.foodItems.firstOrNull()

    Scaffold(
        modifier = modifier.fillMaxSize(),
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
        bottomBar = {
            HomeBottomNavBar(
                currentTab = activeTab,
                onTabSelected = { tab ->
                    activeTab = tab
                    onNavigateToTab(tab)
                }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(Color(0xFFF8F9FA))
                .verticalScroll(rememberScrollState())
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        Color(0xFF6A040F),
                        shape = RoundedCornerShape(bottomStart = 28.dp, bottomEnd = 28.dp)
                    )
            ) {
                Column {
                    HomeHeaderBar(
                        cartCount = cartCount,
                        onCartClick = onGoToCart,
                        onNotificationClick = {
                            scope.launch {
                                snackbarHostState.showSnackbar("No new notifications")
                            }
                        }
                    )

                    HomeSearchBar(
                        query = searchQuery,
                        onQueryChange = { searchQuery = it }
                    )

                    Spacer(modifier = Modifier.height(12.dp))
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // 1. Hero Banner showing dynamic "Today's Special Offer 👑"
            HeroBannerCard(
                specialItem = todaysSpecialItem,
                onOrderNowClick = { item ->
                    onAddToCart(item)
                    scope.launch {
                        snackbarHostState.showSnackbar("Added ${item.name} to cart!")
                    }
                }
            )

            Spacer(modifier = Modifier.height(16.dp))

            // 2. Category Selector (Biryani, Grilled, Fried, Chicken Curry) -> Click opens full Category Menu Page!
            CategoryRowSection(
                categories = RestaurantRepository.categories,
                selectedCategoryId = selectedCategoryId,
                onCategorySelected = { catId ->
                    selectedCategoryId = catId
                    onCategoryClick(catId)
                }
            )

            Spacer(modifier = Modifier.height(20.dp))

            if (allFoodItems.isEmpty() && RestaurantRepository.specialCombos.isEmpty()) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 24.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    shape = RoundedCornerShape(16.dp),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "Restaurant Menu is Empty 🍽️",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF6A040F)
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "No dishes added yet. The restaurant owner can add new items from the Owner Dashboard!",
                            fontSize = 13.sp,
                            color = Color.Gray,
                            textAlign = TextAlign.Center
                        )
                    }
                }
            } else {
                // 3. Category Specific Dishes Menu List Section
                if (filteredItems.isNotEmpty()) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "$selectedCategoryName Menu 📋",
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF1D1E2C)
                            )
                            Text(
                                text = "${filteredItems.size} items",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = Color.Gray
                            )
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        filteredItems.forEach { item ->
                            val qty = cartMap[item.id] ?: 0
                            MenuItemRow(
                                foodItem = item,
                                quantity = qty,
                                onAdd = {
                                    onAddToCart(item)
                                    scope.launch {
                                        snackbarHostState.showSnackbar("Added ${item.name} to cart!")
                                    }
                                },
                                onRemove = {
                                    onRemoveFromCart(item)
                                }
                            )
                            Spacer(modifier = Modifier.height(10.dp))
                        }
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                }

                // 4. Popular Items Section
                if (RestaurantRepository.popularItems.isNotEmpty()) {
                    PopularSection(
                        popularItems = RestaurantRepository.popularItems,
                        onAddToCart = { item ->
                            onAddToCart(item)
                            scope.launch {
                                snackbarHostState.showSnackbar("Added ${item.name} to cart!")
                            }
                        },
                        onSeeAllClick = {
                            onCategoryClick("all")
                        },
                        title = "Popular Items 👑"
                    )
                    Spacer(modifier = Modifier.height(20.dp))
                }

                // 5. Special Combos Section
                if (RestaurantRepository.specialCombos.isNotEmpty()) {
                    SpecialComboSection(
                        combos = RestaurantRepository.specialCombos,
                        onAddCombo = { combo ->
                            onAddComboToCart(combo)
                            scope.launch {
                                snackbarHostState.showSnackbar("Added ${combo.name} to cart!")
                            }
                        },
                        onSeeAllClick = {
                            onCategoryClick("all")
                        }
                    )
                    Spacer(modifier = Modifier.height(24.dp))
                }
            }
        }
    }
}
