package com.example.aonerestaurant.data.repository

import com.example.aonerestaurant.data.model.FoodItem

object MenuRepository {
    val items: List<FoodItem>
        get() = RestaurantRepository.popularItems
}
