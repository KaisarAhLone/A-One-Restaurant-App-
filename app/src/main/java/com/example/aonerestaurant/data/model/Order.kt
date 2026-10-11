package com.example.aonerestaurant.data.model

data class Order(
    val id: String = "",
    val itemsSummary: String = "",
    val totalAmount: Double = 0.0,
    val deliveryAddress: String = "",
    val timestamp: Long = System.currentTimeMillis(),
    val status: String = "Preparing 🍳"
)
