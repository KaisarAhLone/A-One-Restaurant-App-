package com.example.aonerestaurant.data.repository

import com.example.aonerestaurant.data.model.FoodItem

object MenuRepository {
    val items = listOf(
        FoodItem(
            id = "1",
            name = "Cheese Burger",
            price = 5.99,
            description = "Juicy beef patty with fresh cheese and lettuce"
        ),
        FoodItem(
            id = "2",
            name = "Veg Pizza",
            price = 8.49,
            description = "Crispy crust topped with fresh vegetables and cheese"
        ),
        FoodItem(
            id = "3",
            name = "Chicken Biryani",
            price = 9.99,
            description = "Flavorful basmati rice with tender spiced chicken"
        ),
        FoodItem(
            id = "4",
            name = "French Fries",
            price = 2.99,
            description = "Crispy golden potato fries seasoned with salt"
        ),
        FoodItem(
            id = "5",
            name = "Cold Drink",
            price = 1.49,
            description = "Chilled refreshing carbonated beverage"
        ),
        FoodItem(
            id = "6",
            name = "Paneer Sandwich",
            price = 4.99,
            description = "Grilled bread stuffed with spiced paneer and veggies"
        ),
        FoodItem(
            id = "7",
            name = "Chocolate Ice Cream",
            price = 3.49,
            description = "Rich creamy scoop of Belgian chocolate ice cream"
        )
    )
}
