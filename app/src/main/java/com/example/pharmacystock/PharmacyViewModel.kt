package com.example.pharmacystock

import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.ViewModel
import java.util.UUID
import com.google.firebase.storage.FirebaseStorage
import android.net.Uri
import com.google.firebase.firestore.FirebaseFirestore

class PharmacyViewModel : ViewModel() {

    // STATE
    private val db = FirebaseFirestore.getInstance()

    var medicines = mutableStateListOf<Medicine>()
        private set

    var transactions = mutableStateListOf<Transaction>()
        private set

    // ADD MEDICINE

    fun addMedicine(
        name: String,
        type: String,
        price: Double,
        quantity: Int,
        imageUrl: String,
        expiryDate: Long
    ) {
        val tempId = UUID.randomUUID().toString()

        val newMedicine = Medicine(
            id = tempId,
            name = name,
            type = type,
            price = price,
            quantity = quantity,
            imageUrl = imageUrl,
            expiryDate = expiryDate
        )

        // ADD TO UI IMMEDIATELY
        medicines.add(newMedicine)

        val medicineMap = hashMapOf(
            "name" to name,
            "type" to type,
            "price" to price,
            "quantity" to quantity,
            "imageUrl" to imageUrl,
            "expiryDate" to expiryDate
        )

        db.collection("medicines")
            .add(medicineMap)
            .addOnSuccessListener {
                println("Saved to Firebase")
            }
            .addOnFailureListener {
                println("Error: ${it.message}")
            }
    }

    fun updateMedicine(
        id: String,
        name: String,
        type: String,
        price: Double,
        quantity: Int,
        imageUrl: String
    ) {
        val updated = mapOf(
            "name" to name,
            "type" to type,
            "price" to price,
            "quantity" to quantity,
            "imageUrl" to imageUrl
        )

        db.collection("medicines")
            .document(id)
            .update(updated)
    }

    fun loadMedicines() {
        db.collection("medicines")
            .addSnapshotListener { snapshot, _ ->

                if (snapshot != null) {
                    medicines.clear()

                    for (doc in snapshot.documents) {
                        val medicine = Medicine(
                            id = doc.id,
                            name = doc.getString("name") ?: "",
                            type = doc.getString("type") ?: "",
                            price = doc.getDouble("price") ?: 0.0,
                            quantity = doc.getLong("quantity")?.toInt() ?: 0,
                            imageUrl = doc.getString("imageUrl") ?: "",
                            expiryDate = doc.getLong("expiryDate") ?: 0L
                        )

                        medicines.add(medicine)
                    }
                }
            }
    }

    fun loadTransactions() {
        db.collection("transactions")
            .addSnapshotListener { snapshot, _ ->

                if (snapshot != null) {
                    transactions.clear()

                    for (doc in snapshot.documents) {
                        val transaction = Transaction(
                            id = doc.id,
                            medicineId = doc.getString("medicineId") ?: "",
                            type = doc.getString("type") ?: "",
                            quantity = doc.getLong("quantity")?.toInt() ?: 0,
                            date = doc.getString("date") ?: "",
                            supplier = doc.getString("supplier") ?: ""
                        )

                        transactions.add(transaction)
                    }
                }
            }
    }

    fun deleteMedicine(id: String) {
        db.collection("medicines")
            .document(id)
            .delete()
    }

    fun uploadImage(uri: Uri, onSuccess: (String) -> Unit) {

        val storageRef = FirebaseStorage.getInstance()
            .reference
            .child("medicine_images/${UUID.randomUUID()}")

        storageRef.putFile(uri)
            .addOnSuccessListener {

                storageRef.downloadUrl
                    .addOnSuccessListener { downloadUrl ->
                        onSuccess(downloadUrl.toString())
                    }
            }
            .addOnFailureListener {
                println("UPLOAD ERROR: ${it.message}")
            }
    }

    // STOCK IN

    fun stockIn(
        medicineId: String,
        quantity: Int,
        supplier: String,
        date: String
    ) {
        val index = medicines.indexOfFirst { it.id == medicineId }

        val transaction = hashMapOf(
            "medicineId" to medicineId,
            "type" to "IN",
            "quantity" to quantity,
            "date" to date,
            "supplier" to supplier
        )

        if (index != -1) {
            val updatedMedicine = medicines[index].copy(
                quantity = medicines[index].quantity + quantity
            )

            db.collection("medicines")
                .document(medicineId)
                .update("quantity", updatedMedicine.quantity)

            db.collection("transactions").add(transaction)

            medicines[index] = updatedMedicine

            transactions.add(
                Transaction(
                    id = UUID.randomUUID().toString(),
                    medicineId = medicineId,
                    type = "IN",
                    quantity = quantity,
                    date = date,
                    supplier = supplier
                )
            )
        }
    }

    fun updateLastMedicineImage(imageUrl: String) {
        val last = medicines.lastOrNull() ?: return

        updateMedicine(
            id = last.id,
            name = last.name,
            type = last.type,
            price = last.price,
            quantity = last.quantity,
            imageUrl = imageUrl
        )
    }

    // STOCK OUT

    fun stockOut(
        medicineId: String,
        quantity: Int,
        date: String
    ) {
        val index = medicines.indexOfFirst { it.id == medicineId }

        val transaction = hashMapOf(
            "medicineId" to medicineId,
            "type" to "OUT",
            "quantity" to quantity,
            "date" to date,
            "supplier" to ""
        )

        if (index != -1) {

            val currentQty = medicines[index].quantity

            if (currentQty >= quantity) {

                val updatedMedicine = medicines[index].copy(
                    quantity = currentQty - quantity
                )

                db.collection("medicines")
                    .document(medicineId)
                    .update("quantity", updatedMedicine.quantity)

                db.collection("transactions").add(transaction)

                medicines[index] = updatedMedicine

                transactions.add(
                    Transaction(
                        id = UUID.randomUUID().toString(),
                        medicineId = medicineId,
                        type = "OUT",
                        quantity = quantity,
                        date = date,
                        supplier = ""
                    )
                )
            }
        }
    }
}

