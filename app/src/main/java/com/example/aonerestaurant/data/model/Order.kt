package com.example.aonerestaurant.data.model

data class Order(
    val id: String = "",
    val userId: String = "",
    val customerEmail: String = "",
    val customerName: String = "",
    val customerPhone: String = "",
    val pgHostel: String = "",
    val deliveryAddress: String = "",
    val itemsSummary: String = "",
    val totalAmount: Double = 0.0,
    val timestamp: Long = System.currentTimeMillis(),
    val status: String = "Preparing 🍳"
)
