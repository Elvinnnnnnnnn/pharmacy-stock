package com.example.pharmacystock

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.google.firebase.auth.FirebaseAuth

@Composable
fun VerificationScreen(navController: NavController) {

    val auth = FirebaseAuth.getInstance()
    var message by remember { mutableStateOf("Check your email for verification") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {

        Text(text = "Email Verification")

        Spacer(modifier = Modifier.height(16.dp))

        Text(text = message)

        Spacer(modifier = Modifier.height(20.dp))

        Button(
            onClick = {

                val user = auth.currentUser
                user?.reload()

                if (user?.isEmailVerified == true) {
                    navController.navigate("dashboard")
                } else {
                    message = "Still not verified"
                }

            }
        ) {
            Text("I have verified")
        }
    }
}