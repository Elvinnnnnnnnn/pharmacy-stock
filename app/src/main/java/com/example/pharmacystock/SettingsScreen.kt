package com.example.pharmacystock

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.clickable
import androidx.navigation.NavHostController
import androidx.compose.material3.*
import androidx.compose.ui.platform.LocalContext
import androidx.compose.runtime.*
import android.content.Context
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

@Composable
fun SettingsScreen(
    navController: NavHostController,
    isDarkMode: Boolean,
    onToggleDarkMode: (Boolean) -> Unit
) {

    val context = LocalContext.current
    val prefs = context.getSharedPreferences("settings", Context.MODE_PRIVATE)

    var showChangePasswordDialog by remember { mutableStateOf(false) }

    var selectedLanguage by remember {
        mutableStateOf(prefs.getString("language", "English") ?: "English")
    }

    var showLanguageDialog by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .verticalScroll(rememberScrollState())
    ) {

        // TOP BAR
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color(0xFF0A0F2C))
                .padding(start = 16.dp, end = 16.dp, top = 16.dp, bottom = 60.dp)
        ) {

            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.clickable {
                    navController.navigate("main") {
                        popUpTo("settings") { inclusive = true }
                    }
                }
            ) {

                Image(
                    painter = painterResource(id = R.drawable.arrow),
                    contentDescription = null,
                    modifier = Modifier.size(18.dp)
                )

                Spacer(modifier = Modifier.width(8.dp))

                Text(
                    text = "Back to Dashboard",
                    color = if (isDarkMode) Color.White else Color.White,
                    fontSize = 12.sp
                )
            }

            Spacer(modifier = Modifier.height(20.dp))

            Text(
                text = "Settings",
                color = if (isDarkMode) Color.White else Color.White,
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold
            )
        }

        // CONTENT
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .offset(y = (-30).dp)
                .padding(horizontal = 16.dp)
        ) {

            SettingsSection("Quick Settings") {

                var isNotificationEnabled by remember {
                    mutableStateOf(prefs.getBoolean("notifications", false))
                }

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 10.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {

                    Image(
                        painter = painterResource(id = R.drawable.notificationyellow),
                        contentDescription = null,
                        modifier = Modifier.size(22.dp)
                    )

                    Spacer(modifier = Modifier.width(12.dp))

                    Column(modifier = Modifier.weight(1f)) {
                        Text("Notifications", fontSize = 14.sp, color = MaterialTheme.colorScheme.onSurface)
                        Text(
                            "Enable push notification",
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                        )
                    }

                    Switch(
                        checked = isNotificationEnabled,
                        onCheckedChange = {
                            isNotificationEnabled = it

                            prefs.edit().putBoolean("notifications", it).apply()

                            if (it) {
                                NotificationHelper.showNotification(
                                    context,
                                    "Notifications Enabled",
                                    "You will now receive alerts"
                                )
                            }
                        }
                    )
                }

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 10.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {

                    Icon(
                        painter = painterResource(id = R.drawable.dark),
                        contentDescription = null,
                        modifier = Modifier.size(22.dp),
                        tint = MaterialTheme.colorScheme.onSurface
                    )

                    Spacer(modifier = Modifier.width(12.dp))

                    Column(
                        modifier = Modifier.weight(1f)
                    ) {
                        Text(
                            text = "Dark Mode",
                            fontSize = 14.sp,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text("Change app theme", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f))
                    }

                    Switch(
                        checked = isDarkMode,
                        onCheckedChange = { onToggleDarkMode(it) }
                    )
                }
            }

            SettingsSection("Account") {

                SettingsItem(
                    icon = R.drawable.changepassword,
                    title = "Change Password",
                    onClick = { showChangePasswordDialog = true }
                )
                SettingsItem(
                    icon = R.drawable.privacy,
                    title = "Privacy & Security",
                    onClick = { navController.navigate("privacy") }
                )

                SettingsItem(
                    icon = R.drawable.database,
                    title = "Data Management",
                    onClick = { navController.navigate("data") }
                )
            }

            SettingsSection("Preference") {

                SettingsItem(
                    icon = R.drawable.language,
                    title = "Language",
                    subtitle = selectedLanguage,
                    onClick = { showLanguageDialog = true }
                )
                SettingsItem(
                    icon = R.drawable.notificationyellow,
                    title = "Notification Settings",
                    onClick = { navController.navigate("notification_settings") }
                )
            }

            SettingsSection("Support") {

                SettingsItem(
                    icon = R.drawable.help,
                    title = "Help & FAQ",
                    onClick = { navController.navigate("faq") }
                )
            }

            Spacer(modifier = Modifier.height(20.dp))

            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {

                Text("PharmacyStock", fontSize = 14.sp, color = Color(0xFF1E2A78))
                Text("Version 1.0.0", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f))

                Spacer(modifier = Modifier.height(10.dp))

                Text(
                    "© 2024 PharmacyStock. All rights reserved.",
                    fontSize = 11.sp,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                )
            }

            Spacer(modifier = Modifier.height(20.dp))
        }
    }
    if (showChangePasswordDialog) {
        ChangePasswordDialog(
            onDismiss = { showChangePasswordDialog = false },
            onConfirm = { current, new ->

                val user = FirebaseAuth.getInstance().currentUser
                val email = user?.email

                if (user != null && email != null) {

                    val credential = EmailAuthProvider.getCredential(email, current)

                    user.reauthenticate(credential)
                        .addOnSuccessListener {

                            user.updatePassword(new)
                                .addOnSuccessListener {

                                    NotificationHelper.showNotification(
                                        context,
                                        "Success",
                                        "Password updated successfully"
                                    )

                                    showChangePasswordDialog = false
                                }
                                .addOnFailureListener {

                                    NotificationHelper.showNotification(
                                        context,
                                        "Error",
                                        "Failed to update password"
                                    )
                                }
                        }
                        .addOnFailureListener {

                            NotificationHelper.showNotification(
                                context,
                                "Error",
                                "Current password is incorrect"
                            )
                        }
                }
            }
        )
    }

    if (showLanguageDialog) {

        val languages = listOf("English", "Filipino")

        AlertDialog(
            onDismissRequest = { showLanguageDialog = false },
            title = { Text("Select Language") },
            text = {

                Column {
                    languages.forEach { lang ->
                        Text(
                            text = lang,
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    selectedLanguage = lang
                                    prefs.edit().putString("language", lang).apply()

                                    showLanguageDialog = false
                                }
                                .padding(12.dp)
                        )
                    }
                }
            },
            confirmButton = {},
            dismissButton = {}
        )
    }
}

