package com.example.aonerestaurant.ui.navigation

import androidx.activity.compose.BackHandler
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import com.example.aonerestaurant.data.model.CartItem
import com.example.aonerestaurant.data.model.FoodItem
import com.example.aonerestaurant.data.repository.RestaurantRepository
import com.example.aonerestaurant.ui.screen.cart.CartScreen
import com.example.aonerestaurant.ui.screen.home.HomeScreen
import com.example.aonerestaurant.ui.screen.home.MainTab
import com.example.aonerestaurant.ui.screen.login.LoginScreen
import com.example.aonerestaurant.ui.screen.menu.MenuScreen
import com.example.aonerestaurant.ui.screen.order.OrderSuccessScreen
import com.example.aonerestaurant.ui.screen.orders.OrdersScreen
import com.example.aonerestaurant.ui.screen.owner.OwnerDashboardScreen
import com.example.aonerestaurant.ui.screen.profile.ProfileScreen
import com.example.aonerestaurant.ui.screen.signup.SignUpScreen

@Composable
fun AppNavigation(
    modifier: Modifier = Modifier
) {
    var currentScreen by remember { mutableStateOf<Screen>(Screen.Login) }
    val cartMap = remember { mutableStateMapOf<String, Int>() }

    val totalCartCount = cartMap.values.sum()

    // Smooth system back button navigation
    BackHandler(enabled = currentScreen != Screen.Home && currentScreen != Screen.Login) {
        currentScreen = when (currentScreen) {
            is Screen.SignUp -> Screen.Login
            is Screen.OwnerDashboard -> Screen.Login
            else -> Screen.Home
        }
    }

    val cartItemsList = remember(cartMap.toMap(), RestaurantRepository.foodItems.size, RestaurantRepository.specialCombos.size) {
        val comboAsFoodItems = RestaurantRepository.specialCombos.map { combo ->
            FoodItem(
                id = combo.id,
                name = combo.name,
                price = combo.price.toDouble(),
                description = combo.description,
                imageUri = combo.imageUri
            )
        }
        val allItems = RestaurantRepository.foodItems + comboAsFoodItems
        allItems.mapNotNull { item ->
            val count = cartMap[item.id] ?: 0
            if (count > 0) CartItem(item, count) else null
        }
    }

    when (val screen = currentScreen) {
        is Screen.Login -> {
            LoginScreen(
                onLoginSuccess = { currentScreen = Screen.Home },
                onNavigateToSignUp = { currentScreen = Screen.SignUp },
                onSkipLogin = { currentScreen = Screen.Home },
                onGoogleSignIn = { currentScreen = Screen.Home },
                onOwnerLoginSuccess = { currentScreen = Screen.OwnerDashboard },
                modifier = modifier
            )
        }

        is Screen.SignUp -> {
            SignUpScreen(
                onSignUpSuccess = { currentScreen = Screen.Home },
                onNavigateBackToLogin = { currentScreen = Screen.Login },
                modifier = modifier
            )
        }

        is Screen.Home -> {
            HomeScreen(
                cartCount = totalCartCount,
                cartMap = cartMap,
                onAddToCart = { item ->
                    val currentCount = cartMap[item.id] ?: 0
                    cartMap[item.id] = currentCount + 1
                },
                onRemoveFromCart = { item ->
                    val currentCount = cartMap[item.id] ?: 0
                    if (currentCount > 1) {
                        cartMap[item.id] = currentCount - 1
                    } else {
                        cartMap.remove(item.id)
                    }
                },
                onAddComboToCart = { combo ->
                    val currentCount = cartMap[combo.id] ?: 0
                    cartMap[combo.id] = currentCount + 1
                },
                onGoToCart = { currentScreen = Screen.Cart },
                onNavigateToTab = { tab ->
                    when (tab) {
                        MainTab.HOME -> {}
                        MainTab.MENU -> currentScreen = Screen.Menu()
                        MainTab.ORDERS -> currentScreen = Screen.Orders
                        MainTab.PROFILE -> currentScreen = Screen.Profile
                    }
                },
                onCategoryClick = { categoryId ->
                    currentScreen = Screen.Menu(categoryId = categoryId)
                },
                modifier = modifier
            )
        }

        is Screen.Menu -> {
            val categoryIdParam = screen.categoryId
            MenuScreen(
                cartMap = cartMap,
                initialCategoryId = categoryIdParam,
                onAddItem = { item ->
                    val currentCount = cartMap[item.id] ?: 0
                    cartMap[item.id] = currentCount + 1
                },
                onRemoveItem = { item ->
                    val currentCount = cartMap[item.id] ?: 0
                    if (currentCount > 1) {
                        cartMap[item.id] = currentCount - 1
                    } else {
                        cartMap.remove(item.id)
                    }
                },
                onGoToCart = { currentScreen = Screen.Cart },
                onSignOut = {
                    cartMap.clear()
                    currentScreen = Screen.Login
                },
                onBackClick = {
                    currentScreen = Screen.Home
                },
                onNavigateToTab = { tab ->
                    when (tab) {
                        MainTab.HOME -> currentScreen = Screen.Home
                        MainTab.MENU -> {}
                        MainTab.ORDERS -> currentScreen = Screen.Orders
                        MainTab.PROFILE -> currentScreen = Screen.Profile
                    }
                },
                modifier = modifier
            )
        }

        is Screen.Orders -> {
            OrdersScreen(
                onNavigateToTab = { tab ->
                    when (tab) {
                        MainTab.HOME -> currentScreen = Screen.Home
                        MainTab.MENU -> currentScreen = Screen.Menu()
                        MainTab.ORDERS -> {}
                        MainTab.PROFILE -> currentScreen = Screen.Profile
                    }
                },
                onOrderClick = {
                    currentScreen = Screen.OrderSuccess
                },
                onBackClick = { currentScreen = Screen.Home },
                modifier = modifier
            )
        }

        is Screen.Profile -> {
            ProfileScreen(
                onNavigateToTab = { tab ->
                    when (tab) {
                        MainTab.HOME -> currentScreen = Screen.Home
                        MainTab.MENU -> currentScreen = Screen.Menu()
                        MainTab.ORDERS -> currentScreen = Screen.Orders
                        MainTab.PROFILE -> {}
                    }
                },
                onNavigateToOrders = { currentScreen = Screen.Orders },
                onLogout = {
                    cartMap.clear()
                    currentScreen = Screen.Login
                },
                onBackClick = { currentScreen = Screen.Home },
                modifier = modifier
            )
        }

        is Screen.Cart -> {
            CartScreen(
                cartItems = cartItemsList,
                onAddItem = { item ->
                    val currentCount = cartMap[item.id] ?: 0
                    cartMap[item.id] = currentCount + 1
                },
                onRemoveItem = { item ->
                    val currentCount = cartMap[item.id] ?: 0
                    if (currentCount > 1) {
                        cartMap[item.id] = currentCount - 1
                    } else {
                        cartMap.remove(item.id)
                    }
                },
                onPlaceOrder = { currentScreen = Screen.OrderSuccess },
                onBack = { currentScreen = Screen.Home },
                modifier = modifier
            )
        }

        is Screen.OrderSuccess -> {
            OrderSuccessScreen(
                onBackToMenu = {
                    cartMap.clear()
                    currentScreen = Screen.Home
                },
                modifier = modifier
            )
        }

        is Screen.OwnerDashboard -> {
            OwnerDashboardScreen(
                onLogout = { currentScreen = Screen.Login },
                modifier = modifier
            )
        }
    }
}
