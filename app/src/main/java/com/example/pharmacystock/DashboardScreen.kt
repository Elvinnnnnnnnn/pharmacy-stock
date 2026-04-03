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
import androidx.compose.runtime.*
import androidx.compose.foundation.clickable
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.rememberScrollState
import androidx.navigation.NavHostController
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.layout.statusBarsPadding

@Composable
fun DashboardScreen(
    viewModel: PharmacyViewModel,
    navController: NavHostController
) {

    LaunchedEffect(Unit) {
        viewModel.loadMedicines()
        viewModel.loadTransactions()
    }

    val medicines = viewModel.medicines
    val transactions = viewModel.transactions

    val total = medicines.size
    val lowStock = medicines.count { it.quantity in 1..10 }
    val outOfStock = medicines.count { it.quantity == 0 }
    val currentTime = System.currentTimeMillis()
    val thirtyDays = 30L * 24 * 60 * 60 * 1000
    val expiringSoon = medicines.count {
        it.expiryDate in currentTime..(currentTime + thirtyDays)
    }
    var transactionFilter by remember { mutableStateOf("All") }
    val filteredTransactions = transactions.filter {
        when (transactionFilter) {
            "IN" -> it.type == "IN"
            "OUT" -> it.type == "OUT"
            else -> true
        }
    }
    val recentTransactions = filteredTransactions.takeLast(5).reversed()
    var menuExpanded by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .statusBarsPadding()
            .padding(16.dp)
            .background(Color.White)
    ) {

        // HEADER
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {

            Image(
                painter = painterResource(id = R.drawable.pharmacy_logo2),
                contentDescription = null,
                modifier = Modifier
                    .width(200.dp)
                    .height(60.dp)
            )

            Row {
                IconButton(onClick = { }, modifier = Modifier.size(36.dp)) {
                    Icon(
                        painter = painterResource(id = R.drawable.notification),
                        contentDescription = null,
                        modifier = Modifier.size(20.dp)
                    )
                }

                IconButton(onClick = { }, modifier = Modifier.size(36.dp)) {
                    Icon(
                        painter = painterResource(id = R.drawable.profile),
                        contentDescription = null,
                        modifier = Modifier.size(30.dp)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // TABS
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            TabItem("Dashboard", true) {
                navController.navigate("dashboard")
            }

            TabItem("Medicines", false) {
                navController.navigate("medicines")
            }

            TabItem("Stock In/Out", false) {
                navController.navigate("stockInOut")
            }

            TabItem("Reports", false) {
                navController.navigate("reports")
            }
        }

        Spacer(modifier = Modifier.height(30.dp))

        // CARDS
        Column {

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                DashboardCard(
                    "TOTAL MEDICINES",
                    total.toString(),
                    R.drawable.drugs,
                    Color(0xFF4A90E2),
                    Modifier.weight(1f)
                )

                DashboardCard(
                    "LOW STOCK ALERTS",
                    lowStock.toString(),
                    R.drawable.warning,
                    Color(0xFFFF7A00),
                    Modifier.weight(1f)
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                DashboardCard(
                    "EXPIRING SOON",
                    expiringSoon.toString(),
                    R.drawable.clock,
                    Color(0xFFE53935),
                    Modifier.weight(1f)
                )

                DashboardCard(
                    "OUT OF STOCK",
                    outOfStock.toString(),
                    R.drawable.line,
                    Color(0xFF3E4A59),
                    Modifier.weight(1f)
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // RECENT TRANSACTIONS
        // HEADER
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Recent Transactions",
                fontSize = 16.sp,
                color = Color(0xFF2E3A8C)
            )

            Box {

                Icon(
                    painter = painterResource(id = R.drawable.menu),
                    contentDescription = null,
                    tint = Color.Gray,
                    modifier = Modifier
                        .size(18.dp)
                        .clickable { menuExpanded = true }
                )

                DropdownMenu(
                    expanded = menuExpanded,
                    onDismissRequest = { menuExpanded = false }
                ) {

                    DropdownMenuItem(
                        text = { Text("All") },
                        onClick = {
                            transactionFilter = "All"
                            menuExpanded = false
                        }
                    )

                    DropdownMenuItem(
                        text = { Text("Stock In") },
                        onClick = {
                            transactionFilter = "IN"
                            menuExpanded = false
                        }
                    )

                    DropdownMenuItem(
                        text = { Text("Stock Out") },
                        onClick = {
                            transactionFilter = "OUT"
                            menuExpanded = false
                        }
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

// TABLE CONTAINER
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color(0xFFF9FAFB), RoundedCornerShape(16.dp))
                .padding(12.dp)
        ) {

            // HEADER
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    "MEDICINE",
                    modifier = Modifier.weight(2f),
                    fontSize = 12.sp,
                    color = Color.Gray
                )

                Text(
                    "TYPE",
                    modifier = Modifier.weight(1f),
                    fontSize = 12.sp,
                    color = Color.Gray
                )

                Text(
                    "QTY",
                    modifier = Modifier.weight(1f),
                    fontSize = 12.sp,
                    color = Color.Gray
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            if (recentTransactions.isEmpty()) {

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(100.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text("No transactions yet.", color = Color.Gray)
                }

            } else {

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(max = 200.dp)
                        .verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {

                    recentTransactions.forEach { transaction ->

                        val medicineName = medicines.find {
                            it.id == transaction.medicineId
                        }?.name ?: "Unknown"

                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(Color.White, RoundedCornerShape(12.dp))
                                .padding(horizontal = 12.dp, vertical = 10.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {

                            Text(
                                text = medicineName,
                                modifier = Modifier.weight(2f),
                                fontSize = 13.sp
                            )

                            Text(
                                text = transaction.type,
                                modifier = Modifier.weight(1f),
                                fontSize = 12.sp,
                                color = if (transaction.type == "IN")
                                    Color(0xFF3BB273)
                                else Color(0xFFE53935)
                            )

                            Text(
                                text = transaction.quantity.toString(),
                                modifier = Modifier.weight(1f),
                                fontSize = 13.sp
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun TabItem(
    text: String,
    selected: Boolean,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .background(
                color = if (selected) Color(0xFF2E3A8C) else Color.Transparent,
                shape = RoundedCornerShape(20.dp)
            )
            .clickable { onClick() }
            .padding(horizontal = 16.dp, vertical = 8.dp)
    ) {
        Text(
            text = text,
            color = if (selected) Color.White else Color.Gray,
            fontSize = 12.sp
        )
    }
}

@Composable
fun DashboardCard(
    title: String,
    count: String,
    icon: Int,
    color: Color,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .height(110.dp)
            .background(
                color = color,
                shape = RoundedCornerShape(16.dp)
            )
            .padding(12.dp),
        verticalArrangement = Arrangement.SpaceBetween
    ) {

        Row(verticalAlignment = Alignment.CenterVertically) {
            Image(
                painter = painterResource(id = icon),
                contentDescription = null,
                modifier = Modifier.size(20.dp)
            )

            Spacer(modifier = Modifier.width(6.dp))

            Text(
                text = title,
                color = Color.White,
                fontSize = 12.sp
            )
        }

        Row(verticalAlignment = Alignment.Bottom) {
            Text(
                text = count,
                color = Color.White,
                fontSize = 28.sp
            )

            Spacer(modifier = Modifier.width(4.dp))

            Text(
                text = "Items",
                color = Color.White,
                fontSize = 12.sp
            )
        }
    }
}