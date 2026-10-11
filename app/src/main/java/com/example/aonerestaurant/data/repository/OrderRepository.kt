package com.example.aonerestaurant.data.repository

import android.util.Log
import androidx.compose.runtime.mutableStateListOf
import com.example.aonerestaurant.data.model.Order
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query

object OrderRepository {
    private const val TAG = "OrderRepository"
    private val db by lazy { FirebaseFirestore.getInstance() }

    val orders = mutableStateListOf<Order>()

    init {
        setupOrdersListener()
    }

    private fun setupOrdersListener() {
        try {
            db.collection("orders")
                .orderBy("timestamp", Query.Direction.DESCENDING)
                .addSnapshotListener { snapshot, error ->
                    if (error != null) {
                        Log.e(TAG, "Error fetching orders: ${error.message}")
                        return@addSnapshotListener
                    }

                    if (snapshot != null) {
                        val fetchedOrders = snapshot.documents.mapNotNull { doc ->
                            try {
                                Order(
                                    id = doc.getString("id") ?: doc.id,
                                    itemsSummary = doc.getString("itemsSummary") ?: "",
                                    totalAmount = doc.getDouble("totalAmount") ?: 0.0,
                                    deliveryAddress = doc.getString("deliveryAddress") ?: "",
                                    timestamp = doc.getLong("timestamp") ?: System.currentTimeMillis(),
                                    status = doc.getString("status") ?: "Preparing 🍳"
                                )
                            } catch (e: Exception) {
                                Log.e(TAG, "Error parsing order ${doc.id}: ${e.message}")
                                null
                            }
                        }
                        orders.clear()
                        orders.addAll(fetchedOrders)
                    }
                }
        } catch (e: Exception) {
            Log.e(TAG, "Error initializing orders listener: ${e.message}")
        }
    }

    fun placeOrder(order: Order) {
        if (!orders.any { it.id == order.id }) {
            orders.add(0, order)
        }
        try {
            val orderMap = hashMapOf(
                "id" to order.id,
                "itemsSummary" to order.itemsSummary,
                "totalAmount" to order.totalAmount,
                "deliveryAddress" to order.deliveryAddress,
                "timestamp" to order.timestamp,
                "status" to order.status
            )
            db.collection("orders").document(order.id).set(orderMap)
                .addOnSuccessListener { Log.d(TAG, "Order placed successfully in Firestore: ${order.id}") }
                .addOnFailureListener { e -> Log.e(TAG, "Failed placing order: ${e.message}") }
        } catch (e: Exception) {
            Log.e(TAG, "Error placing order: ${e.message}")
        }
    }
}
