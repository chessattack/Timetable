package com.example

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Person
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.ui.Alignment
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.sp
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.ui.theme.MyApplicationTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        createNotificationChannel()
        enableEdgeToEdge()
        setContent {
            MyApplicationTheme {
                TimetableApp()
            }
        }
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = "Timetable Updates"
            val descriptionText = "Notifications for timetable changes"
            val importance = NotificationManager.IMPORTANCE_HIGH
            val channel = NotificationChannel("timetable_channel", name, importance).apply {
                description = descriptionText
            }
            val notificationManager: NotificationManager =
                getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }
}

sealed class BottomNavItem(var title: String, var icon: ImageVector, var route: String) {
    object Timetable : BottomNavItem("Timetable", Icons.Filled.DateRange, "classes")
    object Notifications : BottomNavItem("Notifications", Icons.Filled.Notifications, "notifications")
    object Profile : BottomNavItem("Profile", Icons.Filled.Person, "profile")
}

@Composable
fun TimetableApp() {
    var isAuthenticated by remember { mutableStateOf(false) }

    if (!isAuthenticated) {
        PasswordScreen(onAuthenticated = { isAuthenticated = true })
    } else {
        AuthenticatedApp()
    }
}

@Composable
fun PasswordScreen(onAuthenticated: () -> Unit) {
    var passwordInput by remember { mutableStateOf("") }
    var errorMessage by remember { mutableStateOf("") }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFFAF9F2)),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(24.dp),
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = "School Timetable App",
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF172047),
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Please enter password to unlock",
                fontSize = 16.sp,
                color = Color(0xFF64748B),
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(32.dp))
            OutlinedTextField(
                value = passwordInput,
                onValueChange = { 
                    passwordInput = it
                    errorMessage = ""
                },
                label = { Text("Password") },
                placeholder = { Text("Enter password") },
                visualTransformation = PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.NumberPassword),
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                singleLine = true
            )
            if (errorMessage.isNotBlank()) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = errorMessage,
                    color = Color(0xFFE84356),
                    fontSize = 14.sp
                )
            }
            Spacer(modifier = Modifier.height(24.dp))
            Button(
                onClick = {
                    if (passwordInput.trim() == "123") {
                        onAuthenticated()
                    } else {
                        errorMessage = "Incorrect password."
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF263E75))
            ) {
                Text("Unlock App", fontSize = 18.sp, color = Color.White, fontWeight = FontWeight.Bold)
            }
        }
    }
}

@Composable
fun AuthenticatedApp() {
    val context = androidx.compose.ui.platform.LocalContext.current
    val prefs = remember { context.getSharedPreferences("profile_prefs", Context.MODE_PRIVATE) }
    
    // Check for banned user
    val accountName = prefs.getString("accountName", "") ?: ""
    val isBanned = TimetableData.bannedUsers.contains(accountName)
    
    if (isBanned) {
        BannedScreen()
    } else {
        MainAppContent()
    }
}

@Composable
fun BannedScreen() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(24.dp)
        ) {
            Icon(
                imageVector = Icons.Filled.Person,
                contentDescription = "Developer",
                modifier = Modifier.size(100.dp),
                tint = Color.White
            )
            Spacer(modifier = Modifier.height(24.dp))
            Text(
                text = "your account has been banned by the app developer\nno timetable no switching account\nwant to use teb app re install and create a new account",
                color = Color.Red,
                fontSize = 20.sp,
                textAlign = TextAlign.Center,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
fun MainAppContent() {
    val context = androidx.compose.ui.platform.LocalContext.current
    val permissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean -> }

    LaunchedEffect(Unit) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ActivityCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
                permissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
            }
        }
    }

    val prefs = remember { context.getSharedPreferences("profile_prefs", Context.MODE_PRIVATE) }
    val profileRole = prefs.getString("role", null)
    val profileDetail = prefs.getString("detail", null)

    LaunchedEffect(TimetableData.timetableUpdateTrigger.value) {
        if (TimetableData.timetableUpdateTrigger.value > 0) {
            var notificationTitle = "Timetable Updated"
            var notificationText = "A new timetable has been generated."
            var shouldNotify = false
            
            if (profileRole == "Student" && profileDetail != null) {
                val userClass = profileDetail.removePrefix("Class: ").trim()
                val classNotifs = TimetableData.classNotifications.filter { it.targetClass == userClass }
                if (classNotifs.isNotEmpty()) {
                    val latest = classNotifs.maxByOrNull { it.timestamp }
                    if (latest != null) {
                        notificationTitle = "Class $userClass Timetable Update"
                        notificationText = latest.message
                        shouldNotify = true
                    }
                }
            } else if (profileRole == "Admin" || profileRole == "Developer") {
                // Admins/Developers can see generic notification
                shouldNotify = true
            }

            if (shouldNotify) {
                if (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU || ActivityCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS) == PackageManager.PERMISSION_GRANTED) {
                    val builder = NotificationCompat.Builder(context, "timetable_channel")
                        .setSmallIcon(android.R.drawable.ic_dialog_info)
                        .setContentTitle(notificationTitle)
                        .setContentText(notificationText)
                        .setStyle(NotificationCompat.BigTextStyle().bigText(notificationText))
                        .setPriority(NotificationCompat.PRIORITY_HIGH)
                        .setAutoCancel(true)

                    with(NotificationManagerCompat.from(context)) {
                        notify(101, builder.build())
                    }
                }
            }
        }
    }

    val navController = rememberNavController()
    val items = listOf(
        BottomNavItem.Timetable,
        BottomNavItem.Notifications,
        BottomNavItem.Profile
    )

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        bottomBar = {
            val navBackStackEntry by navController.currentBackStackEntryAsState()
            val currentRoute = navBackStackEntry?.destination?.route
            val showBottomBar = currentRoute in items.map { it.route }
            
            if (showBottomBar) {
                NavigationBar(
                    containerColor = Color(0xFFFAF9F2),
                    tonalElevation = 0.dp
                ) {
                    items.forEach { item ->
                        NavigationBarItem(
                            icon = { Icon(item.icon, contentDescription = item.title) },
                            label = { Text(item.title) },
                            selected = currentRoute == item.route,
                            onClick = {
                                navController.navigate(item.route) {
                                    popUpTo(navController.graph.startDestinationId) {
                                        saveState = true
                                    }
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            },
                            colors = NavigationBarItemDefaults.colors(
                                indicatorColor = Color(0xFFDCE2FA),
                                selectedIconColor = Color(0xFF172047),
                                unselectedIconColor = Color(0xFF94A3B8),
                                selectedTextColor = Color(0xFF172047),
                                unselectedTextColor = Color(0xFF94A3B8)
                            )
                        )
                    }
                }
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = BottomNavItem.Timetable.route,
            modifier = Modifier.padding(innerPadding)
        ) {
            composable(BottomNavItem.Timetable.route) {
                ClassesScreen(
                    onClassSelected = { className ->
                        navController.navigate("timetable/$className")
                    }
                )
            }
            composable(BottomNavItem.Notifications.route) {
                NotificationScreen()
            }
            composable(BottomNavItem.Profile.route) {
                ProfileScreen()
            }
            composable("timetable/{className}") { backStackEntry ->
                val className = backStackEntry.arguments?.getString("className") ?: ""
                TimetableScreen(
                    className = className,
                    onNavigateBack = { navController.popBackStack() }
                )
            }
        }
    }
}