@Composable
fun SettingsSection(title: String, content: @Composable ColumnScope.() -> Unit) {

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 16.dp)
            .background(MaterialTheme.colorScheme.surface, RoundedCornerShape(12.dp))
            .padding(12.dp)
    ) {

        Text(
            text = title,
            fontSize = 13.sp,
            fontWeight = FontWeight.SemiBold,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
        )

        Spacer(modifier = Modifier.height(8.dp))

        content()
    }
}

@Composable
fun SettingsItem(
    icon: Int,
    title: String,
    subtitle: String? = null,
    onClick: (() -> Unit)? = null
) {

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick?.invoke() }
            .padding(vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {

        Image(
            painter = painterResource(id = icon),
            contentDescription = null,
            modifier = Modifier.size(22.dp)
        )

        Spacer(modifier = Modifier.width(12.dp))

        Column {

            Text(
                text = title,
                fontSize = 14.sp,
                color = MaterialTheme.colorScheme.onSurface
            )

            if (subtitle != null) {
                Text(
                    text = subtitle,
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                )
            }
        }
    }
}

@Composable
fun ChangePasswordDialog(
    onDismiss: () -> Unit,
    onConfirm: (String, String) -> Unit
) {

    var currentPassword by remember { mutableStateOf("") }
    var newPassword by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Change Password") },
        text = {

            Column {

                OutlinedTextField(
                    value = currentPassword,
                    onValueChange = { currentPassword = it },
                    label = { Text("Current Password") },
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = newPassword,
                    onValueChange = { newPassword = it },
                    label = { Text("New Password") },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    onConfirm(currentPassword, newPassword)
                }
            ) {
                Text("Update")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}

@Composable
fun PrivacyScreen(navController: NavHostController) {

    var showChangePasswordDialog by remember { mutableStateOf(false) }
    val context = LocalContext.current

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .verticalScroll(rememberScrollState())
    ) {

        // TOP BAR
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color(0xFF0A0F2C))
                .padding(start = 16.dp, end = 16.dp, top = 16.dp, bottom = 60.dp)
        ) {

            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.clickable {
                    navController.popBackStack()
                }
            ) {

                Image(
                    painter = painterResource(id = R.drawable.arrow),
                    contentDescription = null,
                    modifier = Modifier.size(18.dp)
                )

                Spacer(modifier = Modifier.width(8.dp))

                Text(
                    text = "Back",
                    color = Color.White,
                    fontSize = 12.sp
                )
            }

            Spacer(modifier = Modifier.height(20.dp))

            Text(
                text = "Privacy & Security",
                color = Color.White,
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold
            )
        }

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .offset(y = (-30).dp)
                .padding(horizontal = 16.dp)
        ) {

            // ACCOUNT SECURITY
            SettingsSection("Account Security") {

                SettingsItem(
                    icon = R.drawable.changepassword,
                    title = "Change Password",
                    onClick = { showChangePasswordDialog = true }
                )

                val user = FirebaseAuth.getInstance().currentUser

                SettingsItem(
                    icon = R.drawable.notificationyellow,
                    title = "Active Session",
                    subtitle = user?.email ?: "No active user"
                )
            }

            // PRIVACY INFO
            SettingsSection("Privacy") {

                Text(
                    text = "Your data is stored securely and only used for inventory management.",
                    fontSize = 12.sp,
                    color = Color.Gray
                )
            }

            // LOGOUT
            SettingsSection("Session") {

                SettingsItem(
                    icon = R.drawable.logout, // make sure you have this icon
                    title = "Logout",
                    onClick = {

                        FirebaseAuth.getInstance().signOut()

                        navController.navigate("signin") {
                            popUpTo("main") { inclusive = true }
                        }
                    }
                )
            }

            Spacer(modifier = Modifier.height(20.dp))
        }
    }
    if (showChangePasswordDialog) {
        ChangePasswordDialog(
            onDismiss = { showChangePasswordDialog = false },
            onConfirm = { current, new ->

                val user = FirebaseAuth.getInstance().currentUser
                val email = user?.email

                if (user != null && email != null) {

                    val credential = EmailAuthProvider.getCredential(email, current)

                    user.reauthenticate(credential)
                        .addOnSuccessListener {

                            user.updatePassword(new)
                                .addOnSuccessListener {

                                    NotificationHelper.showNotification(
                                        context,
                                        "Success",
                                        "Password updated"
                                    )

                                    showChangePasswordDialog = false
                                }
                                .addOnFailureListener {

                                    NotificationHelper.showNotification(
                                        context,
                                        "Error",
                                        "Update failed"
                                    )
                                }
                        }
                        .addOnFailureListener {

                            NotificationHelper.showNotification(
                                context,
                                "Error",
                                "Wrong current password"
                            )
                        }
                }
            }
        )
    }
}

