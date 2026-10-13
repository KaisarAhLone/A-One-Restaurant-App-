package com.example.aonerestaurant.data.repository

import android.util.Log
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KebabDining
import androidx.compose.material.icons.filled.LocalPizza
import androidx.compose.material.icons.filled.RamenDining
import androidx.compose.material.icons.filled.SetMeal
import androidx.compose.runtime.mutableStateListOf
import com.example.aonerestaurant.R
import com.example.aonerestaurant.data.model.FoodCategory
import com.example.aonerestaurant.data.model.FoodItem
import com.example.aonerestaurant.data.model.SpecialCombo
import com.google.firebase.firestore.FirebaseFirestore

object RestaurantRepository {
    private const val TAG = "RestaurantRepository"
    private val db: FirebaseFirestore by lazy { FirebaseFirestore.getInstance() }

    val categories = listOf(
        FoodCategory("1", "Biryani", Icons.Default.RamenDining, R.drawable.cat_biryani),
        FoodCategory("2", "Grilled", Icons.Default.KebabDining, R.drawable.cat_grilled),
        FoodCategory("3", "Fried Chicken", Icons.Default.SetMeal, R.drawable.cat_fried),
        FoodCategory("4", "Chicken Curry", Icons.Default.LocalPizza, R.drawable.cat_curry)
    )

    private val defaultFoodItems = listOf(
        FoodItem(
            id = "1",
            name = "Chicken Biryani",
            price = 120.0,
            description = "Rich Flavour, Real Taste. Dum cooked fragrant basmati rice with spiced tender chicken leg.",
            categoryId = "1",
            rating = 4.9,
            preparationTime = "15-20 min",
            isPopular = true,
            isTodaysSpecial = true,
            imageUri = "res:dish_chicken_biryani"
        ),
        FoodItem(
            id = "2",
            name = "Fried Chicken (4 pcs)",
            price = 180.0,
            description = "Crispy golden fried chicken pieces, spiced to perfection with secret royal herbs.",
            categoryId = "3",
            rating = 4.8,
            preparationTime = "10-15 min",
            isPopular = true,
            isTodaysSpecial = false,
            imageUri = "res:dish_fried_chicken"
        ),
        FoodItem(
            id = "3",
            name = "Butter Chicken",
            price = 200.0,
            description = "Tender marinated chicken pieces simmered in rich creamy butter tomato gravy.",
            categoryId = "4",
            rating = 4.9,
            preparationTime = "15-20 min",
            isPopular = true,
            isTodaysSpecial = false,
            imageUri = "res:dish_butter_chicken"
        ),
        FoodItem(
            id = "4",
            name = "Grilled Chicken",
            price = 220.0,
            description = "Charcoal grilled juicy chicken marinated in authentic yogurt and spices.",
            categoryId = "2",
            rating = 4.7,
            preparationTime = "20-25 min",
            isPopular = true,
            isTodaysSpecial = false,
            imageUri = "res:cat_grilled"
        ),
        FoodItem(
            id = "5",
            name = "Chicken Curry",
            price = 170.0,
            description = "Homestyle aromatic chicken curry cooked slow in brass handi.",
            categoryId = "4",
            rating = 4.6,
            preparationTime = "15-20 min",
            isPopular = false,
            isTodaysSpecial = false,
            imageUri = "res:cat_curry"
        )
    )

    val foodItems = mutableStateListOf<FoodItem>().apply { addAll(defaultFoodItems) }
    val specialCombos = mutableStateListOf<SpecialCombo>()

    val popularItems: List<FoodItem>
        get() = foodItems.filter { it.isPopular }

    val todaysSpecials: List<FoodItem>
        get() = foodItems.filter { it.isTodaysSpecial }

    init {
        // Setup Firestore real-time listeners for foodItems and specialCombos
        setupFirestoreListeners()
    }

