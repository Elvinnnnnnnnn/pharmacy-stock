package com.example.pharmacystock

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.navigation.compose.*
import com.example.pharmacystock.ui.theme.PharmacyStockTheme
import com.google.firebase.FirebaseApp

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        FirebaseApp.initializeApp(this)

        setContent {
            PharmacyStockTheme {

                val navController = rememberNavController()

                NavHost(
                    navController = navController,
                    startDestination = "welcome"
                ) {

                    composable("welcome") {
                        WelcomeScreen(navController)
                    }

                    composable("signin") {
                        SignInScreen(navController)
                    }

                    composable("signup") {
                        SignUpScreen(navController)
                    }

                    composable("verify") {
                        VerificationScreen(navController)
                    }

                    composable("main") {
                        MainNavScreen(navController)
                    }
                }
            }
        }
    }
}