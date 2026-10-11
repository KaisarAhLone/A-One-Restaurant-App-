package com.example.aonerestaurant.ui.navigation

sealed class Screen {
    data object Login : Screen()
    data object SignUp : Screen()
    data object Home : Screen()
    data class Menu(val categoryId: String? = null) : Screen()
    data object Orders : Screen()
    data object Profile : Screen()
    data object Cart : Screen()
    data object OrderSuccess : Screen()
    data object OwnerDashboard : Screen()
}
