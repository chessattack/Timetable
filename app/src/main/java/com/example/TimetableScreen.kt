package com.example

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.*
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.draw.shadow

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TimetableScreen(
    className: String,
    onNavigateBack: () -> Unit
) {
    val context = LocalContext.current
    val prefs = remember { context.getSharedPreferences("profile_prefs", android.content.Context.MODE_PRIVATE) }
    val role = prefs.getString("role", "Student")
    val isAdmin = role == "Admin" || role == "Developer"

    var currentSchedule by remember(className) {
        mutableStateOf(
            TimetableData.getScheduleForClass(className).map { day ->
                day.copy(periods = day.periods.map { it.copy() })
            }
        )
    }

    var selectedDay by remember { mutableStateOf("Monday") }
    val days = listOf("Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday")
    val selectedTabIndex = days.indexOf(selectedDay)

    val periodColors = listOf(
        Color(0xFFEE5865), // 1 Coral/red
        Color(0xFFF28A12), // 2 Orange
        Color(0xFFF2DA48), // 3 Yellow
        Color(0xFF8ACB42), // 4 Lime/green
        Color(0xFF2999C7), // 5 Cyan/blue
        Color(0xFF294FBE), // 6 Deep royal blue
        Color(0xFFA51D79), // 7 Magenta/purple
        Color(0xFF718CA0)  // 8 Muted blue-grey
    )
    val TabBgColor = Color(0xFF454B59) // Dark charcoal/blue-grey
    val PrimaryNavy = Color(0xFF263E75)
    val TextNavy = Color(0xFF172047)

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        bottomBar = {
            if (isAdmin) {
                Box(modifier = Modifier.padding(16.dp)) {
                    Button(
                        onClick = {
                            TimetableData.saveSchedule(className, currentSchedule)
                            android.widget.Toast.makeText(context, "Timetable Updated Successfully!", android.widget.Toast.LENGTH_SHORT).show()
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(56.dp),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = PrimaryNavy)
                    ) {
                        Text("Submit Changes", color = Color.White, fontSize = 18.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 24.dp)
        ) {
            Spacer(modifier = Modifier.height(32.dp))
            
            // Header Row
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Start
            ) {
                Icon(
                    imageVector = Icons.Default.ArrowBack,
                    contentDescription = "Back",
                    tint = TextNavy,
                    modifier = Modifier
                        .size(24.dp)
                        .clickable { onNavigateBack() }
                )
                Spacer(modifier = Modifier.width(16.dp))
                Column {
                    Text(
                        text = "Class $className",
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontFamily.Serif,
                        fontSize = 36.sp,
                        color = TextNavy
                    )
                    Text(
                        text = "Timetable",
                        fontSize = 16.sp,
                        color = Color(0xFF767676)
                    )
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .padding(bottom = 16.dp)
                    .shadow(2.dp, RoundedCornerShape(16.dp), spotColor = Color(0x22000000))
            ) {
                // Days Tabs - Dark Top Header
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp))
                        .background(TabBgColor)
                ) {
                    ScrollableTabRow(
                        selectedTabIndex = selectedTabIndex,
                        containerColor = TabBgColor,
                        contentColor = Color.White,
                        edgePadding = 0.dp,
                        divider = {},
                        indicator = { tabPositions ->
                            if (selectedTabIndex < tabPositions.size) {
                                TabRowDefaults.Indicator(
                                    Modifier.tabIndicatorOffset(tabPositions[selectedTabIndex]),
                                    color = Color.White,
                                    height = 3.dp
                                )
                            }
                        }
                    ) {
                        days.forEachIndexed { index, day ->
                            Tab(
                                selected = selectedTabIndex == index,
                                onClick = { selectedDay = day },
                                text = {
                                    Text(
                                        text = day,
                                        color = if (selectedTabIndex == index) Color.White else Color(0xFFB0B4C0),
                                        fontWeight = if (selectedTabIndex == index) FontWeight.Bold else FontWeight.Normal,
                                        fontSize = 16.sp
                                    )
                                }
                            )
                        }
                    }
                }

                // Periods List
                val currentDaySchedule = currentSchedule.find { it.dayName == selectedDay }
                val dayIndex = currentSchedule.indexOfFirst { it.dayName == selectedDay }
                
                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                        .clip(RoundedCornerShape(bottomStart = 16.dp, bottomEnd = 16.dp)),
                    verticalArrangement = Arrangement.spacedBy(0.dp)
                ) {
                    currentDaySchedule?.periods?.let { periods ->
                        items(periods.size) { periodIndex ->
                            val period = periods[periodIndex]
                            val bgColor = periodColors[periodIndex % periodColors.size]
                            val isYellow = periodIndex == 2
                            val contentColor = if (isYellow) Color(0xFF1C2857) else Color.White
                            
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(64.dp)
                                    .background(bgColor)
                                    .padding(horizontal = 24.dp)
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier.fillMaxSize()
                                ) {
                                    // Period Number
                                    Text(
                                        text = "${periodIndex + 1}",
                                        fontSize = 24.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = contentColor,
                                        modifier = Modifier.width(48.dp)
                                    )
                                    // Times
                                    val startTime = String.format("%02d:30", 8 + periodIndex)
                                    val endTime = String.format("%02d:15", 9 + periodIndex)
                                    val timeStr = "$startTime\n$endTime"
                                    Text(
                                        text = timeStr,
                                        fontSize = 12.sp,
                                        color = contentColor,
                                        lineHeight = 16.sp,
                                        textAlign = TextAlign.Center,
                                        modifier = Modifier.width(64.dp)
                                    )
                                    Spacer(modifier = Modifier.width(16.dp))
                                    
                                    // Subject/Class details
                                    Column(verticalArrangement = Arrangement.Center) {
                                        if (isAdmin) {
                                            var subjectExpanded by remember { mutableStateOf(false) }
                                            Box {
                                                Row(
                                                    verticalAlignment = Alignment.CenterVertically,
                                                    modifier = Modifier.clickable { subjectExpanded = true }
                                                ) {
                                                    Text(
                                                        text = period.title.ifEmpty { "Subject" },
                                                        fontSize = 18.sp,
                                                        fontWeight = FontWeight.Bold,
                                                        color = contentColor
                                                    )
                                                    Icon(Icons.Default.ArrowDropDown, contentDescription = null, tint = contentColor)
                                                }
                                                DropdownMenu(expanded = subjectExpanded, onDismissRequest = { subjectExpanded = false }) {
                                                    TimetableData.subjects.forEach { subj ->
                                                        DropdownMenuItem(text = { Text(subj) }, onClick = {
                                                            val newSchedule = currentSchedule.toMutableList()
                                                            val newDay = newSchedule[dayIndex].copy()
                                                            val newPeriods = newDay.periods.toMutableList()
                                                            newPeriods[periodIndex] = period.copy(title = subj, subtitle = "")
                                                            newSchedule[dayIndex] = newDay.copy(periods = newPeriods)
                                                            currentSchedule = newSchedule
                                                            subjectExpanded = false
                                                        })
                                                    }
                                                }
                                            }

                                            var teacherExpanded by remember { mutableStateOf(false) }
                                            Box {
                                                Row(
                                                    verticalAlignment = Alignment.CenterVertically,
                                                    modifier = Modifier.clickable { teacherExpanded = true }
                                                ) {
                                                    Text(
                                                        text = period.subtitle.ifEmpty { "Teacher" },
                                                        fontSize = 14.sp,
                                                        color = contentColor
                                                    )
                                                    Icon(Icons.Default.ArrowDropDown, contentDescription = null, tint = contentColor)
                                                }
                                                DropdownMenu(expanded = teacherExpanded, onDismissRequest = { teacherExpanded = false }) {
                                                    TimetableData.teachersForSubject(period.title).forEach { t ->
                                                        DropdownMenuItem(text = { Text(t) }, onClick = {
                                                            val newSchedule = currentSchedule.toMutableList()
                                                            val newDay = newSchedule[dayIndex].copy()
                                                            val newPeriods = newDay.periods.toMutableList()
                                                            newPeriods[periodIndex] = period.copy(subtitle = t)
                                                            newSchedule[dayIndex] = newDay.copy(periods = newPeriods)
                                                            currentSchedule = newSchedule
                                                            teacherExpanded = false
                                                        })
                                                    }
                                                }
                                            }
                                        } else {
                                            Text(
                                                text = period.title.ifEmpty { "Free Period" },
                                                fontSize = 18.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = contentColor
                                            )
                                            if (period.title.isNotEmpty() && period.subtitle.isNotEmpty()) {
                                                Text(
                                                    text = period.subtitle,
                                                    fontSize = 14.sp,
                                                    color = contentColor
                                                )
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