@Composable
fun DataManagementScreen(navController: NavHostController) {

    val context = LocalContext.current

    var showConfirmDialog by remember { mutableStateOf(false) }
    var actionType by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .verticalScroll(rememberScrollState())
    ) {

        // TOP BAR
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color(0xFF0A0F2C))
                .padding(start = 16.dp, end = 16.dp, top = 16.dp, bottom = 60.dp)
        ) {

            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.clickable {
                    navController.popBackStack()
                }
            ) {

                Image(
                    painter = painterResource(id = R.drawable.arrow),
                    contentDescription = null,
                    modifier = Modifier.size(18.dp)
                )

                Spacer(modifier = Modifier.width(8.dp))

                Text(
                    text = "Back",
                    color = Color.White,
                    fontSize = 12.sp
                )
            }

            Spacer(modifier = Modifier.height(20.dp))

            Text(
                text = "Data Management",
                color = Color.White,
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold
            )
        }

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .offset(y = (-30).dp)
                .padding(horizontal = 16.dp)
        ) {

            // DATA ACTIONS
            SettingsSection("Data Actions") {

                SettingsItem(
                    icon = R.drawable.database,
                    title = "Clear All Inventory Data",
                    subtitle = "Remove all medicines and records",
                    onClick = {
                        actionType = "clear_inventory"
                        showConfirmDialog = true
                    }
                )

                SettingsItem(
                    icon = R.drawable.database,
                    title = "Reset Transactions",
                    subtitle = "Clear stock history",
                    onClick = {
                        actionType = "reset_transactions"
                        showConfirmDialog = true
                    }
                )
            }

            // STORAGE INFO
            SettingsSection("Storage") {

                Text(
                    text = "All data is stored securely in the cloud. Clearing data cannot be undone.",
                    fontSize = 12.sp,
                    color = Color.Gray
                )
            }

            // DANGER ZONE
            SettingsSection("Danger Zone") {

                SettingsItem(
                    icon = R.drawable.delete, // add icon if you have
                    title = "Delete All Data",
                    subtitle = "Permanent action",
                    onClick = {
                        actionType = "delete_all"
                        showConfirmDialog = true
                        NotificationHelper.showNotification(
                            context,
                            "Warning",
                            "All data deleted"
                        )
                    }
                )
            }

            Spacer(modifier = Modifier.height(20.dp))
        }
    }
    if (showConfirmDialog) {

        AlertDialog(
            onDismissRequest = { showConfirmDialog = false },
            title = { Text("Confirm Action") },
            text = { Text("This action cannot be undone. Continue?") },
            confirmButton = {
                TextButton(
                    onClick = {

                        val db = FirebaseFirestore.getInstance()

                        when (actionType) {

                            "clear_inventory" -> {
                                db.collection("medicines").get()
                                    .addOnSuccessListener { result ->
                                        for (doc in result) {
                                            doc.reference.delete()
                                        }
                                    }
                            }

                            "reset_transactions" -> {
                                db.collection("transactions").get()
                                    .addOnSuccessListener { result ->
                                        for (doc in result) {
                                            doc.reference.delete()
                                        }
                                    }
                            }

                            "delete_all" -> {
                                db.collection("medicines").get()
                                    .addOnSuccessListener { result ->
                                        for (doc in result) {
                                            doc.reference.delete()
                                        }
                                    }

                                db.collection("transactions").get()
                                    .addOnSuccessListener { result ->
                                        for (doc in result) {
                                            doc.reference.delete()
                                        }
                                    }
                            }
                        }

                        NotificationHelper.showNotification(
                            context,
                            "Success",
                            "Action completed"
                        )

                        showConfirmDialog = false
                    }
                ) {
                    Text("Yes")
                }
            },
            dismissButton = {
                TextButton(
                    onClick = { showConfirmDialog = false }
                ) {
                    Text("Cancel")
                }
            }
        )
    }
}

