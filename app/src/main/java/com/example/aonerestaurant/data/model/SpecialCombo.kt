package com.example.aonerestaurant.data.model

data class SpecialCombo(
    val id: String,
    val name: String,
    val description: String,
    val price: Int,
    val imageUri: String? = null
)
