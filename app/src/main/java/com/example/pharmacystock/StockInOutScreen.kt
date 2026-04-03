package com.example.pharmacystock

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.material3.ButtonDefaults.buttonColors
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.clickable
import androidx.compose.ui.platform.LocalContext

@Composable
fun StockInOutScreen(viewModel: PharmacyViewModel) {

    var selectedTab by remember { mutableStateOf("in") }
    val context = LocalContext.current
    val medicines = viewModel.medicines

    var selectedMedicine by remember { mutableStateOf<Medicine?>(null) }
    var quantity by remember { mutableStateOf("") }
    var supplier by remember { mutableStateOf("") }
    var date by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .background(Color.White)
    ) {

        // HEADER
        Text(
            text = if (selectedTab == "in") "Stock In" else "Stock Out",
            fontSize = 20.sp,
            color = Color(0xFF2E3A8C)
        )

        Spacer(modifier = Modifier.height(16.dp))

        // TABS
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    Color.LightGray.copy(alpha = 0.3f),
                    RoundedCornerShape(12.dp)
                )
                .padding(4.dp)
        ) {

            TabButton("Stock In", selectedTab == "in") {
                selectedTab = "in"
            }

            TabButton("Stock Out", selectedTab == "out") {
                selectedTab = "out"
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // FORM
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color.White, RoundedCornerShape(16.dp))
                .padding(16.dp)
        ) {

            // SELECT MEDICINE
            Label("SELECT MEDICINE")

            selectedMedicine?.let {
                Text(
                    text = "Current Stock: ${it.quantity}",
                    color = Color.Gray,
                    fontSize = 12.sp
                )

                Spacer(modifier = Modifier.height(8.dp))
            }

            DropdownMenuBox(
                medicines = medicines,
                selectedMedicine = selectedMedicine,
                onSelect = { selectedMedicine = it }
            )

            Spacer(modifier = Modifier.height(12.dp))

            // QUANTITY
            Label("QUANTITY")

            OutlinedTextField(
                value = quantity,
                onValueChange = { quantity = it },
                modifier = Modifier.fillMaxWidth(),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedTextColor = Color.Black,
                    unfocusedTextColor = Color.Black,
                    cursorColor = Color.Black,
                    focusedBorderColor = Color.Gray,
                    unfocusedBorderColor = Color.Gray
                )
            )

            Spacer(modifier = Modifier.height(12.dp))

            // SUPPLIER (only for stock in)
            if (selectedTab == "in") {
                Label("Supplier / Reference")

                OutlinedTextField(
                    value = supplier,
                    onValueChange = { supplier = it },
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = Color.Black,
                        unfocusedTextColor = Color.Black,
                        cursorColor = Color.Black,
                        focusedBorderColor = Color.Gray,
                        unfocusedBorderColor = Color.Gray
                    )
                )

                Spacer(modifier = Modifier.height(12.dp))
            }

            if (selectedTab == "out" && selectedMedicine != null) {

                val qty = quantity.toIntOrNull()

                if (qty != null && qty > selectedMedicine!!.quantity) {
                    Text(
                        text = "Not enough stock",
                        color = Color.Red,
                        fontSize = 12.sp
                    )
                }
            }

            // DATE
            Label("DATE")

            OutlinedButton(
                onClick = {

                    val calendar = java.util.Calendar.getInstance()

                    val year = calendar.get(java.util.Calendar.YEAR)
                    val month = calendar.get(java.util.Calendar.MONTH)
                    val day = calendar.get(java.util.Calendar.DAY_OF_MONTH)

                    android.app.DatePickerDialog(
                        context,
                        { _, y, m, d ->
                            date = "${m + 1}/$d/$y"
                        },
                        year,
                        month,
                        day
                    ).show()
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                shape = RoundedCornerShape(8.dp)
            ) {
                Text(
                    text = if (date.isEmpty()) "Select date" else date,
                    color = Color.Gray
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // BUTTON
            Button(
                onClick = {

                    val qty = quantity.toIntOrNull() ?: return@Button
                    val med = selectedMedicine ?: return@Button

                    if (selectedTab == "in") {

                        viewModel.stockIn(
                            medicineId = med.id,
                            quantity = qty,
                            supplier = supplier,
                            date = date
                        )

                    } else {

                        if (qty > med.quantity) {
                            return@Button
                        }

                        viewModel.stockOut(
                            medicineId = med.id,
                            quantity = qty,
                            date = date
                        )
                    }

                    quantity = ""
                    supplier = ""
                    date = ""

                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                shape = RoundedCornerShape(12.dp),
                colors = buttonColors(containerColor = Color(0xFF3BB273))
            ) {
                Text(
                    text = if (selectedTab == "in") "Stock In" else "Stock Out",
                    color = Color.White
                )
            }
        }
    }
}
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DropdownMenuBox(
    medicines: List<Medicine>,
    selectedMedicine: Medicine?,
    onSelect: (Medicine) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = !expanded }
    ) {

        OutlinedTextField(
            value = selectedMedicine?.name ?: "Select medicine",
            onValueChange = {},
            readOnly = true,
            modifier = Modifier
                .menuAnchor()
                .fillMaxWidth(),
            trailingIcon = {
                ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded)
            },
            colors = OutlinedTextFieldDefaults.colors(
                focusedTextColor = Color.Black,
                unfocusedTextColor = Color.Black,
                cursorColor = Color.Black,
                focusedBorderColor = Color.Gray,
                unfocusedBorderColor = Color.Gray
            )
        )

        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            medicines.forEach { medicine ->
                DropdownMenuItem(
                    text = {
                        Text("${medicine.name} (${medicine.quantity})")
                    },
                    onClick = {
                        onSelect(medicine)
                        expanded = false
                    }
                )
            }
        }
    }
}
@Composable
fun RowScope.TabButton(
    text: String,
    selected: Boolean,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .weight(1f)
            .clickable { onClick() }
            .background(
                if (selected) Color(0xFF3BB273) else Color.Transparent,
                RoundedCornerShape(10.dp)
            )
            .padding(vertical = 10.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            color = if (selected) Color.White else Color.Gray
        )
    }
}
@Composable
fun Label(text: String) {
    Text(
        text = text,
        fontSize = 12.sp,
        color = Color.Gray
    )
}