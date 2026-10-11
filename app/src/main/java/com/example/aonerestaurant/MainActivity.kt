package com.example.aonerestaurant

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.example.aonerestaurant.ui.navigation.AppNavigation
import com.example.aonerestaurant.ui.theme.AOneRestaurantTheme
import com.google.firebase.FirebaseApp

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        FirebaseApp.initializeApp(this)
        enableEdgeToEdge()
        setContent {
            AOneRestaurantTheme {
                AppNavigation()
            }
        }
    }
}
