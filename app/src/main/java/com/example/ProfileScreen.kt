package com.example

import android.content.Context
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import androidx.compose.ui.draw.shadow
import androidx.compose.material.icons.outlined.Lock
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material.icons.outlined.DateRange
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material.icons.outlined.List
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.material.icons.outlined.AccountCircle
import android.content.Intent
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import kotlinx.coroutines.launch
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen() {
    val context = LocalContext.current
    val prefs = remember { context.getSharedPreferences("profile_prefs", Context.MODE_PRIVATE) }

    var profileRole by remember { mutableStateOf(prefs.getString("role", null)) }
    var profileDetail by remember { mutableStateOf(prefs.getString("detail", null)) }
    var profileImageUri by remember { mutableStateOf(prefs.getString("profileImageUri", null)) }
    var arrangementSheet by remember { mutableStateOf<ArrangementSheetData?>(null) }
    var showAttendanceHistory by remember { mutableStateOf(false) }

    if (showAttendanceHistory) {
        StaffAttendanceHistoryScreen(onBack = { showAttendanceHistory = false })
        return
    }

    val photoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia(),
        onResult = { uri ->
            if (uri != null) {
                try {
                    val flag = android.content.Intent.FLAG_GRANT_READ_URI_PERMISSION
                    context.contentResolver.takePersistableUriPermission(uri, flag)
                } catch (e: Exception) {
                    e.printStackTrace()
                }
                prefs.edit().putString("profileImageUri", uri.toString()).apply()
                profileImageUri = uri.toString()
            }
        }
    )

    var currentStep by remember {
        mutableStateOf(if (profileRole != null) "CREATED" else "START")
    }
    var selectedRole by remember { mutableStateOf("") }
    var detailInput1 by remember { mutableStateOf("") }
    var detailInput2 by remember { mutableStateOf("") }
    var errorMessage by remember { mutableStateOf("") }

    val PrimaryNavy = Color(0xFF263E75)
    val TextNavy = Color(0xFF172047)
    val BorderNavy = Color(0xFF1D285D)

    fun saveProfile(role: String, detail: String, accountName: String) {
        prefs.edit().putString("role", role).putString("detail", detail).putString("accountName", accountName).apply()
        profileRole = role
        profileDetail = detail
        currentStep = "CREATED"
    }

    fun clearProfile() {
        prefs.edit().clear().apply()
        profileRole = null
        profileDetail = null
        profileImageUri = null
        currentStep = "START"
        selectedRole = ""
        detailInput1 = ""
        detailInput2 = ""
        errorMessage = ""
    }

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background
    ) { paddingValues ->
        Box(modifier = Modifier.fillMaxSize()) {
            // Background Illustration layer
            if (currentStep != "CREATED") {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.BottomCenter
                ) {
                    Icon(
                        imageVector = Icons.Outlined.AccountCircle,
                        contentDescription = null,
                        tint = Color.LightGray.copy(alpha = 0.2f),
                        modifier = Modifier
                            .size(150.dp)
                            .offset(y = (-40).dp, x = (-40).dp)
                    )
                    Icon(
                        imageVector = Icons.Outlined.Settings,
                        contentDescription = null,
                        tint = Color.LightGray.copy(alpha = 0.2f),
                        modifier = Modifier
                            .size(100.dp)
                            .offset(y = (-120).dp, x = 60.dp)
                    )
                    Icon(
                        imageVector = Icons.Outlined.List,
                        contentDescription = null,
                        tint = Color.LightGray.copy(alpha = 0.2f),
                        modifier = Modifier
                            .size(120.dp)
                            .offset(y = (-180).dp, x = (-20).dp)
                    )
                }
            }

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Spacer(modifier = Modifier.height(32.dp))
                
                // Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Start
                ) {
                    Text(
                        text = "Profile",
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontFamily.Serif,
                        fontSize = 42.sp,
                        color = TextNavy
                    )
                }

                Spacer(modifier = Modifier.height(48.dp))

                if (currentStep != "CREATED") {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .shadow(2.dp, RoundedCornerShape(28.dp), spotColor = Color(0x22000000)),
                        shape = RoundedCornerShape(28.dp),
                        colors = CardDefaults.cardColors(containerColor = Color(0xFFFAFAFA))
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(32.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            when (currentStep) {
                                "START" -> {
                                    // Custom composed icon for start screen
                                    Box(contentAlignment = Alignment.Center, modifier = Modifier.size(120.dp)) {
                                        Icon(
                                            imageVector = Icons.Filled.Person,
                                            contentDescription = "Profile",
                                            modifier = Modifier.size(120.dp),
                                            tint = PrimaryNavy
                                        )
                                    }
                                    Spacer(modifier = Modifier.height(32.dp))
                                    Text(
                                        "You don't have a profile yet.",
                                        fontSize = 18.sp,
                                        color = Color(0xFF555555),
                                        textAlign = TextAlign.Center
                                    )
                                    Spacer(modifier = Modifier.height(40.dp))
                                    Button(
                                        onClick = { currentStep = "ROLE" },
                                        modifier = Modifier.fillMaxWidth().height(56.dp),
                                        shape = RoundedCornerShape(12.dp),
                                        colors = ButtonDefaults.buttonColors(containerColor = PrimaryNavy)
                                    ) {
                                        Text("Create a Profile", fontSize = 18.sp, color = Color.White, fontWeight = FontWeight.Bold)
                                    }
                                }

                                "ROLE" -> {
                                    Text(
                                        "Choose your role",
                                        fontSize = 24.sp,
                                        color = Color(0xFF767676),
                                        modifier = Modifier.padding(bottom = 40.dp)
                                    )

                                    val roles = listOf("Student", "Teacher", "Admin")
                                    roles.forEach { role ->
                                        OutlinedButton(
                                            onClick = {
                                                selectedRole = role
                                                currentStep = role.uppercase()
                                                detailInput1 = ""
                                                detailInput2 = ""
                                                errorMessage = ""
                                            },
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .height(56.dp)
                                                .padding(vertical = 8.dp),
                                            shape = RoundedCornerShape(32.dp),
                                            border = BorderStroke(1.5.dp, BorderNavy),
                                            colors = ButtonDefaults.outlinedButtonColors(containerColor = Color.Transparent)
                                        ) {
                                            Text(role, fontSize = 18.sp, color = BorderNavy, fontWeight = FontWeight.Bold)
                                        }
                                    }
                                    Spacer(modifier = Modifier.height(16.dp))
                                }

                                "STUDENT" -> {
                                    Text("Student Profile", fontSize = 28.sp, color = TextNavy)
                                    Spacer(modifier = Modifier.height(40.dp))
                                    OutlinedTextField(
                                        value = detailInput1,
                                        onValueChange = { detailInput1 = it },
                                        placeholder = { Text("Which class are you from? (e.g. 1A)", color = Color(0xFF999999)) },
                                        modifier = Modifier.fillMaxWidth(),
                                        shape = RoundedCornerShape(12.dp),
                                        colors = OutlinedTextFieldDefaults.colors(
                                            unfocusedContainerColor = Color.Transparent,
                                            focusedContainerColor = Color.Transparent,
                                            unfocusedBorderColor = Color(0xFFA8A8A8),
                                            focusedBorderColor = PrimaryNavy
                                        )
                                    )
                                    Spacer(modifier = Modifier.height(40.dp))
                                    Button(
                                        onClick = {
                                            if (detailInput1.isNotBlank()) {
                                                saveProfile("Student", "Class: $detailInput1", "Student $detailInput1")
                                            } else {
                                                errorMessage = "Please enter your class"
                                            }
                                        },
                                        modifier = Modifier.fillMaxWidth().height(56.dp),
                                        shape = RoundedCornerShape(28.dp),
                                        colors = ButtonDefaults.buttonColors(containerColor = PrimaryNavy)
                                    ) {
                                        Text("Done", fontSize = 18.sp, color = Color.White, fontWeight = FontWeight.Bold)
                                    }
                                    if (errorMessage.isNotEmpty()) {
                                        Text(errorMessage, color = Color.Red, modifier = Modifier.padding(top = 16.dp))
                                    }
                                }

                                "TEACHER" -> {
                                    Text("Teacher Profile", fontSize = 28.sp, color = TextNavy)
                                    Spacer(modifier = Modifier.height(40.dp))
                                    
                                    var expanded by remember { mutableStateOf(false) }
                                    
                                    ExposedDropdownMenuBox(
                                        expanded = expanded,
                                        onExpandedChange = { expanded = it }
                                    ) {
                                        OutlinedTextField(
                                            value = detailInput1,
                                            onValueChange = {},
                                            readOnly = true,
                                            placeholder = { Text("Select your name", color = Color(0xFF999999)) },
                                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                                            modifier = Modifier.fillMaxWidth().menuAnchor(),
                                            shape = RoundedCornerShape(12.dp),
                                            colors = OutlinedTextFieldDefaults.colors(
                                                unfocusedContainerColor = Color.Transparent,
                                                focusedContainerColor = Color.Transparent,
                                                unfocusedBorderColor = Color(0xFFA8A8A8),
                                                focusedBorderColor = PrimaryNavy
                                            )
                                        )
                                        ExposedDropdownMenu(
                                            expanded = expanded,
                                            onDismissRequest = { expanded = false }
                                        ) {
                                            TimetableData.allPreRegisteredTeachers.forEach { teacher ->
                                                DropdownMenuItem(
                                                    text = { Text(teacher) },
                                                    onClick = {
                                                        detailInput1 = teacher
                                                        expanded = false
                                                    }
                                                )
                                            }
                                        }
                                    }

                                    Spacer(modifier = Modifier.height(24.dp))
                                    OutlinedTextField(
                                        value = detailInput2,
                                        onValueChange = { detailInput2 = it },
                                        placeholder = { Text("Enter password", color = Color(0xFF999999)) },
                                        trailingIcon = { Icon(Icons.Outlined.Lock, contentDescription = null, tint = Color.Gray) },
                                        visualTransformation = PasswordVisualTransformation(),
                                        modifier = Modifier.fillMaxWidth(),
                                        shape = RoundedCornerShape(12.dp),
                                        colors = OutlinedTextFieldDefaults.colors(
                                            unfocusedContainerColor = Color.Transparent,
                                            focusedContainerColor = Color.Transparent,
                                            unfocusedBorderColor = Color(0xFFA8A8A8),
                                            focusedBorderColor = PrimaryNavy
                                        )
                                    )
                                    Spacer(modifier = Modifier.height(40.dp))
                                    Button(
                                        onClick = {
                                            if (detailInput1.isBlank()) {
                                                errorMessage = "Please select your name"
                                            } else if (detailInput2.trim() == "123") {
                                                val subject = TimetableData.getSubjectForTeacher(detailInput1)
                                                saveProfile("Teacher", "Name: $detailInput1\nSubject: $subject", detailInput1)
                                            } else {
                                                errorMessage = "Incorrect password"
                                            }
                                        },
                                        modifier = Modifier.fillMaxWidth().height(56.dp),
                                        shape = RoundedCornerShape(28.dp),
                                        colors = ButtonDefaults.buttonColors(containerColor = PrimaryNavy)
                                    ) {
                                        Text("Done", fontSize = 18.sp, color = Color.White, fontWeight = FontWeight.Bold)
                                    }
                                    if (errorMessage.isNotEmpty()) {
                                        Text(errorMessage, color = Color.Red, modifier = Modifier.padding(top = 16.dp))
                                    }
                                }

                                "ADMIN" -> {
                                    Text("Admin Profile", fontSize = 28.sp, fontFamily = FontFamily.Serif, color = TextNavy)
                                    Spacer(modifier = Modifier.height(40.dp))
                                    OutlinedTextField(
                                        value = detailInput1,
                                        onValueChange = { detailInput1 = it },
                                        placeholder = { Text("Enter Admin ID (e.g. Admin 1)", color = Color(0xFF999999)) },
                                        trailingIcon = { Icon(Icons.Outlined.AccountCircle, contentDescription = null, tint = Color.Gray) },
                                        modifier = Modifier.fillMaxWidth(),
                                        shape = RoundedCornerShape(12.dp),
                                        colors = OutlinedTextFieldDefaults.colors(
                                            unfocusedContainerColor = Color.Transparent,
                                            focusedContainerColor = Color.Transparent,
                                            unfocusedBorderColor = Color(0xFFA8A8A8),
                                            focusedBorderColor = PrimaryNavy
                                        )
                                    )
                                    Spacer(modifier = Modifier.height(24.dp))
                                    OutlinedTextField(
                                        value = detailInput2,
                                        onValueChange = { detailInput2 = it },
                                        placeholder = { Text("Enter password", color = Color(0xFF999999)) },
                                        trailingIcon = { Icon(Icons.Outlined.Lock, contentDescription = null, tint = Color.Gray) },
                                        visualTransformation = PasswordVisualTransformation(),
                                        modifier = Modifier.fillMaxWidth(),
                                        shape = RoundedCornerShape(12.dp),
                                        colors = OutlinedTextFieldDefaults.colors(
                                            unfocusedContainerColor = Color.Transparent,
                                            focusedContainerColor = Color.Transparent,
                                            unfocusedBorderColor = Color(0xFFA8A8A8),
                                            focusedBorderColor = PrimaryNavy
                                        )
                                    )
                                    Spacer(modifier = Modifier.height(40.dp))
                                    Button(
                                        onClick = {
                                            if (detailInput1.isBlank()) {
                                                errorMessage = "Please enter your Admin ID"
                                            } else if (detailInput1.trim().lowercase() == "vikranth" && detailInput2.trim() == "Poorvika@2021") {
                                                saveProfile("Developer", "ID: vikranth", "vikranth")
                                            } else if (detailInput2.trim() == "123") {
                                                saveProfile("Admin", "ID: $detailInput1", detailInput1)
                                            } else {
                                                errorMessage = "Incorrect password"
                                            }
                                        },
                                        modifier = Modifier.fillMaxWidth().height(56.dp),
                                        shape = RoundedCornerShape(28.dp),
                                        colors = ButtonDefaults.buttonColors(containerColor = PrimaryNavy)
                                    ) {
                                        Text("Done", fontSize = 18.sp, color = Color.White, fontWeight = FontWeight.Bold)
                                    }
                                    if (errorMessage.isNotEmpty()) {
                                        Text(errorMessage, color = Color.Red, modifier = Modifier.padding(top = 16.dp))
                                    }
                                }
                            }
                        }
                    }
                } else {
                    // CREATED STATE
                    // Profile Photo
                    Box(
                        modifier = Modifier
                            .size(100.dp)
                            .clip(CircleShape)
                            .background(Color(0xFFE0E0E0))
                            .clickable {
                                photoPickerLauncher.launch(
                                    PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                                )
                            },
                        contentAlignment = Alignment.Center
                    ) {
                        if (profileImageUri != null) {
                            AsyncImage(
                                model = profileImageUri,
                                contentDescription = "Profile Picture",
                                modifier = Modifier.fillMaxSize(),
                                contentScale = ContentScale.Crop
                            )
                        } else {
                            Icon(
                                imageVector = Icons.Filled.Person,
                                contentDescription = "Add Photo",
                                modifier = Modifier.size(60.dp),
                                tint = Color(0xFF767676)
                            )
                        }
                    }
                    
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    // Specific profile details based on role
                    if (profileRole == "Student") {
                        val classStr = profileDetail?.removePrefix("Class: ")?.trim() ?: ""
                        Text(
                            text = "Student",
                            fontSize = 28.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextNavy
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Class $classStr",
                            fontSize = 18.sp,
                            color = Color(0xFF767676)
                        )
                    } else if (profileRole == "Teacher") {
                        val nameStr = profileDetail?.lines()?.find { it.startsWith("Name: ") }?.removePrefix("Name: ")?.trim() ?: ""
                        val subjectStr = profileDetail?.lines()?.find { it.startsWith("Subject: ") }?.removePrefix("Subject: ")?.trim() ?: ""
                        
                        Text(
                            text = nameStr,
                            fontSize = 28.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextNavy
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = subjectStr,
                            fontSize = 18.sp,
                            color = Color(0xFF767676)
                        )
                        
                        Spacer(modifier = Modifier.height(24.dp))
                        TeacherScheduleGrid(teacherName = nameStr)
                        
                        Spacer(modifier = Modifier.height(24.dp))
                        
                        var applyAbsent by remember { mutableStateOf(TimetableData.absentTeachers.contains(nameStr)) }
                        
                        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth()) {
                            Checkbox(
                                checked = applyAbsent, 
                                onCheckedChange = { applyAbsent = it },
                                colors = CheckboxDefaults.colors(
                                    checkedColor = PrimaryNavy,
                                    uncheckedColor = Color(0xFF767676),
                                    checkmarkColor = Color.White
                                )
                            )
                            Text("Apply for absentees for tomorrow", fontSize = 16.sp, color = Color(0xFF333333))
                        }
                        Spacer(modifier = Modifier.height(16.dp))
                        Button(
                            onClick = { 
                                if (applyAbsent && !TimetableData.absentTeachers.contains(nameStr)) {
                                    TimetableData.absentTeachers.add(nameStr)
                                    TimetableData.db.collection("absent_teachers").document(nameStr).set(mapOf("name" to nameStr))
                                    android.widget.Toast.makeText(context, "Leave application submitted successfully.", android.widget.Toast.LENGTH_SHORT).show()
                                } else if (!applyAbsent) {
                                    TimetableData.absentTeachers.remove(nameStr)
                                    TimetableData.db.collection("absent_teachers").document(nameStr).delete()
                                    android.widget.Toast.makeText(context, "Leave application cancelled.", android.widget.Toast.LENGTH_SHORT).show()
                                }
                            },
                            modifier = Modifier.fillMaxWidth().height(56.dp),
                            shape = RoundedCornerShape(16.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = PrimaryNavy)
                        ) {
                            Text("Apply", color = Color.White, fontSize = 18.sp, fontWeight = FontWeight.Bold)
                        }
                    } else if (profileRole == "Admin" || profileRole == "Developer") {
                        if (profileRole == "Developer") {
                            Text(
                                text = "Developer Dashboard",
                                fontSize = 28.sp,
                                fontWeight = FontWeight.Bold,
                                color = TextNavy
                            )
                        } else {
                            val idStr = profileDetail?.removePrefix("ID: ")?.trim() ?: ""
                            Text(
                                text = idStr,
                                fontSize = 28.sp,
                                fontWeight = FontWeight.Bold,
                                color = TextNavy
                            )
                        }
                        
                        Spacer(modifier = Modifier.height(32.dp))
                        
                        var showAdminConfirm by remember { mutableStateOf(false) }
                        val coroutineScope = rememberCoroutineScope()
                        val db = remember { AppDatabase.getDatabase(context) }
                        val moshi = remember { Moshi.Builder().addLast(KotlinJsonAdapterFactory()).build() }
                        val adapter = remember { moshi.adapter(ArrangementSheetData::class.java) }
                        
                        Button(
                            onClick = { 
                                val sheet = TimetableData.generateArrangementSheetData()
                                arrangementSheet = sheet
                                coroutineScope.launch {
                                    db.arrangementHistoryDao().insertHistory(
                                        ArrangementHistoryEntity(
                                            dateString = sheet.dateString,
                                            sheetDataJson = adapter.toJson(sheet)
                                        )
                                    )
                                }
                            },
                            modifier = Modifier.fillMaxWidth().height(56.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = PrimaryNavy),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Text("Auto-generate timetable", fontSize = 18.sp, color = Color.White)
                        }
                        
                        Spacer(modifier = Modifier.height(16.dp))
                        
                        Button(
                            onClick = { showAttendanceHistory = true },
                            modifier = Modifier.fillMaxWidth().height(56.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF64748B)),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Text("Staff Attendance History", fontSize = 18.sp, color = Color.White)
                        }
                        
                        Spacer(modifier = Modifier.height(32.dp))
                        
                        Text(
                            text = "Absent Teachers (Today)",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextNavy,
                            modifier = Modifier.fillMaxWidth()
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        
                        if (TimetableData.absentTeachers.isEmpty()) {
                            Text(
                                text = "No teachers are absent today.",
                                fontSize = 16.sp,
                                color = Color.Gray,
                                modifier = Modifier.fillMaxWidth()
                            )
                        } else {
                            Column(modifier = Modifier.fillMaxWidth()) {
                                TimetableData.absentTeachers.forEach { absentTeacherName ->
                                    Card(
                                        modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                                        colors = CardDefaults.cardColors(containerColor = Color(0xFFF1F5F9)),
                                        shape = RoundedCornerShape(8.dp)
                                    ) {
                                        Row(
                                            modifier = Modifier.padding(16.dp),
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Icon(
                                                imageVector = Icons.Default.Warning,
                                                contentDescription = null,
                                                tint = Color(0xFFE84356),
                                                modifier = Modifier.size(24.dp)
                                            )
                                            Spacer(modifier = Modifier.width(16.dp))
                                            Text(
                                                text = absentTeacherName,
                                                fontSize = 16.sp,
                                                fontWeight = FontWeight.Medium,
                                                color = Color(0xFF334155)
                                            )
                                        }
                                    }
                                }
                            }
                        }
                        
                        if (arrangementSheet != null) {
                            ArrangementSheetView(arrangementSheet!!)
                        }
                        
                        if (profileRole == "Developer") {
                            Spacer(modifier = Modifier.height(24.dp))
                            DeveloperDashboard()
                        }
                    }
                    
                    Spacer(modifier = Modifier.height(16.dp))
                    OutlinedButton(
                        onClick = { clearProfile() },
                        modifier = Modifier.fillMaxWidth().height(56.dp),
                        shape = RoundedCornerShape(16.dp),
                        border = BorderStroke(1.5.dp, Color(0xFFC86A6A)),
                        colors = ButtonDefaults.outlinedButtonColors(containerColor = Color(0xFFFAF9F2))
                    ) {
                        Text("Log Out", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = Color(0xFFB94B4B))
                    }
                    Spacer(modifier = Modifier.height(32.dp))
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DeveloperDashboard() {
    var searchQuery by remember { mutableStateOf("") }
    val PrimaryNavy = Color(0xFF263E75)
    val TextNavy = Color(0xFF172047)
    
    // Teacher Search
    OutlinedTextField(
        value = searchQuery,
        onValueChange = { searchQuery = it },
        placeholder = { Text("Search Teacher Timetable") },
        leadingIcon = { Icon(Icons.Outlined.Search, null) },
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp)
    )
    
    if (searchQuery.isNotBlank()) {
        val teacher = TimetableData.allPreRegisteredTeachers.find { it.contains(searchQuery, ignoreCase = true) }
        if (teacher != null) {
            Spacer(modifier = Modifier.height(16.dp))
            Text("Timetable for $teacher", fontWeight = FontWeight.Bold, color = TextNavy)
            Spacer(modifier = Modifier.height(8.dp))
            TeacherScheduleGrid(teacherName = teacher)
        }
    }
    
    Spacer(modifier = Modifier.height(32.dp))
    
    // Accounts list
    Text("All Accounts", fontWeight = FontWeight.Bold, fontSize = 20.sp, color = TextNavy)
    Spacer(modifier = Modifier.height(16.dp))
    
    TimetableData.allAccounts.forEach { account ->
        var showDialog by remember { mutableStateOf(false) }
        
        Card(
            modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp).clickable { showDialog = true },
            colors = CardDefaults.cardColors(containerColor = Color(0xFFFAFAFA))
        ) {
            Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Filled.Person, contentDescription = null, tint = PrimaryNavy, modifier = Modifier.size(40.dp))
                Spacer(modifier = Modifier.width(16.dp))
                Column {
                    Text(account.name, fontWeight = FontWeight.Bold)
                    Text(account.role, color = Color.Gray, fontSize = 14.sp)
                }
            }
        }
        
        if (showDialog) {
            androidx.compose.material3.AlertDialog(
                onDismissRequest = { showDialog = false },
                title = { Text("Account Info") },
                text = {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(Icons.Filled.Person, contentDescription = null, modifier = Modifier.size(60.dp))
                        Spacer(modifier = Modifier.height(8.dp))
                        Text("Name: ${account.name}", fontWeight = FontWeight.Bold)
                        Text("Role: ${account.role}")
                    }
                },
                confirmButton = {
                    val isBanned = TimetableData.bannedUsers.contains(account.name)
                    Button(
                        onClick = {
                            if (isBanned) {
                                TimetableData.bannedUsers.remove(account.name)
                                TimetableData.db.collection("banned_users").document(account.name).delete()
                            } else {
                                TimetableData.bannedUsers.add(account.name)
                                TimetableData.db.collection("banned_users").document(account.name).set(mapOf("name" to account.name))
                            }
                            showDialog = false
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = if (isBanned) Color.Green else Color.Red)
                    ) {
                        Text(if (isBanned) "Unban" else "Ban")
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showDialog = false }) { Text("Close") }
                }
            )
        }
    }
    
    Spacer(modifier = Modifier.height(32.dp))
    
    // Absentees
    Text("Absent Teachers", fontWeight = FontWeight.Bold, fontSize = 20.sp, color = TextNavy)
    Spacer(modifier = Modifier.height(16.dp))
    
    Text("Today", fontWeight = FontWeight.Bold, color = Color.Gray)
    if (TimetableData.absentTeachers.isEmpty()) Text("None", color = Color.Gray)
    TimetableData.absentTeachers.forEach { Text("- $it") }
    
    Spacer(modifier = Modifier.height(8.dp))
    Text("Yesterday", fontWeight = FontWeight.Bold, color = Color.Gray)
    if (TimetableData.absentTeachersYesterday.isEmpty()) Text("None", color = Color.Gray)
    TimetableData.absentTeachersYesterday.forEach { Text("- $it") }
    
    Spacer(modifier = Modifier.height(8.dp))
    Text("Day Before Yesterday", fontWeight = FontWeight.Bold, color = Color.Gray)
    if (TimetableData.absentTeachersDayBefore.isEmpty()) Text("None", color = Color.Gray)
    TimetableData.absentTeachersDayBefore.forEach { Text("- $it") }
}

