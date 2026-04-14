package com.example.pharmacystock

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.navigation.compose.*
import com.example.pharmacystock.ui.theme.PharmacyStockTheme
import com.google.firebase.FirebaseApp
import androidx.compose.runtime.*
import android.Manifest
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.lifecycle.viewmodel.compose.viewModel
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.Modifier
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.background
import androidx.compose.animation.*
import androidx.compose.animation.core.tween


class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        window.navigationBarColor = android.graphics.Color.WHITE

        window.decorView.systemUiVisibility =
            window.decorView.systemUiVisibility or
                    android.view.View.SYSTEM_UI_FLAG_LIGHT_NAVIGATION_BAR

        FirebaseApp.initializeApp(this)

        setContent {
            val context = this
            val prefs = context.getSharedPreferences("settings", MODE_PRIVATE)

            var isDarkMode by remember {
                mutableStateOf(prefs.getBoolean("dark_mode", false))
            }

            val launcher = rememberLauncherForActivityResult(
                contract = ActivityResultContracts.RequestPermission(),
                onResult = {}
            )

            LaunchedEffect(Unit) {
                if (Build.VERSION.SDK_INT >= 33) {
                    launcher.launch(Manifest.permission.POST_NOTIFICATIONS)
                }
            }

            PharmacyStockTheme(darkTheme = isDarkMode) {

                val navController = rememberNavController()
                val viewModel: PharmacyViewModel = viewModel()

                NavHost(
                    navController = navController,
                    startDestination = "welcome",

                    enterTransition = {
                        slideInHorizontally(
                            initialOffsetX = { it },
                            animationSpec = tween(300)
                        )
                    },

                    exitTransition = {
                        slideOutHorizontally(
                            targetOffsetX = { -it },
                            animationSpec = tween(300)
                        )
                    },

                    popEnterTransition = {
                        slideInHorizontally(
                            initialOffsetX = { -it },
                            animationSpec = tween(300)
                        )
                    },

                    popExitTransition = {
                        slideOutHorizontally(
                            targetOffsetX = { it },
                            animationSpec = tween(300)
                        )
                    }
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
                        MainNavScreen(navController, isDarkMode)
                    }

                    composable("profile") {
                        ProfileScreen(navController)
                    }

                    composable("settings") {
                        SettingsScreen(navController, isDarkMode) {
                            isDarkMode = it
                            prefs.edit().putBoolean("dark_mode", it).apply()
                        }
                    }

                    composable("editProfile") {
                        EditProfileScreen(navController)
                    }

                    composable("notifications") {
                        NotificationScreen(viewModel, navController)
                    }

                    composable("privacy") {
                        PrivacyScreen(navController)
                    }

                    composable("data") {
                        DataManagementScreen(navController)
                    }

                    composable("notification_settings") {
                        NotificationSettingsScreen(navController)
                    }

                    composable("faq") {
                        FAQScreen(navController)
                    }
                }
            }
        }
    }
}