@Composable
fun NotificationSettingsScreen(navController: NavHostController) {

    val context = LocalContext.current
    val prefs = context.getSharedPreferences("settings", Context.MODE_PRIVATE)

    var lowStock by remember {
        mutableStateOf(prefs.getBoolean("low_stock", true))
    }

    var outOfStock by remember {
        mutableStateOf(prefs.getBoolean("out_stock", true))
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .verticalScroll(rememberScrollState())
    ) {

        // TOP BAR
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color(0xFF0A0F2C))
                .padding(16.dp)
        ) {

            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.clickable { navController.popBackStack() }
            ) {

                Image(
                    painter = painterResource(id = R.drawable.arrow),
                    contentDescription = null,
                    modifier = Modifier.size(18.dp)
                )

                Spacer(modifier = Modifier.width(8.dp))

                Text(
                    text = "Back",
                    color = Color.White,
                    fontSize = 12.sp
                )
            }

            Spacer(modifier = Modifier.height(20.dp))

            Text(
                text = "Notification Settings",
                color = Color.White,
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold
            )
        }

        Column(
            modifier = Modifier
                .padding(16.dp)
        ) {

            SettingsSection("Alerts") {

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {

                    Text(
                        text = "Low Stock Alerts",
                        modifier = Modifier.weight(1f),
                        color = MaterialTheme.colorScheme.onSurface
                    )

                    Switch(
                        checked = lowStock,
                        onCheckedChange = {
                            lowStock = it
                            prefs.edit().putBoolean("low_stock", it).apply()
                        }
                    )
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {

                    Text(
                        text = "Out of Stock Alerts",
                        modifier = Modifier.weight(1f),
                        color = MaterialTheme.colorScheme.onSurface
                    )

                    Switch(
                        checked = outOfStock,
                        onCheckedChange = {
                            outOfStock = it
                            prefs.edit().putBoolean("out_stock", it).apply()
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun FAQScreen(navController: NavHostController) {

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .verticalScroll(rememberScrollState())
    ) {

        // TOP BAR
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color(0xFF0A0F2C))
                .padding(start = 16.dp, end = 16.dp, top = 16.dp, bottom = 60.dp)
        ) {

            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.clickable {
                    navController.popBackStack()
                }
            ) {

                Image(
                    painter = painterResource(id = R.drawable.arrow),
                    contentDescription = null,
                    modifier = Modifier.size(18.dp)
                )

                Spacer(modifier = Modifier.width(8.dp))

                Text(
                    text = "Back",
                    color = Color.White,
                    fontSize = 12.sp
                )
            }

            Spacer(modifier = Modifier.height(20.dp))

            Text(
                text = "Help & FAQ",
                color = Color.White,
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold
            )
        }

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .offset(y = (-30).dp)
                .padding(horizontal = 16.dp)
        ) {

            FAQItem(
                question = "How do I add medicine?",
                answer = "Go to dashboard and tap Add button, then fill details."
            )

            FAQItem(
                question = "Why am I not receiving notifications?",
                answer = "Check Notification Settings and make sure alerts are enabled."
            )

            FAQItem(
                question = "How to reset my data?",
                answer = "Go to Data Management and use Clear or Reset options."
            )

            FAQItem(
                question = "Is my data secure?",
                answer = "Yes. Data is stored securely using Firebase services."
            )

            FAQItem(
                question = "How do I change my password?",
                answer = "Go to Settings, then tap Change Password."
            )

            Spacer(modifier = Modifier.height(20.dp))
        }
    }
}

@Composable
fun FAQItem(question: String, answer: String) {

    var expanded by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 10.dp)
            .background(
                MaterialTheme.colorScheme.surface,
                RoundedCornerShape(12.dp)
            )
            .clickable { expanded = !expanded }
            .padding(12.dp)
    ) {

        Text(
            text = question,
            fontSize = 14.sp,
            fontWeight = FontWeight.SemiBold,
            color = MaterialTheme.colorScheme.onSurface
        )

        if (expanded) {

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = answer,
                fontSize = 12.sp,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
            )
        }
    }
}