package com.example.aonerestaurant.data.model

data class FoodItem(
    val id: String,
    val name: String,
    val price: Double,
    val description: String,
    val categoryId: String = "1",
    val rating: Double = 4.5,
    val preparationTime: String = "15-20 min",
    val isPopular: Boolean = false,
    val isTodaysSpecial: Boolean = false,
    val isCombo: Boolean = false,
    val imageUri: String? = null
)
