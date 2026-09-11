package com.example

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ScrollableTabRow
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.draw.shadow

@Composable
fun TeacherScheduleGrid(teacherName: String) {
    val currentSchedule by remember(teacherName, TimetableData.classes) {
        derivedStateOf {
            val days = listOf("Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday")
            days.map { dayName ->
                val periods = mutableListOf<Period>()
                for (pIndex in 0 until 8) {
                    var assignedClass = ""
                    var assignedSubject = "Free"
                    for (className in TimetableData.classes) {
                        val classSch = TimetableData.getScheduleForClass(className)
                        val daySch = classSch.find { it.dayName == dayName }
                        val period = daySch?.periods?.getOrNull(pIndex)
                        if (period?.subtitle?.trim().equals(teacherName, ignoreCase = true) && teacherName.isNotEmpty()) {
                            assignedClass = className
                            assignedSubject = period?.title ?: ""
                            break
                        }
                    }
                    val startTime = String.format("%02d:30", 8 + pIndex)
                    val endTime = String.format("%02d:15", 9 + pIndex)
                    val timeStr = "$startTime\n$endTime"
                    
                    periods.add(
                        Period(
                            title = assignedClass,
                            subtitle = assignedSubject,
                            time = timeStr,
                            periodName = "P${pIndex + 1}"
                        )
                    )
                }
                DaySchedule(dayName = dayName, periods = periods)
            }
        }
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

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .height(580.dp)
            .shadow(2.dp, RoundedCornerShape(16.dp), spotColor = Color(0x22000000))
    ) {
        if (teacherName.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text(
                    text = "Teacher name not found.",
                    fontSize = 16.sp,
                    color = Color.Gray,
                    textAlign = TextAlign.Center
                )
            }
        } else {
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
                    divider = {}, // Remove divider line
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
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .clip(RoundedCornerShape(bottomStart = 16.dp, bottomEnd = 16.dp)),
                verticalArrangement = Arrangement.spacedBy(0.dp)
            ) {
                currentDaySchedule?.periods?.let { periods ->
                    items(periods.size) { index ->
                        val period = periods[index]
                        val bgColor = periodColors[index % periodColors.size]
                        val isYellow = index == 2 // 3rd period is yellow
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
                                    text = "${index + 1}",
                                    fontSize = 24.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = contentColor,
                                    modifier = Modifier.width(48.dp)
                                )
                                // Times
                                Text(
                                    text = period.time,
                                    fontSize = 12.sp,
                                    color = contentColor,
                                    lineHeight = 16.sp,
                                    textAlign = TextAlign.Center,
                                    modifier = Modifier.width(64.dp)
                                )
                                Spacer(modifier = Modifier.width(16.dp))
                                // Subject/Class details
                                Column(verticalArrangement = Arrangement.Center) {
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
