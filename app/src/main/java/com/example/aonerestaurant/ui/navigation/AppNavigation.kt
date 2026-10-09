package com.example.aonerestaurant.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import com.example.aonerestaurant.data.model.CartItem
import com.example.aonerestaurant.data.model.FoodItem
import com.example.aonerestaurant.data.repository.MenuRepository
import com.example.aonerestaurant.ui.screen.cart.CartScreen
import com.example.aonerestaurant.ui.screen.home.HomeScreen
import com.example.aonerestaurant.ui.screen.home.MainTab
import com.example.aonerestaurant.ui.screen.login.LoginScreen
import com.example.aonerestaurant.ui.screen.menu.MenuScreen
import com.example.aonerestaurant.ui.screen.order.OrderSuccessScreen
import com.example.aonerestaurant.ui.screen.owner.OwnerDashboardScreen
import com.example.aonerestaurant.ui.screen.signup.SignUpScreen

@Composable
fun AppNavigation(
    modifier: Modifier = Modifier
) {
    var currentScreen by remember { mutableStateOf<Screen>(Screen.Login) }
    val cartMap = remember { mutableStateMapOf<String, Int>() }

    val totalCartCount = cartMap.values.sum()

    val cartItemsList = remember(cartMap.toMap()) {
        val allItems = MenuRepository.items + FoodItem(
            id = "c1",
            name = "Biryani Combo",
            price = 300.0,
            description = "Chicken Biryani + 2 Pcs Tandoori"
        )
        allItems.mapNotNull { item ->
            val count = cartMap[item.id] ?: 0
            if (count > 0) CartItem(item, count) else null
        }
    }

    when (currentScreen) {
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
                onAddToCart = { item ->
                    val currentCount = cartMap[item.id] ?: 0
                    cartMap[item.id] = currentCount + 1
                },
                onAddComboToCart = { combo ->
                    val currentCount = cartMap[combo.id] ?: 0
                    cartMap[combo.id] = currentCount + 1
                },
                onGoToCart = { currentScreen = Screen.Cart },
                onNavigateToTab = { tab ->
                    when (tab) {
                        MainTab.MENU -> currentScreen = Screen.Menu
                        MainTab.HOME -> {}
                        else -> {}
                    }
                },
                modifier = modifier
            )
        }

        is Screen.Menu -> {
            MenuScreen(
                cartMap = cartMap,
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
