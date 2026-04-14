package com.example.pharmacystock

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.draw.shadow
import androidx.compose.foundation.clickable
import androidx.compose.runtime.*
import coil.compose.AsyncImage
import androidx.compose.ui.draw.clip
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.rememberScrollState
import androidx.compose.ui.platform.LocalContext
import android.content.Context

@Composable
fun ReportsScreen(viewModel: PharmacyViewModel) {

    LaunchedEffect(Unit) {
        viewModel.loadTransactions()
    }

    val context = LocalContext.current
    val notifiedItems = remember { mutableStateListOf<String>() }
    val prefs = context.getSharedPreferences("settings", Context.MODE_PRIVATE)
    val isNotificationEnabled = prefs.getBoolean("notifications", false)

    val lowStockEnabled = prefs.getBoolean("low_stock", true)
    val outStockEnabled = prefs.getBoolean("out_stock", true)

    var currentIndex by remember { mutableStateOf(0) }
    val medicines = viewModel.medicines
    val transactions = viewModel.transactions
    val groupedData = transactions.groupBy { it.date }
    val chartData = groupedData.map { (date, list) ->

        val stockIn = list
            .filter { it.type == "IN" }
            .sumOf { it.quantity }

        val stockOut = list
            .filter { it.type == "OUT" }
            .sumOf { it.quantity }

        Triple(date, stockIn, stockOut)
    }
    var selectedFilter by remember { mutableStateOf("All") }
    var expanded by remember { mutableStateOf(false) }

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
                text = "Reports",
                fontSize = 20.sp,
                color = Color(0xFF2E3A8C)
            )

            Row(verticalAlignment = Alignment.CenterVertically) {

                Text(
                    text = "Filter",
                    fontSize = 12.sp,
                    color = Color.Gray
                )

                Spacer(modifier = Modifier.width(8.dp))

                Box {

                    Box(
                        modifier = Modifier
                            .background(
                                Color(0xFF2E3A8C),
                                RoundedCornerShape(12.dp)
                            )
                            .clickable { expanded = true }
                            .padding(horizontal = 12.dp, vertical = 6.dp)
                    ) {
                        Text(
                            text = when (selectedFilter) {
                                "All" -> "All Alerts"
                                "Low" -> "Low Stock"
                                "Out" -> "Out of Stock"
                                else -> "All Alerts"
                            },
                            color = Color.White,
                            fontSize = 12.sp
                        )
                    }

                    DropdownMenu(
                        expanded = expanded,
                        onDismissRequest = { expanded = false }
                    ) {

                        DropdownMenuItem(
                            text = { Text("All Alerts") },
                            onClick = {
                                selectedFilter = "All"
                                expanded = false
                            }
                        )

                        DropdownMenuItem(
                            text = { Text("Low Stock") },
                            onClick = {
                                selectedFilter = "Low"
                                expanded = false
                            }
                        )

                        DropdownMenuItem(
                            text = { Text("Out of Stock") },
                            onClick = {
                                selectedFilter = "Out"
                                expanded = false
                            }
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // INVENTORY ALERTS
        Text(
            text = "INVENTORY ALERTS",
            fontSize = 12.sp,
            color = Color.Gray
        )

        Spacer(modifier = Modifier.height(12.dp))

        val alertList = medicines.filter { med ->
            when (selectedFilter) {
                "Low" -> med.quantity in 1..10
                "Out" -> med.quantity == 0
                else -> med.quantity <= 10 || med.quantity == 0
            }
        }

        LaunchedEffect(alertList) {

            if (isNotificationEnabled) {

                alertList.forEach { med ->

                    if (lowStockEnabled && med.quantity in 1..10 && !notifiedItems.contains(med.id)) {

                        NotificationHelper.showNotification(
                            context,
                            "Low Stock Alert",
                            "${med.name} is low on stock (${med.quantity} left)"
                        )

                        notifiedItems.add(med.id)
                    }

                    if (outStockEnabled && med.quantity == 0 && !notifiedItems.contains(med.id)) {

                        NotificationHelper.showNotification(
                            context,
                            "Out of Stock",
                            "${med.name} is out of stock"
                        )

                        notifiedItems.add(med.id)
                    }
                }
            }
        }

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(max = 250.dp)
        ) {

            Column(
                modifier = Modifier.verticalScroll(rememberScrollState())
            ) {

                alertList.forEach { med ->
                    // EXISTING UI
                    when {
                        med.quantity == 0 -> {
                            AlertItem(
                                med.name,
                                "Current Stock: ${med.quantity}",
                                "Out of Stock",
                                Color(0xFFFFCDD2),
                                med.imageUrl
                            )
                        }

                        med.quantity <= 10 -> {
                            AlertItem(
                                med.name,
                                "Current Stock: ${med.quantity}",
                                "Low Stock",
                                Color(0xFF2E3A8C),
                                med.imageUrl
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // STOCK IN / OUT TITLE
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {

            Text(
                text = "<",
                color = Color.Gray,
                modifier = Modifier.clickable {
                    if (currentIndex < chartData.size - 3) {
                        currentIndex++
                    }
                }
            )

            Text(
                text = "Stock In / Stock Out",
                fontSize = 16.sp,
                color = Color(0xFF2E3A8C)
            )

            Text(
                text = ">",
                color = Color.Gray,
                modifier = Modifier.clickable {
                    if (currentIndex > 0) {
                        currentIndex--
                    }
                }
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        // CHART CARD
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color.White, RoundedCornerShape(16.dp))
                .padding(16.dp)
        ) {

            val visibleData = chartData
                .reversed()
                .drop(currentIndex)
                .take(3)

            // SIMPLE BAR CHART MOCK
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(120.dp),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.Bottom
            ) {

                visibleData.forEach { data ->
                    BarGroup(
                        stockIn = data.second / 2,
                        stockOut = data.third / 2
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // DATES
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                visibleData.forEach { data ->
                    Text(data.first, fontSize = 12.sp, color = Color.Gray)
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // LEGEND
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center
            ) {

                LegendItem(Color(0xFF3BB273), "Stock In")

                Spacer(modifier = Modifier.width(16.dp))

                LegendItem(Color(0xFFE53935), "Stock Out")
            }
        }
    }
}

@Composable
fun AlertItem(
    title: String,
    subtitle: String,
    status: String,
    badgeColor: Color,
    imageUrl: String
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 4.dp, vertical = 4.dp)
            .shadow(
                elevation = 8.dp,
                shape = RoundedCornerShape(16.dp),
                clip = false
            )
            .background(
                Color.White,
                RoundedCornerShape(16.dp)
            )
            .padding(12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {

        AsyncImage(
            model = if (imageUrl.isNotEmpty()) imageUrl else R.drawable.pill,
            contentDescription = null,
            modifier = Modifier
                .size(40.dp)
                .clip(CircleShape)
        )

        Spacer(modifier = Modifier.width(10.dp))

        Column(modifier = Modifier.weight(1f)) {
            Text(title, color = Color(0xFF2E3A8C))
            Text(subtitle, fontSize = 12.sp, color = Color.Gray)
        }

        Box(
            modifier = Modifier
                .background(badgeColor, RoundedCornerShape(20.dp))
                .padding(horizontal = 10.dp, vertical = 4.dp)
        ) {
            Text(
                text = status,
                fontSize = 12.sp,
                color = Color.White
            )
        }
    }
}

@Composable
fun BarGroup(stockIn: Int, stockOut: Int) {
    Row(
        verticalAlignment = Alignment.Bottom,
        horizontalArrangement = Arrangement.spacedBy(6.dp)
    ) {

        Box(
            modifier = Modifier
                .width(16.dp)
                .height(stockIn.dp)
                .background(Color(0xFF3BB273), RoundedCornerShape(4.dp))
        )

        Box(
            modifier = Modifier
                .width(16.dp)
                .height(stockOut.dp)
                .background(Color(0xFFE53935), RoundedCornerShape(4.dp))
        )
    }
}

@Composable
fun LegendItem(color: Color, text: String) {
    Row(verticalAlignment = Alignment.CenterVertically) {

        Box(
            modifier = Modifier
                .size(10.dp)
                .background(color, CircleShape)
        )

        Spacer(modifier = Modifier.width(6.dp))

        Text(text, fontSize = 12.sp, color = Color.Gray)
    }
}