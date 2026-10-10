package com.example.aonerestaurant.data.repository

import com.example.aonerestaurant.data.model.FoodCategory
import com.example.aonerestaurant.data.model.FoodItem

object DummyData {
    val categories: List<FoodCategory>
        get() = RestaurantRepository.categories

    val foodItems: List<FoodItem>
        get() = RestaurantRepository.foodItems
}
