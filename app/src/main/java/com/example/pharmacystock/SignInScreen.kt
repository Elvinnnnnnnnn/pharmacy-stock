package com.example.pharmacystock

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.compose.foundation.clickable
import com.google.firebase.auth.FirebaseAuth

@Composable
fun SignInScreen(navController: NavController) {

    val auth = FirebaseAuth.getInstance()

    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var errorMessage by remember { mutableStateOf("") }

    val primaryDark = Color(0xFF0A0F2C)
    val green = Color(0xFF4CAF50)
    val inputBorder = Color(0xFFB3B3B3)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Top
    ) {

        Spacer(modifier = Modifier.height(20.dp))

        Image(
            painter = painterResource(id = R.drawable.pharmacy_logo2),
            contentDescription = "Logo",
            modifier = Modifier.size(250.dp)
        )

        Text(
            text = "Sign In",
            style = MaterialTheme.typography.headlineSmall,
            color = primaryDark
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "Manage your pharmacy inventory with ease",
            style = MaterialTheme.typography.bodySmall
        )

        Spacer(modifier = Modifier.height(24.dp))

        OutlinedTextField(
            value = email,
            onValueChange = { email = it },
            placeholder = { Text("Email") },
            leadingIcon = {
                Icon(
                    painter = painterResource(id = R.drawable.email),
                    contentDescription = "Email",
                    modifier = Modifier.size(20.dp),
                    tint = Color.Gray
                )
            },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedTextColor = Color.Black,
                unfocusedTextColor = Color.Black,
                cursorColor = Color.Black,

                focusedBorderColor = inputBorder,
                unfocusedBorderColor = inputBorder,

                focusedPlaceholderColor = Color.Gray,
                unfocusedPlaceholderColor = Color.Gray
            )
        )

        Spacer(modifier = Modifier.height(12.dp))

        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            placeholder = { Text("Password") },
            leadingIcon = {
                Icon(
                    painter = painterResource(id = R.drawable.lock),
                    contentDescription = "Lock",
                    modifier = Modifier.size(20.dp),
                    tint = Color.Gray
                )
            },
            visualTransformation = PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedTextColor = Color.Black,
                unfocusedTextColor = Color.Black,
                cursorColor = Color.Black,

                focusedBorderColor = inputBorder,
                unfocusedBorderColor = inputBorder,

                focusedPlaceholderColor = Color.Gray,
                unfocusedPlaceholderColor = Color.Gray
            )
        )

        Spacer(modifier = Modifier.height(20.dp))

        Button(
            onClick = {

                if (email.isEmpty() || password.isEmpty()) {
                    errorMessage = "Fill all fields"
                    return@Button
                }

                auth.signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener { task ->

                        if (task.isSuccessful) {

                            val user = auth.currentUser

                            if (user?.isEmailVerified == true) {
                                navController.navigate("main") {
                                    popUpTo("signin") { inclusive = true }
                                }
                            } else {
                                navController.navigate("verify")
                            }

                        } else {
                        errorMessage = "Invalid email or password"
                    }
                    }

            },
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp),
            shape = RoundedCornerShape(16.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = green
            )
        ) {
            Text("Sign In", color = Color.White)
        }

        Spacer(modifier = Modifier.height(10.dp))

        if (errorMessage.isNotEmpty()) {
            Text(text = errorMessage, color = Color.Red)
        }

        Spacer(modifier = Modifier.height(12.dp))

        Text(
            text = "Forgot Password?",
            color = Color.Blue
        )

        Spacer(modifier = Modifier.height(16.dp))

        Row {
            Text(text = "Don't have an account? ")
            Text(
                text = "Sign Up",
                color = green,
                modifier = Modifier.clickable {
                    navController.navigate("signup")
                }
            )
        }
    }
}