package com.example

import android.content.Context
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun NotificationScreen() {
    val context = LocalContext.current
    val prefs = remember { context.getSharedPreferences("profile_prefs", Context.MODE_PRIVATE) }
    val profileRole = prefs.getString("role", null)
    val profileDetail = prefs.getString("detail", null)

    var userClass by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(profileRole, profileDetail) {
        if (profileRole == "Student" && profileDetail != null) {
            userClass = profileDetail.removePrefix("Class: ").trim()
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFFAF9F2))
            .padding(16.dp)
    ) {
        Text(
            text = "Notifications",
            fontSize = 32.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF172047),
            modifier = Modifier.padding(bottom = 24.dp, top = 24.dp)
        )

        if (profileRole == "Student" && userClass != null) {
            val relevantNotifications = TimetableData.classNotifications.filter {
                it.targetClass == userClass
            }.sortedByDescending { it.timestamp }

            if (relevantNotifications.isEmpty()) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text(
                        text = "No new notifications for your class.",
                        fontSize = 16.sp,
                        color = Color(0xFF64748B)
                    )
                }
            } else {
                LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    items(relevantNotifications) { notification ->
                        NotificationCard(notification)
                    }
                }
            }
        } else {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text(
                    text = "Notifications are currently active for Student profiles to see class-specific updates.",
                    fontSize = 16.sp,
                    color = Color(0xFF64748B),
                    textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                    modifier = Modifier.padding(32.dp)
                )
            }
        }
    }
}

@Composable
fun NotificationCard(notification: NotificationItem) {
    val sdf = remember { SimpleDateFormat("hh:mm a", Locale.getDefault()) }
    val timeString = sdf.format(Date(notification.timestamp))

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Timetable Update",
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                    color = Color(0xFF172047)
                )
                Text(
                    text = timeString,
                    fontSize = 12.sp,
                    color = Color(0xFF94A3B8)
                )
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = notification.message,
                fontSize = 14.sp,
                color = Color(0xFF334155),
                lineHeight = 20.sp
            )
        }
    }
}