    private fun setupFirestoreListeners() {
        try {
            // Listen to foodItems collection in Firestore
            db.collection("foodItems").addSnapshotListener { snapshot, error ->
                if (error != null) {
                    Log.e(TAG, "Error listening to foodItems in Firestore: ${error.message}")
                    return@addSnapshotListener
                }

                if (snapshot != null) {
                    val fetchedItems = snapshot.documents.mapNotNull { doc ->
                        try {
                            FoodItem(
                                id = doc.getString("id") ?: doc.id,
                                name = doc.getString("name") ?: "",
                                price = doc.getDouble("price") ?: 0.0,
                                description = doc.getString("description") ?: "",
                                categoryId = doc.getString("categoryId") ?: "1",
                                rating = doc.getDouble("rating") ?: 4.5,
                                preparationTime = doc.getString("preparationTime") ?: "15-20 min",
                                isPopular = doc.getBoolean("isPopular") ?: false,
                                isTodaysSpecial = doc.getBoolean("isTodaysSpecial") ?: false,
                                isCombo = doc.getBoolean("isCombo") ?: false,
                                imageUri = doc.getString("imageUri")
                            )
                        } catch (e: Exception) {
                            Log.e(TAG, "Error parsing FoodItem doc ${doc.id}: ${e.message}")
                            null
                        }
                    }
                    foodItems.clear()
                    foodItems.addAll(fetchedItems)
                }
            }

            // Listen to specialCombos collection in Firestore
            db.collection("specialCombos").addSnapshotListener { snapshot, error ->
                if (error != null) {
                    Log.e(TAG, "Error listening to specialCombos in Firestore: ${error.message}")
                    return@addSnapshotListener
                }

                if (snapshot != null) {
                    val fetchedCombos = snapshot.documents.mapNotNull { doc ->
                        try {
                            SpecialCombo(
                                id = doc.getString("id") ?: doc.id,
                                name = doc.getString("name") ?: "",
                                description = doc.getString("description") ?: "",
                                price = doc.getLong("price")?.toInt() ?: 0,
                                originalPrice = doc.getLong("originalPrice")?.toInt() ?: 0,
                                imageUri = doc.getString("imageUri")
                            )
                        } catch (e: Exception) {
                            Log.e(TAG, "Error parsing SpecialCombo doc ${doc.id}: ${e.message}")
                            null
                        }
                    }
                    specialCombos.clear()
                    specialCombos.addAll(fetchedCombos)
                }
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error initializing Firestore listeners: ${e.message}")
        }
    }

    fun togglePopularStatus(foodItem: FoodItem, isPopular: Boolean) {
        val index = foodItems.indexOfFirst { it.id == foodItem.id }
        if (index != -1) {
            val updated = foodItems[index].copy(isPopular = isPopular)
            foodItems[index] = updated
            try {
                db.collection("foodItems").document(foodItem.id).update("isPopular", isPopular)
            } catch (e: Exception) {
                Log.e(TAG, "Error updating popular status: ${e.message}")
            }
        }
    }

    fun setTodaysSpecialItem(foodItem: FoodItem) {
        foodItems.forEachIndexed { idx, item ->
            val isSpecial = (item.id == foodItem.id)
            if (item.isTodaysSpecial != isSpecial) {
                foodItems[idx] = item.copy(isTodaysSpecial = isSpecial)
                try {
                    db.collection("foodItems").document(item.id).update("isTodaysSpecial", isSpecial)
                } catch (e: Exception) {
                    Log.e(TAG, "Error updating todays special status: ${e.message}")
                }
            }
        }
    }

    fun addFoodItem(item: FoodItem, onResult: ((Boolean, String) -> Unit)? = null) {
        if (!foodItems.any { it.id == item.id }) {
            foodItems.add(0, item)
        }
        addFoodItemToFirestore(item, onResult)
    }

    fun addSpecialCombo(combo: SpecialCombo, onResult: ((Boolean, String) -> Unit)? = null) {
        if (!specialCombos.any { it.id == combo.id }) {
            specialCombos.add(0, combo)
        }
        addSpecialComboToFirestore(combo, onResult)
    }

    fun removeFoodItem(item: FoodItem) {
        foodItems.remove(item)
        try {
            db.collection("foodItems").document(item.id).delete()
                .addOnSuccessListener { Log.d(TAG, "Deleted food item from Firestore: ${item.name}") }
                .addOnFailureListener { e -> Log.e(TAG, "Failed deleting food item: ${e.message}") }
        } catch (e: Exception) {
            Log.e(TAG, "Error deleting food item from Firestore: ${e.message}")
        }
    }

    fun removeSpecialCombo(combo: SpecialCombo) {
        specialCombos.remove(combo)
        try {
            db.collection("specialCombos").document(combo.id).delete()
                .addOnSuccessListener { Log.d(TAG, "Deleted combo from Firestore: ${combo.name}") }
                .addOnFailureListener { e -> Log.e(TAG, "Failed deleting combo: ${e.message}") }
        } catch (e: Exception) {
            Log.e(TAG, "Error deleting special combo from Firestore: ${e.message}")
        }
    }

    private fun addFoodItemToFirestore(item: FoodItem, onResult: ((Boolean, String) -> Unit)? = null) {
        try {
            // If new item is set as Today's Special, uncheck isTodaysSpecial on all other existing dishes
            if (item.isTodaysSpecial) {
                foodItems.forEach { existing ->
                    if (existing.id != item.id && existing.isTodaysSpecial) {
                        db.collection("foodItems").document(existing.id).update("isTodaysSpecial", false)
                    }
                }
            }

            val itemMap = hashMapOf(
                "id" to item.id,
                "name" to item.name,
                "price" to item.price,
                "description" to item.description,
                "categoryId" to item.categoryId,
                "rating" to item.rating,
                "preparationTime" to item.preparationTime,
                "isPopular" to item.isPopular,
                "isTodaysSpecial" to item.isTodaysSpecial,
                "isCombo" to item.isCombo,
                "imageUri" to (item.imageUri ?: "")
            )
            db.collection("foodItems").document(item.id).set(itemMap)
                .addOnSuccessListener {
                    Log.d(TAG, "Successfully added ${item.name} to Firestore!")
                    onResult?.invoke(true, "Successfully saved '${item.name}' to Firebase Firestore!")
                }
                .addOnFailureListener { e ->
                    Log.e(TAG, "Firestore write error for ${item.name}: ${e.message}")
                    onResult?.invoke(false, "Firestore Error: ${e.message}")
                }
        } catch (e: Exception) {
            Log.e(TAG, "Error adding food item to Firestore: ${e.message}")
            onResult?.invoke(false, "Exception: ${e.message}")
        }
    }

    private fun addSpecialComboToFirestore(combo: SpecialCombo, onResult: ((Boolean, String) -> Unit)? = null) {
        try {
            val comboMap = hashMapOf(
                "id" to combo.id,
                "name" to combo.name,
                "description" to combo.description,
                "price" to combo.price,
                "originalPrice" to combo.originalPrice,
                "imageUri" to (combo.imageUri ?: "")
            )
            db.collection("specialCombos").document(combo.id).set(comboMap)
                .addOnSuccessListener {
                    Log.d(TAG, "Successfully added combo ${combo.name} to Firestore!")
                    onResult?.invoke(true, "Successfully saved combo '${combo.name}' to Firebase Firestore!")
                }
                .addOnFailureListener { e ->
                    Log.e(TAG, "Firestore write error for combo ${combo.name}: ${e.message}")
                    onResult?.invoke(false, "Firestore Error: ${e.message}")
                }
        } catch (e: Exception) {
            Log.e(TAG, "Error adding special combo to Firestore: ${e.message}")
            onResult?.invoke(false, "Exception: ${e.message}")
        }
    }
}
