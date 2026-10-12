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
    var latestOrder: Order? = null

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
                                    customerName = doc.getString("customerName") ?: "",
                                    customerPhone = doc.getString("customerPhone") ?: "",
                                    pgHostel = doc.getString("pgHostel") ?: "",
                                    deliveryAddress = doc.getString("deliveryAddress") ?: "",
                                    itemsSummary = doc.getString("itemsSummary") ?: "",
                                    totalAmount = doc.getDouble("totalAmount") ?: 0.0,
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

                        // Keep latestOrder synced if present
                        if (latestOrder != null) {
                            latestOrder = orders.find { it.id == latestOrder?.id } ?: latestOrder
                        }
                    }
                }
        } catch (e: Exception) {
            Log.e(TAG, "Error initializing orders listener: ${e.message}")
        }
    }

    fun placeOrder(order: Order) {
        latestOrder = order
        if (!orders.any { it.id == order.id }) {
            orders.add(0, order)
        }
        try {
            val orderMap = hashMapOf(
                "id" to order.id,
                "customerName" to order.customerName,
                "customerPhone" to order.customerPhone,
                "pgHostel" to order.pgHostel,
                "deliveryAddress" to order.deliveryAddress,
                "itemsSummary" to order.itemsSummary,
                "totalAmount" to order.totalAmount,
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

    fun updateOrderStatus(orderId: String, newStatus: String) {
        val index = orders.indexOfFirst { it.id == orderId }
        if (index != -1) {
            val updated = orders[index].copy(status = newStatus)
            orders[index] = updated
            if (latestOrder?.id == orderId) {
                latestOrder = updated
            }
        }
        try {
            db.collection("orders").document(orderId).update("status", newStatus)
                .addOnSuccessListener { Log.d(TAG, "Order status updated to $newStatus in Firestore") }
                .addOnFailureListener { e -> Log.e(TAG, "Failed updating order status: ${e.message}") }
        } catch (e: Exception) {
            Log.e(TAG, "Error updating order status: ${e.message}")
        }
    }
}