@Composable
fun ArrangementSheetView(sheetData: ArrangementSheetData) {
    val context = LocalContext.current
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 24.dp)
            .background(Color(0xFFFFF9C4)) // Light yellow background
            .padding(16.dp)
            .border(2.dp, Color.Black)
    ) {
        Text(
            text = "TIME TABLE (2026-27)\nARRANGEMENT SHEET",
            fontWeight = FontWeight.Bold,
            fontSize = 20.sp,
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp)
        )
        Row(modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp), horizontalArrangement = Arrangement.SpaceBetween) {
            Text("DATE ${sheetData.dateString}", fontWeight = FontWeight.Bold)
        }

        // Table Header
        Row(
            modifier = Modifier.fillMaxWidth().border(1.dp, Color.Black)
        ) {
            Text("Sl.No", modifier = Modifier.weight(0.1f).border(1.dp, Color.Black).padding(4.dp), fontWeight = FontWeight.Bold)
            Text("TEACHER", modifier = Modifier.weight(0.2f).border(1.dp, Color.Black).padding(4.dp), fontWeight = FontWeight.Bold)
            for (i in 1..4) {
                Text("$i", modifier = Modifier.weight(0.1f).border(1.dp, Color.Black).padding(4.dp), textAlign = TextAlign.Center, fontWeight = FontWeight.Bold)
            }
            Text("RECESS", modifier = Modifier.weight(0.1f).border(1.dp, Color.Black).padding(4.dp), textAlign = TextAlign.Center, fontWeight = FontWeight.Bold)
            for (i in 5..8) {
                Text("$i", modifier = Modifier.weight(0.1f).border(1.dp, Color.Black).padding(4.dp), textAlign = TextAlign.Center, fontWeight = FontWeight.Bold)
            }
        }

        // Table Rows
        sheetData.rows.forEach { row ->
            Row(modifier = Modifier.fillMaxWidth().border(1.dp, Color.Black)) {
                Text("${row.slNo}", modifier = Modifier.weight(0.1f).border(1.dp, Color.Black).padding(4.dp))
                Text(row.absentTeacherName, modifier = Modifier.weight(0.2f).border(1.dp, Color.Black).padding(4.dp))
                for (i in 0..3) {
                    Text(row.periodAssignments[i] ?: "", modifier = Modifier.weight(0.1f).border(1.dp, Color.Black).padding(4.dp), fontSize = 10.sp)
                }
                Text("", modifier = Modifier.weight(0.1f).border(1.dp, Color.Black).padding(4.dp)) // Recess column
                for (i in 4..7) {
                    Text(row.periodAssignments[i] ?: "", modifier = Modifier.weight(0.1f).border(1.dp, Color.Black).padding(4.dp), fontSize = 10.sp)
                }
            }
        }

        // Fill empty rows to make it look like a sheet (e.g., minimum 5 rows)
        val emptyRowsNeeded = maxOf(0, 5 - sheetData.rows.size)
        for (emptyIdx in 1..emptyRowsNeeded) {
            Row(modifier = Modifier.fillMaxWidth().border(1.dp, Color.Black)) {
                Text("", modifier = Modifier.weight(0.1f).border(1.dp, Color.Black).padding(4.dp))
                Text("", modifier = Modifier.weight(0.2f).border(1.dp, Color.Black).padding(4.dp))
                for (i in 0..3) {
                    Text("", modifier = Modifier.weight(0.1f).border(1.dp, Color.Black).padding(4.dp))
                }
                Text("", modifier = Modifier.weight(0.1f).border(1.dp, Color.Black).padding(4.dp))
                for (i in 4..7) {
                    Text("", modifier = Modifier.weight(0.1f).border(1.dp, Color.Black).padding(4.dp))
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Text("TABLE INCHARGE: Timetable App", fontWeight = FontWeight.Bold)
            Text("PRINCIPAL", fontWeight = FontWeight.Bold)
        }
        
        Spacer(modifier = Modifier.height(16.dp))
        
        Button(
            onClick = {
                ExportUtils.shareArrangementSheet(context, sheetData)
            },
            modifier = Modifier.align(Alignment.End),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1E293B))
        ) {
            Text("Share Sheet", color = Color.White)
        }
    }
}
