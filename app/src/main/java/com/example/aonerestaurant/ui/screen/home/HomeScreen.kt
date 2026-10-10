package com.example.aonerestaurant.ui.screen.home

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.aonerestaurant.data.model.FoodItem
import com.example.aonerestaurant.data.model.SpecialCombo
import com.example.aonerestaurant.data.repository.RestaurantRepository
import kotlinx.coroutines.launch

@Composable
fun HomeScreen(
    cartCount: Int,
    onAddToCart: (FoodItem) -> Unit,
    onAddComboToCart: (SpecialCombo) -> Unit,
    onGoToCart: () -> Unit,
    onNavigateToTab: (MainTab) -> Unit,
    modifier: Modifier = Modifier
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

            HeroBannerCard(
                onOrderNowClick = {
                    val firstItem = RestaurantRepository.foodItems.firstOrNull()
                    if (firstItem != null) {
                        onAddToCart(firstItem)
                        scope.launch {
                            snackbarHostState.showSnackbar("Added ${firstItem.name} to cart!")
                        }
                    }
                }
            )

            Spacer(modifier = Modifier.height(16.dp))

            CategoryRowSection(
                categories = RestaurantRepository.categories,
                selectedCategoryId = selectedCategoryId,
                onCategorySelected = { selectedCategoryId = it }
            )

            Spacer(modifier = Modifier.height(20.dp))

            if (filteredItems.isNotEmpty()) {
                PopularSection(
                    popularItems = filteredItems,
                    onAddToCart = { item ->
                        onAddToCart(item)
                        scope.launch {
                            snackbarHostState.showSnackbar("Added ${item.name} to cart!")
                        }
                    },
                    onSeeAllClick = {
                        scope.launch {
                            snackbarHostState.showSnackbar("Viewing all $selectedCategoryName items")
                        }
                    }
                )
                Spacer(modifier = Modifier.height(20.dp))
            }

            if (RestaurantRepository.popularItems.isNotEmpty() && selectedCategoryId != "1") {
                PopularSection(
                    popularItems = RestaurantRepository.popularItems,
                    onAddToCart = { item ->
                        onAddToCart(item)
                        scope.launch {
                            snackbarHostState.showSnackbar("Added ${item.name} to cart!")
                        }
                    },
                    onSeeAllClick = {
                        scope.launch {
                            snackbarHostState.showSnackbar("Viewing popular items")
                        }
                    }
                )
                Spacer(modifier = Modifier.height(20.dp))
            }

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
                        scope.launch {
                            snackbarHostState.showSnackbar("Viewing all special combos")
                        }
                    }
                )
                Spacer(modifier = Modifier.height(20.dp))
            }

            if (RestaurantRepository.todaysSpecials.isNotEmpty()) {
                Box(modifier = Modifier.padding(horizontal = 16.dp)) {
                    RecommendedSection(
                        foodItems = RestaurantRepository.todaysSpecials,
                        onAddToCart = { item ->
                            onAddToCart(item)
                            scope.launch {
                                snackbarHostState.showSnackbar("Added ${item.name} to cart!")
                            }
                        }
                    )
                }
                Spacer(modifier = Modifier.height(24.dp))
            }
        }
    }
}
