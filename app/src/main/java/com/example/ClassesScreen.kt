package com.example

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.outlined.AccountCircle
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.draw.shadow

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ClassesScreen(
    onClassSelected: (String) -> Unit
) {
    var searchQuery by remember { mutableStateOf("") }
    val filteredClasses = TimetableData.classes.filter {
        it.contains(searchQuery, ignoreCase = true)
    }

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background
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
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.ArrowBack,
                        contentDescription = "Back",
                        tint = Color(0xFF172047),
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.width(16.dp))
                    Column {
                        Text(
                            text = "Classes",
                            fontWeight = FontWeight.Bold,
                            fontFamily = FontFamily.Serif,
                            fontSize = 42.sp,
                            color = Color(0xFF172047)
                        )
                        Text(
                            text = "Select a class to view timetable",
                            fontSize = 16.sp,
                            color = Color(0xFF767676)
                        )
                    }
                }
                Icon(
                    imageVector = Icons.Outlined.AccountCircle,
                    contentDescription = "Profile",
                    tint = Color(0xFF172047),
                    modifier = Modifier.size(36.dp)
                )
            }

            Spacer(modifier = Modifier.height(32.dp))

            // Search Bar
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                modifier = Modifier.fillMaxWidth().height(56.dp),
                placeholder = { 
                    Text(
                        "Search class (e.g. 1A, 12E...)",
                        color = Color(0xFF767676),
                        fontSize = 16.sp
                    ) 
                },
                leadingIcon = { 
                    Icon(
                        Icons.Default.Search, 
                        contentDescription = "Search",
                        tint = Color(0xFF767676)
                    ) 
                },
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedContainerColor = Color(0xFFF1F1F1),
                    unfocusedContainerColor = Color(0xFFF1F1F1),
                    unfocusedBorderColor = Color(0xFFA8A8A8),
                    focusedBorderColor = Color(0xFF172047),
                    cursorColor = Color(0xFF172047)
                ),
                singleLine = true
            )
            
            Spacer(modifier = Modifier.height(24.dp))

            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(16.dp),
                contentPadding = PaddingValues(bottom = 80.dp)
            ) {
                items(filteredClasses) { className ->
                    ClassCard(className = className, onClick = { onClassSelected(className) })
                }
            }
        }
    }
}

@Composable
fun ClassCard(className: String, onClick: () -> Unit) {
    val gradientBrush = Brush.horizontalGradient(
        colors = listOf(
            Color(0xFFE2F4ED), // Very pale mint
            Color(0xFFFDF1E4)  // Very pale cream
        )
    )

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(86.dp)
            .shadow(2.dp, RoundedCornerShape(16.dp), spotColor = Color(0x11000000))
            .clip(RoundedCornerShape(16.dp))
            .background(gradientBrush)
            .clickable { onClick() }
            .padding(horizontal = 24.dp),
        contentAlignment = Alignment.CenterStart
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = className,
                fontSize = 32.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF27337A) // Dark navy
            )
            Text(
                text = ">",
                fontSize = 24.sp,
                fontWeight = FontWeight.Light,
                color = Color(0xFF767676) // Medium grey
            )
        }
    }
}
