package com.example.aonerestaurant.data.model

data class SpecialCombo(
    val id: String = "",
    val name: String = "",
    val description: String = "",
    val price: Int = 0,
    val originalPrice: Int = 0,
    val imageUri: String? = null
)
