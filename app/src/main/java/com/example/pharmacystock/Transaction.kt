package com.example.pharmacystock

data class Transaction(
    val id: String,
    val medicineId: String,
    val type: String,
    val quantity: Int,
    val date: String,
    val supplier: String
)