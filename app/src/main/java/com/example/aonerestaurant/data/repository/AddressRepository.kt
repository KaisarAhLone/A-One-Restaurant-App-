package com.example.aonerestaurant.data.repository

import androidx.compose.runtime.mutableStateListOf
import com.example.aonerestaurant.data.model.Address

object AddressRepository {
    val savedAddresses = mutableStateListOf<Address>()

    fun saveAddress(address: Address) {
        val existingIndex = savedAddresses.indexOfFirst { it.id == address.id }
        if (existingIndex != -1) {
            savedAddresses[existingIndex] = address
        } else {
            savedAddresses.add(0, address)
        }
    }

    fun removeAddress(address: Address) {
        savedAddresses.removeAll { it.id == address.id }
    }
}
