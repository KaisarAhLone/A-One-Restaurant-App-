package com.example.aonerestaurant.data.repository

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KebabDining
import androidx.compose.material.icons.filled.LocalPizza
import androidx.compose.material.icons.filled.RamenDining
import androidx.compose.material.icons.filled.SetMeal
import androidx.compose.runtime.mutableStateListOf
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

    val foodItems = mutableStateListOf(
        FoodItem(
            id = "f1",
            name = "Chicken Biryani",
            price = 180.0,
            description = "Aromatic basmati rice cooked with juicy marinated chicken and spices",
            categoryId = "1",
            rating = 4.8,
            preparationTime = "15-20 min",
            isPopular = true,
            isTodaysSpecial = true
        ),
        FoodItem(
            id = "f2",
            name = "Mutton Dum Biryani",
            price = 250.0,
            description = "Slow cooked mutton tender pieces with herbs & fragrant basmati rice",
            categoryId = "1",
            rating = 4.9,
            preparationTime = "20-25 min",
            isPopular = true
        ),
        FoodItem(
            id = "f3",
            name = "Tandoori Chicken",
            price = 220.0,
            description = "Smoky grilled marinated chicken with roasted spices",
            categoryId = "2",
            rating = 4.7,
            preparationTime = "15-20 min",
            isPopular = true,
            isTodaysSpecial = true
        ),
        FoodItem(
            id = "f4",
            name = "Malai Chicken Tikka",
            price = 240.0,
            description = "Mouth-melting cream and cashew marinated grilled chicken tikka",
            categoryId = "2",
            rating = 4.6,
            preparationTime = "15 min"
        ),
        FoodItem(
            id = "f5",
            name = "Crispy Fried Chicken",
            price = 200.0,
            description = "Golden crunchy fried chicken with spicy seasoning",
            categoryId = "3",
            rating = 4.7,
            preparationTime = "12-15 min",
            isPopular = true
        ),
        FoodItem(
            id = "f6",
            name = "Crispy Chicken Wings",
            price = 180.0,
            description = "Deep fried crunchy wings served with garlic dip",
            categoryId = "3",
            rating = 4.5,
            preparationTime = "10-12 min"
        ),
        FoodItem(
            id = "f7",
            name = "Butter Chicken Curry",
            price = 230.0,
            description = "Rich creamy tomato gravy with juicy grilled chicken pieces",
            categoryId = "4",
            rating = 4.9,
            preparationTime = "15-20 min",
            isPopular = true,
            isTodaysSpecial = true
        ),
        FoodItem(
            id = "f8",
            name = "Kadai Chicken Curry",
            price = 210.0,
            description = "Spicy gravy with bell peppers, onions and fresh ground spices",
            categoryId = "4",
            rating = 4.6,
            preparationTime = "15-20 min"
        )
    )

    val popularItems: List<FoodItem>
        get() = foodItems.filter { it.isPopular }

    val todaysSpecials: List<FoodItem>
        get() = foodItems.filter { it.isTodaysSpecial }

    val specialCombos = mutableStateListOf(
        SpecialCombo(
            id = "c1",
            name = "Special Biryani Combo",
            description = "Chicken Biryani + 2 Pcs Tandoori Chicken",
            price = 300
        ),
        SpecialCombo(
            id = "c2",
            name = "Curry & Tandoori Feast",
            description = "Butter Chicken Curry + Tandoori Naan + Fried Wings",
            price = 420
        )
    )

    fun addFoodItem(item: FoodItem) {
        foodItems.add(0, item)
    }

    fun addSpecialCombo(combo: SpecialCombo) {
        specialCombos.add(0, combo)
    }

    fun removeFoodItem(item: FoodItem) {
        foodItems.remove(item)
    }

    fun removeSpecialCombo(combo: SpecialCombo) {
        specialCombos.remove(combo)
    }
}
