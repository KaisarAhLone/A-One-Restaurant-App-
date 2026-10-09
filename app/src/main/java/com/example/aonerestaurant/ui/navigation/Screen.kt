package com.example.aonerestaurant.ui.navigation

sealed class Screen {
    data object Login : Screen()
    data object SignUp : Screen()
    data object Home : Screen()
    data object Menu : Screen()
    data object Cart : Screen()
    data object OrderSuccess : Screen()
    data object OwnerDashboard : Screen()
}
