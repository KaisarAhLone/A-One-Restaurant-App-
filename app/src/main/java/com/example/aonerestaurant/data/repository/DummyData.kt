package com.example.aonerestaurant.data.repository

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Fastfood
import androidx.compose.material.icons.filled.LocalDrink
import androidx.compose.material.icons.filled.LocalPizza
import androidx.compose.material.icons.filled.LunchDining
import androidx.compose.material.icons.filled.RamenDining
import androidx.compose.material.icons.filled.Restaurant
import com.example.aonerestaurant.data.model.FoodCategory
import com.example.aonerestaurant.data.model.FoodItem

object DummyData {
    val categories = listOf(
        FoodCategory("all", "All", Icons.Default.Restaurant),
        FoodCategory("burger", "Burger", Icons.Default.LunchDining),
        FoodCategory("pizza", "Pizza", Icons.Default.LocalPizza),
        FoodCategory("asian", "Asian", Icons.Default.RamenDining),
        FoodCategory("snacks", "Snacks", Icons.Default.Fastfood),
        FoodCategory("drinks", "Drinks", Icons.Default.LocalDrink)
    )

    val foodItems = listOf(
        FoodItem(
            id = "1",
            name = "Special Cheese Burger",
            categoryId = "burger",
            price = 8.99,
            rating = 4.8,
            preparationTime = "15-20 min",
            description = "Juicy beef patty with cheddar cheese, lettuce, tomato, and special sauce.",
            isPopular = true
        ),
        FoodItem(
            id = "2",
            name = "Pepperoni Supreme Pizza",
            categoryId = "pizza",
            price = 14.50,
            rating = 4.9,
            preparationTime = "20-25 min",
            description = "Loaded with double pepperoni, mozzarella cheese, and signature tomato sauce.",
            isPopular = true
        ),
        FoodItem(
            id = "3",
            name = "Chicken Biryani Bowl",
            categoryId = "asian",
            price = 11.99,
            rating = 4.7,
            preparationTime = "15-20 min",
            description = "Aromatic basmati rice cooked with tender marinated chicken and spices.",
            isPopular = true
        ),
        FoodItem(
            id = "4",
            name = "Crispy French Fries",
            categoryId = "snacks",
            price = 4.50,
            rating = 4.5,
            preparationTime = "10 min",
            description = "Golden crispy potato fries seasoned with sea salt and paprika.",
            isPopular = false
        ),
        FoodItem(
            id = "5",
            name = "Fresh Mango Smoothie",
            categoryId = "drinks",
            price = 5.25,
            rating = 4.6,
            preparationTime = "5 min",
            description = "Blend of fresh Alphonso mangoes, yogurt, and honey.",
            isPopular = true
        ),
        FoodItem(
            id = "6",
            name = "Veggie Delight Pizza",
            categoryId = "pizza",
            price = 12.00,
            rating = 4.4,
            preparationTime = "20 min",
            description = "Fresh bell peppers, onions, mushrooms, olives, and mozzarella.",
            isPopular = false
        )
    )
}
