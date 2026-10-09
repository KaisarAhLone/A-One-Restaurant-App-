package com.example.aonerestaurant.data.repository

import androidx.compose.runtime.mutableStateListOf
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KebabDining
import androidx.compose.material.icons.filled.LocalPizza
import androidx.compose.material.icons.filled.RamenDining
import androidx.compose.material.icons.filled.SetMeal
import com.example.aonerestaurant.data.model.FoodCategory
import com.example.aonerestaurant.data.model.FoodItem
import com.example.aonerestaurant.data.model.SpecialCombo

object RestaurantRepository {
    val categories = listOf(
        FoodCategory("1", "Biryani", Icons.Default.RamenDining),
        FoodCategory("2", "Grilled", Icons.Default.KebabDining),
        FoodCategory("3", "Fried", Icons.Default.SetMeal),
        FoodCategory("4", "Chicken Curry", Icons.Default.LocalPizza)
    )

    val popularItems = mutableStateListOf(
        FoodItem(
            id = "p1",
            name = "Chicken Biryani",
            price = 180.0,
            description = "Rich in flavors, unforgettable taste",
            isPopular = true
        ),
        FoodItem(
            id = "p2",
            name = "Tandoori Chicken",
            price = 220.0,
            description = "Smoky grilled marinated chicken"
        ),
        FoodItem(
            id = "p3",
            name = "Fried Chicken",
            price = 200.0,
            description = "Crispy golden fried chicken wings"
        )
    )

    val specialCombos = mutableStateListOf(
        SpecialCombo(
            id = "c1",
            name = "Biryani Combo",
            description = "Chicken Biryani + 2 Pcs Tandoori",
            price = 300
        )
    )

    fun addFoodItem(item: FoodItem) {
        popularItems.add(0, item)
    }

    fun addSpecialCombo(combo: SpecialCombo) {
        specialCombos.add(0, combo)
    }
}
