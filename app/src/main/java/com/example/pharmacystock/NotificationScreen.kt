package com.example.pharmacystock

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.Image
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.res.painterResource
import androidx.navigation.NavHostController
import androidx.compose.material3.*
import androidx.compose.runtime.LaunchedEffect

@Composable
fun NotificationScreen(
    viewModel: PharmacyViewModel,
    navController: NavHostController
) {

    val medicines = viewModel.medicines

    val alerts = medicines.filter {
        it.quantity <= 10
    }

    LaunchedEffect(Unit) {
        viewModel.loadMedicines()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {

        // TOP BAR
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color(0xFF0A0F2C))
                .padding(horizontal = 16.dp, vertical = 20.dp)
        ) {

            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {

                Image(
                    painter = painterResource(id = R.drawable.arrow),
                    contentDescription = null,
                    modifier = Modifier
                        .size(18.dp)
                        .clickable {
                            navController.popBackStack()
                        }
                )

                Spacer(modifier = Modifier.width(8.dp))

                Text(
                    text = "Back",
                    color = Color.White,
                    fontSize = 12.sp
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = "Notifications",
                color = Color.White,
                fontSize = 22.sp
            )
        }

        // CONTENT
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {

            if (alerts.isEmpty()) {

                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        "No alerts",
                        color = Color.Gray,
                        fontSize = 14.sp
                    )
                }

            } else {

                Column(
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {

                    alerts.forEach { med ->

                        val isOut = med.quantity == 0

                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(
                                    MaterialTheme.colorScheme.surface,
                                    RoundedCornerShape(16.dp)
                                )
                                .padding(14.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {

                            // ICON BOX
                            Box(
                                modifier = Modifier
                                    .size(42.dp)
                                    .background(
                                        if (isOut) Color(0xFFFFE0E0)
                                        else Color(0xFFFFF3E0),
                                        RoundedCornerShape(12.dp)
                                    ),
                                contentAlignment = Alignment.Center
                            ) {
                                Image(
                                    painter = painterResource(
                                        id = if (isOut) R.drawable.line else R.drawable.warning
                                    ),
                                    contentDescription = null,
                                    modifier = Modifier.size(20.dp)
                                )
                            }

                            Spacer(modifier = Modifier.width(12.dp))

                            Column(modifier = Modifier.weight(1f)) {

                                Text(
                                    text = med.name,
                                    fontSize = 15.sp,
                                    color = MaterialTheme.colorScheme.onSurface
                                )

                                Text(
                                    text = if (isOut)
                                        "Out of stock"
                                    else
                                        "Low stock (${med.quantity} left)",
                                    fontSize = 12.sp,
                                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                                )
                            }

                            // STATUS BADGE
                            Box(
                                modifier = Modifier
                                    .background(
                                        if (isOut) Color(0xFFE53935)
                                        else Color(0xFFFF9800),
                                        RoundedCornerShape(20.dp)
                                    )
                                    .padding(horizontal = 10.dp, vertical = 4.dp)
                            ) {
                                Text(
                                    text = if (isOut) "OUT" else "LOW",
                                    color = Color.White,
                                    fontSize = 10.sp
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}