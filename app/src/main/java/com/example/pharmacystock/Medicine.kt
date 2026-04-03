package com.example.pharmacystock

data class Medicine(
    val id: String = "",
    val name: String = "",
    val type: String = "",
    val price: Double = 0.0,
    val quantity: Int = 0,
    val imageUrl: String = "",
    val expiryDate: Long = 0L
)