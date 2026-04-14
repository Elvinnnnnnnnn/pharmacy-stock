package com.example.pharmacystock

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.draw.shadow
import androidx.compose.runtime.*
import androidx.compose.material3.*
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.rememberScrollState
import android.net.Uri
import coil.compose.AsyncImage
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.draw.clip
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items

@Composable
fun MedicinesScreen(viewModel: PharmacyViewModel) {

    LaunchedEffect(Unit) {
        viewModel.loadMedicines()
    }

    val medicines = viewModel.medicines
    var showDialog by remember { mutableStateOf(false) }
    var searchQuery by remember { mutableStateOf("") }
    var name by remember { mutableStateOf("") }
    var type by remember { mutableStateOf("") }
    var price by remember { mutableStateOf("") }
    var quantity by remember { mutableStateOf("") }
    var expiryDate by remember { mutableStateOf(0L) }
    var imageUri by remember { mutableStateOf<Uri?>(null) }
    var selectedCategory by remember { mutableStateOf("All") }
    val filteredMedicines = medicines.filter {

        val matchesSearch = it.name.contains(searchQuery, ignoreCase = true)

        val matchesCategory =
            selectedCategory == "All" || it.type == selectedCategory

        matchesSearch && matchesCategory
    }
    val categories = listOf("All") + medicines
        .map { it.type }
        .distinct()
    var showEditDialog by remember { mutableStateOf(false) }
    var editingMedicine by remember { mutableStateOf<Medicine?>(null) }
    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri ->
        imageUri = uri
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .statusBarsPadding()
            .padding(16.dp)
    ) {

        // HEADER
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Medicines",
                fontSize = 22.sp,
                color = Color(0xFF2E3A8C)
            )

            IconButton(
                onClick = { showDialog = true },
                modifier = Modifier.size(48.dp)
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.add),
                    contentDescription = null,
                    modifier = Modifier.size(24.dp),
                    tint = Color.Unspecified
                )
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // SEARCH BAR
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    Color.LightGray.copy(alpha = 0.3f),
                    RoundedCornerShape(12.dp)
                )
                .padding(horizontal = 12.dp, vertical = 10.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {

                Icon(
                    painter = painterResource(id = R.drawable.search),
                    contentDescription = null,
                    modifier = Modifier.size(18.dp),
                    tint = Color.Gray
                )

                Spacer(modifier = Modifier.width(8.dp))

                BasicTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    decorationBox = { innerTextField ->
                        if (searchQuery.isEmpty()) {
                            Text(
                                text = "Search medicines...",
                                color = Color.Gray,
                                fontSize = 14.sp
                            )
                        }
                        innerTextField()
                    }
                )

                Spacer(modifier = Modifier.weight(1f))

                Icon(
                    painter = painterResource(id = R.drawable.filter),
                    contentDescription = null,
                    modifier = Modifier.size(18.dp),
                    tint = Color.Gray
                )
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // FILTER TABS
        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.horizontalScroll(rememberScrollState())
        ) {

            categories.forEach { category ->

                FilterChip(
                    text = category,
                    selected = selectedCategory == category,
                    onClick = { selectedCategory = category }
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // LIST
        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier.fillMaxSize()
        ) {

            items(filteredMedicines) { medicine ->

                MedicineItem(
                    medicine = medicine,
                    onDelete = {
                        viewModel.deleteMedicine(medicine.id)
                    },
                    onEdit = {
                        name = medicine.name
                        type = medicine.type
                        price = medicine.price.toString()
                        quantity = medicine.quantity.toString()
                        editingMedicine = medicine
                        showEditDialog = true
                    }
                )
            }
        }
    }
    if (showDialog) {

        AlertDialog(
            onDismissRequest = { showDialog = false },
            confirmButton = {
                Button(onClick = {

                    val priceValue = price.toDoubleOrNull()
                    val quantityValue = quantity.toIntOrNull()

                    if (
                        name.isNotEmpty() &&
                        type.isNotEmpty() &&
                        priceValue != null &&
                        quantityValue != null
                    ) {

                        val tempName = name
                        val tempType = type

                        if (imageUri != null) {
                            viewModel.uploadImage(imageUri!!) { url ->
                                viewModel.addMedicine(
                                    tempName,
                                    tempType,
                                    priceValue,
                                    quantityValue,
                                    url,
                                    expiryDate
                                )
                            }
                        } else {
                            viewModel.addMedicine(
                                tempName,
                                tempType,
                                priceValue,
                                quantityValue,
                                "",
                                expiryDate
                            )

                            if (imageUri != null) {
                                viewModel.uploadImage(imageUri!!) { url ->
                                    viewModel.updateLastMedicineImage(url)
                                }
                            }
                        }

                        name = ""
                        type = ""
                        price = ""
                        quantity = ""
                        expiryDate = 0L
                        imageUri = null

                        showDialog = false
                    }

                }) {
                    Text("Add")
                }
            },
            dismissButton = {
                TextButton(onClick = { showDialog = false }) {
                    Text("Cancel")
                }
            },
            title = { Text("Add Medicine") },
            text = {

                Column {

                    Button(
                        onClick = { launcher.launch("image/*") },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Select Image")
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    OutlinedTextField(
                        value = name,
                        onValueChange = { name = it },
                        label = { Text("Name") }
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    OutlinedTextField(
                        value = type,
                        onValueChange = { type = it },
                        label = { Text("Type") }
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    OutlinedTextField(
                        value = price,
                        onValueChange = { price = it },
                        label = { Text("Price") }
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    OutlinedTextField(
                        value = quantity,
                        onValueChange = { quantity = it },
                        label = { Text("Quantity") }
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    val context = LocalContext.current

                    Button(
                        onClick = {
                            val calendar = java.util.Calendar.getInstance()

                            android.app.DatePickerDialog(
                                context,
                                { _, y, m, d ->
                                    val cal = java.util.Calendar.getInstance()
                                    cal.set(y, m, d)
                                    expiryDate = cal.timeInMillis
                                },
                                calendar.get(java.util.Calendar.YEAR),
                                calendar.get(java.util.Calendar.MONTH),
                                calendar.get(java.util.Calendar.DAY_OF_MONTH)
                            ).show()
                        },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Select Expiry Date")
                    }
                }
            }
        )
    }
    if (showEditDialog && editingMedicine != null) {

        AlertDialog(
            onDismissRequest = { showEditDialog = false },
            confirmButton = {
                Button(onClick = {

                    val priceValue = price.toDoubleOrNull()
                    val quantityValue = quantity.toIntOrNull()

                    if (
                        name.isNotEmpty() &&
                        type.isNotEmpty() &&
                        priceValue != null &&
                        quantityValue != null
                    ) {

                        val tempName = name
                        val tempType = type

                        if (imageUri != null) {
                            viewModel.uploadImage(imageUri!!) { url ->
                                viewModel.updateMedicine(
                                    id = editingMedicine!!.id,
                                    name = tempName,
                                    type = tempType,
                                    price = priceValue,
                                    quantity = quantityValue,
                                    imageUrl = url
                                )
                            }
                        } else {
                            viewModel.updateMedicine(
                                id = editingMedicine!!.id,
                                name = tempName,
                                type = tempType,
                                price = priceValue,
                                quantity = quantityValue,
                                imageUrl = editingMedicine!!.imageUrl
                            )
                        }

                        name = ""
                        type = ""
                        price = ""
                        quantity = ""
                        imageUri = null

                        showEditDialog = false
                    }

                }) {
                    Text("Save")
                }
            },
            dismissButton = {
                TextButton(onClick = { showEditDialog = false }) {
                    Text("Cancel")
                }
            },
            title = { Text("Edit Medicine") },
            text = {

                Column {

                    OutlinedTextField(name, { name = it }, label = { Text("Name") })
                    Spacer(modifier = Modifier.height(8.dp))

                    OutlinedTextField(type, { type = it }, label = { Text("Type") })
                    Spacer(modifier = Modifier.height(8.dp))

                    OutlinedTextField(price, { price = it }, label = { Text("Price") })
                    Spacer(modifier = Modifier.height(8.dp))

                    OutlinedTextField(quantity, { quantity = it }, label = { Text("Quantity") })
                }
            }
        )
    }
}

@Composable
fun FilterChip(text: String, selected: Boolean, onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .background(
                if (selected) Color(0xFF2E3A8C) else Color.LightGray.copy(alpha = 0.4f),
                RoundedCornerShape(20.dp)
            )
            .padding(horizontal = 14.dp, vertical = 6.dp)
            .clickable { onClick() }
    ) {
        Text(
            text = text,
            color = if (selected) Color.White else Color.DarkGray,
            fontSize = 12.sp
        )
    }
}

@Composable
fun MedicineItem(
    medicine: Medicine,
    onDelete: () -> Unit,
    onEdit: () -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    Row(
        Modifier
            .fillMaxWidth()
            .padding(horizontal = 4.dp, vertical = 4.dp)
            .shadow(8.dp, RoundedCornerShape(16.dp))
            .background(Color.White, RoundedCornerShape(16.dp))
            .padding(12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {

        AsyncImage(
            model = medicine.imageUrl.ifEmpty { R.drawable.pill },
            contentDescription = null,
            modifier = Modifier
                .size(56.dp)
                .clip(CircleShape),
            placeholder = painterResource(id = R.drawable.pill),
            error = painterResource(id = R.drawable.pill)
        )

        Spacer(modifier = Modifier.width(12.dp))

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = medicine.name,
                fontSize = 16.sp,
                color = Color.Black,
                maxLines = 1
            )
            Text("${medicine.type} • ₱${medicine.price}", fontSize = 12.sp, color = Color.Gray)
        }

        Column(horizontalAlignment = Alignment.End) {
            Text(
                text = medicine.quantity.toString(),
                fontSize = 16.sp,
                color = if (medicine.quantity <= 10) Color(0xFFFF6F00) else Color(0xFF2E3A8C)
            )
            Text("Items", fontSize = 12.sp, color = Color.Gray)
        }

        Box {

            Icon(
                painter = painterResource(id = R.drawable.menu),
                contentDescription = null,
                tint = Color.Gray,
                modifier = Modifier
                    .size(18.dp)
                    .clickable { expanded = true }
            )

            DropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false }
            ) {
                DropdownMenuItem(
                    text = { Text("Edit") },
                    onClick = {
                        expanded = false
                        onEdit()
                    }
                )

                DropdownMenuItem(
                    text = { Text("Delete") },
                    onClick = {
                        expanded = false
                        onDelete()
                    }
                )
            }
        }
    }
}