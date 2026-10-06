package com.example.aonerestaurant

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.example.aonerestaurant.ui.navigation.AppNavigation
import com.example.aonerestaurant.ui.theme.AOneRestaurantTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            AOneRestaurantTheme {
                AppNavigation()
            }
        }
    }
}
