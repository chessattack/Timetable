package com.example

import com.google.firebase.firestore.FirebaseFirestore

data class Period(
    var title: String,
    var subtitle: String,
    var time: String,
    var periodName: String
)

data class DaySchedule(
    val dayName: String,
    val periods: List<Period>
)

data class ArrangementRow(
    val slNo: Int,
    val absentTeacherName: String,
    val periodAssignments: Map<Int, String>
)

data class ArrangementSheetData(
    val dateString: String,
    val rows: List<ArrangementRow>
)

data class NotificationItem(
    val targetClass: String,
    val message: String,
    val timestamp: Long
)

object TimetableData {
    val db = FirebaseFirestore.getInstance()
    
    data class UserAccount(val id: String, val role: String, val name: String, var isBanned: Boolean = false, val profilePicUri: String? = null)

    val allAccounts = androidx.compose.runtime.mutableStateListOf(
        UserAccount("1", "Student", "Arjun (Class 1A)"),
        UserAccount("2", "Teacher", "Mahima Tiwari"),
        UserAccount("3", "Teacher", "Deepa Manoj Nair"),
        UserAccount("4", "Admin", "Admin 1"),
        UserAccount("5", "Student", "Priya (Class 1B)")
    )

    val bannedUsers = androidx.compose.runtime.mutableStateListOf<String>()
    
    val classes = listOf(
        "1A", "1B", "1C", "1D", "1E",
        "2A", "2B", "2C", "2D", "2E",
        "3A", "3B", "3C", "3D", "3E",
        "4A", "4B", "4C", "4D", "4E",
        "5A", "5B", "5C", "5D", "5E",
        "6A", "6B", "6C", "6D", "6E",
        "7A", "7B", "7C", "7D", "7E",
        "8A", "8B", "8C", "8D", "8E",
        "9A", "9B", "9C", "9D", "9E",
        "10A", "10B", "10C", "10D", "10E",
        "11A", "11B", "11C", "11D", "11E",
        "12A", "12B", "12C", "12D", "12E"
    )

    private val defaultSchedule = listOf(
        DaySchedule("Monday", listOf(
            Period("SCIENCE", "Mahima Tiwari", "8:45-9:30", "P1"),
            Period("VE/WE", "Mrs Diksha", "9:30-10:10", "P2"),
            Period("HINDI", "Usha", "10:10-10:50", "P3"),
            Period("ENGLISH", "Deepa Manoj Nair", "10:50-11:30", "P4"),
            Period("MATHS", "Renu", "12:00-12:40", "P5"),
            Period("SOCIAL SCIENCE", "Sandeep", "12:40-1:20", "P6"),
            Period("YOGA", "Yoga", "1:20-2:00", "P7"),
            Period("DANCE", "Dance", "2:00-2:40", "P8")
        )),
        DaySchedule("Tuesday", listOf(
            Period("SCIENCE", "Mahima Tiwari", "8:45-9:30", "P1"),
            Period("SOCIAL SCIENCE", "Sandeep", "9:30-10:10", "P2"),
            Period("HINDI", "Usha", "10:10-10:50", "P3"),
            Period("DANCE", "Dance", "10:50-11:30", "P4"),
            Period("ENGLISH", "Deepa Manoj Nair", "12:00-12:40", "P5"),
            Period("MATHS", "Renu", "12:40-1:20", "P6"),
            Period("VE", "Sashirekha ATL Instructor", "1:20-2:00", "P7"),
            Period("COMPUTER/AI", "Loch", "2:00-2:40", "P8")
        )),
        DaySchedule("Wednesday", listOf(
            Period("SCIENCE", "Mahima Tiwari", "8:45-9:30", "P1"),
            Period("ART", "ART", "9:30-10:10", "P2"),
            Period("HINDI", "Usha", "10:10-10:50", "P3"),
            Period("ENGLISH", "Deepa Manoj Nair", "10:50-11:30", "P4"),
            Period("SOCIAL SCIENCE", "Sandeep", "12:00-12:40", "P5"),
            Period("SANSKRIT", "K M Vyanjana", "12:40-1:20", "P6"),
            Period("MATHS", "Renu", "1:20-2:00", "P7"),
            Period("VE", "Sashirekha ATL Instructor", "2:00-2:40", "P8")
        )),
        DaySchedule("Thursday", listOf(
            Period("SCIENCE", "Mahima Tiwari", "8:45-9:30", "P1"),
            Period("COMPUTER/AI", "Loch", "9:30-10:10", "P2"),
            Period("GAMES/PAT", "sports2", "10:10-10:50", "P3"),
            Period("GAMES/PAT", "sports2", "10:50-11:30", "P4"),
            Period("MATHS", "Renu", "12:00-12:40", "P5"),
            Period("SOCIAL SCIENCE", "Sandeep", "12:40-1:20", "P6"),
            Period("ENGLISH", "Deepa Manoj Nair", "1:20-2:00", "P7"),
            Period("HINDI", "Usha", "2:00-2:40", "P8")
        )),
        DaySchedule("Friday", listOf(
            Period("SCIENCE", "Mahima Tiwari", "8:45-9:30", "P1"),
            Period("LIB", "Shashikant Mishra", "9:30-10:10", "P2"),
            Period("HINDI", "Usha", "10:10-10:50", "P3"),
            Period("SOCIAL SCIENCE", "Sandeep", "10:50-11:30", "P4"),
            Period("MATHS", "Renu", "12:00-12:40", "P5"),
            Period("MATHS", "Renu", "12:40-1:20", "P6"),
            Period("SANSKRIT", "K M Vyanjana", "1:20-2:00", "P7"),
            Period("ENGLISH", "Deepa Manoj Nair", "2:00-2:40", "P8")
        )),
        DaySchedule("Saturday", listOf(
            Period("CLA", "", "8:45-9:30", "P1"),
            Period("CLA", "", "9:30-10:10", "P2"),
            Period("SCIENCE", "Mahima Tiwari", "10:10-10:50", "P3"),
            Period("SOCIAL SCIENCE", "Sandeep", "10:50-11:30", "P4"),
            Period("MATHS(CT)", "Renu", "12:00-12:40", "P5"),
            Period("SANSKRIT", "K M Vyanjana", "12:40-1:20", "P6"),
            Period("ENGLISH", "Deepa Manoj Nair", "1:20-2:00", "P7"),
            Period("VE/WE", "Mrs Diksha", "2:00-2:40", "P8")
        ))
    )

    val subjects = listOf(
        "SCIENCE", "ENGLISH", "HINDI", "MATHS", "SOCIAL SCIENCE", "SST",
        "VE", "WE", "VE/WE", "AI", "COMPUTER/AI", "GAMES", "GAMES/PAT",
        "ART", "SANSKRIT", "CLA", "LIB", "YOGA", "DANCE"
    )
    
    val dummyTeacherSubjects = (1..69).associate { "Dummy $it" to subjects[it % subjects.size] }

    val allPreRegisteredTeachers = listOf(
        "Mahima Tiwari", "Deepa Manoj Nair", "Usha", "Renu", "Sandeep",
        "Mrs Diksha", "Sashirekha ATL Instructor", "Loch", "sports2",
        "ART", "K M Vyanjana", "Shashikant Mishra", "Yoga", "Dance"
    ) + (1..69).map { "Dummy $it" }

    private val baseTeacherMap = mapOf(
        "SCIENCE" to listOf("Mahima Tiwari"),
        "ENGLISH" to listOf("Deepa Manoj Nair"),
        "HINDI" to listOf("Usha"),
        "MATHS" to listOf("Renu", "Renu (CT)"),
        "MATHS(CT)" to listOf("Renu"),
        "SOCIAL SCIENCE" to listOf("Sandeep"),
        "SST" to listOf("Sandeep"),
        "VE/WE" to listOf("Mrs Diksha", "Sashirekha ATL Instructor"),
        "VE" to listOf("Sashirekha ATL Instructor", "Mrs Diksha"),
        "WE" to listOf("Mrs Diksha"),
        "AI" to listOf("Loch"),
        "COMPUTER/AI" to listOf("Loch"),
        "GAMES/PAT" to listOf("sports2"),
        "GAMES" to listOf("sports2"),
        "ART" to listOf("ART"),
        "SANSKRIT" to listOf("K M Vyanjana"),
        "CLA" to listOf(""),
        "LIB" to listOf("Shashikant Mishra"),
        "YOGA" to listOf("Yoga"),
        "DANCE" to listOf("Dance")
    )

    private val teacherMap = baseTeacherMap.mapValues { (subject, teachers) ->
        val dummies = (1..69).filter { dummyTeacherSubjects["Dummy $it"] == subject }.map { "Dummy $it" }
        teachers + dummies
    }

    fun getSubjectForTeacher(teacherName: String): String {
        if (teacherName.startsWith("Dummy ")) {
            return dummyTeacherSubjects[teacherName] ?: "Unknown"
        }
        return baseTeacherMap.entries.firstOrNull { it.value.contains(teacherName) }?.key ?: "Unknown"
    }

    fun teachersForSubject(subject: String): List<String> {
        return teacherMap[subject.uppercase()] ?: listOf("Any Teacher")
    }

    private val editedSchedules = androidx.compose.runtime.mutableStateMapOf<String, List<DaySchedule>>()
    
    val absentTeachers = androidx.compose.runtime.mutableStateListOf<String>()
    val absentTeachersYesterday = androidx.compose.runtime.mutableStateListOf<String>("Usha", "Sandeep")
    val absentTeachersDayBefore = androidx.compose.runtime.mutableStateListOf<String>("Mahima Tiwari")
    
    val classNotifications = androidx.compose.runtime.mutableStateListOf<NotificationItem>()
    
    var timetableUpdateTrigger = androidx.compose.runtime.mutableStateOf(0)

    init {
        // Run silent background conflict prevention on startup
        val newSchedules = mutableMapOf<String, MutableList<DaySchedule>>()
        for (c in classes) {
            val schedCopy = defaultSchedule.map { daySch ->
                daySch.copy(periods = daySch.periods.map { it.copy() }.toMutableList())
            }.toMutableList()
            newSchedules[c] = schedCopy
        }
        enforceTimetableIntegrity(newSchedules)
        for ((c, sched) in newSchedules) {
            editedSchedules[c] = sched
        }
        
        // Listen to banned_users collection
        db.collection("banned_users").addSnapshotListener { snapshot, _ ->
            if (snapshot != null) {
                bannedUsers.clear()
                for (doc in snapshot.documents) {
                    val name = doc.getString("name")
                    if (name != null) {
                        bannedUsers.add(name)
                    }
                }
            }
        }
        
        // Listen to absent_teachers collection
        db.collection("absent_teachers").addSnapshotListener { snapshot, _ ->
            if (snapshot != null) {
                absentTeachers.clear()
                for (doc in snapshot.documents) {
                    val name = doc.getString("name")
                    if (name != null) {
                        absentTeachers.add(name)
                    }
                }
            }
        }
    }

    fun getScheduleForClass(className: String): List<DaySchedule> {
        return editedSchedules[className] ?: defaultSchedule
    }
    
    fun saveSchedule(className: String, newSchedule: List<DaySchedule>) {
        editedSchedules[className] = newSchedule
    }

    fun generateArrangementSheetData(): ArrangementSheetData {
        val calendar = java.util.Calendar.getInstance()
        calendar.add(java.util.Calendar.DAY_OF_YEAR, 1) // Tomorrow
        val sdf = java.text.SimpleDateFormat("dd/MM/yyyy", java.util.Locale.ENGLISH)
        val dateString = sdf.format(calendar.time)
        val dayFormat = java.text.SimpleDateFormat("EEEE", java.util.Locale.ENGLISH)
        val dayName = dayFormat.format(calendar.time)

        // Pre-compute subject teachers per class across the week
        val classSubjectTeachers = mutableMapOf<String, MutableSet<String>>()
        for (c in classes) {
            val teachers = mutableSetOf<String>()
            getScheduleForClass(c).forEach { daySch ->
                daySch.periods.forEach { p ->
                    if (p.subtitle.isNotBlank() && p.subtitle != "sports2" && p.subtitle != "GAMES") {
                        teachers.add(p.subtitle)
                    }
                }
            }
            classSubjectTeachers[c] = teachers
        }

        val newlyAssignedBusy = mutableMapOf<Int, MutableSet<String>>()
        for (i in 0 until 8) newlyAssignedBusy[i] = mutableSetOf<String>()

        val rows = mutableListOf<ArrangementRow>()
        var slNo = 1
        
        for (absentTeacher in absentTeachers) {
            val periodAssignments = mutableMapOf<Int, String>()
            
            for (pIndex in 0 until 8) {
                var targetClass: String? = null
                for (c in classes) {
                    val period = getScheduleForClass(c).find { it.dayName == dayName }?.periods?.getOrNull(pIndex)
                    if (period != null && period.subtitle == absentTeacher) {
                        targetClass = c
                        break
                    }
                }
                
                if (targetClass != null) {
                    val busyTeachers = mutableSetOf<String>()
                    for (c in classes) {
                        val p = getScheduleForClass(c).find { it.dayName == dayName }?.periods?.getOrNull(pIndex)
                        if (p != null && p.subtitle.isNotBlank() && p.subtitle !in absentTeachers) {
                            busyTeachers.add(p.subtitle)
                        }
                    }
                    busyTeachers.addAll(newlyAssignedBusy[pIndex]!!)
                    
                    val freeTeachers = allPreRegisteredTeachers.filter { 
                        it !in busyTeachers && it !in absentTeachers 
                    }.toMutableList()
                    
                    val cTeachers = classSubjectTeachers[targetClass] ?: emptySet()
                    
                    var sub = freeTeachers.firstOrNull { it in cTeachers && it != "sports2" && it != "Games" }
                    if (sub == null) {
                        sub = freeTeachers.firstOrNull { it != "sports2" && it != "Games" }
                    }
                    
                    if (sub != null) {
                        periodAssignments[pIndex] = "$sub ($targetClass)"
                        newlyAssignedBusy[pIndex]!!.add(sub)
                        classNotifications.add(
                            NotificationItem(
                                targetClass = targetClass,
                                message = "Instead of $absentTeacher, $sub is coming in the ${pIndex + 1} period.",
                                timestamp = System.currentTimeMillis()
                            )
                        )
                    } else {
                        periodAssignments[pIndex] = "sports2 ($targetClass)"
                        classNotifications.add(
                            NotificationItem(
                                targetClass = targetClass,
                                message = "Instead of $absentTeacher, sports2 is coming in the ${pIndex + 1} period.",
                                timestamp = System.currentTimeMillis()
                            )
                        )
                    }
                }
            }
            rows.add(ArrangementRow(slNo++, absentTeacher, periodAssignments))
        }
        
        return ArrangementSheetData(dateString, rows)
    }

    fun autoGenerateArrangements() {
        val days = listOf("Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday")
        
        // Deep copy of all current schedules
        val newSchedules = mutableMapOf<String, MutableList<DaySchedule>>()
        for (c in classes) {
            val currentSched = getScheduleForClass(c)
            val schedCopy = currentSched.map { daySch ->
                daySch.copy(periods = daySch.periods.map { it.copy() }.toMutableList())
            }.toMutableList()
            newSchedules[c] = schedCopy
        }

        // Pre-compute subject teachers per class across the week
        val classSubjectTeachers = mutableMapOf<String, MutableSet<String>>()
        for (c in classes) {
            val teachers = mutableSetOf<String>()
            for (d in days) {
                newSchedules[c]?.find { it.dayName == d }?.periods?.forEach { p ->
                    if (p.subtitle.isNotBlank() && p.subtitle != "sports2" && p.subtitle != "GAMES") {
                        teachers.add(p.subtitle)
                    }
                }
            }
            classSubjectTeachers[c] = teachers
        }

        for (day in days) {
            for (pIndex in 0 until 8) {
                val busyTeachers = mutableSetOf<String>()
                
                // Track who is busy in this period across all classes
                for (c in classes) {
                    val period = newSchedules[c]?.find { it.dayName == day }?.periods?.getOrNull(pIndex)
                    if (period != null && period.subtitle.isNotBlank() && period.subtitle !in absentTeachers) {
                        busyTeachers.add(period.subtitle)
                    }
                }

                // Available teachers are all pre-registered minus busy minus absent
                val freeTeachers = allPreRegisteredTeachers.filter { 
                    it !in busyTeachers && it !in absentTeachers 
                }.toMutableList()

                // Assign substitutes
                for (c in classes) {
                    val period = newSchedules[c]?.find { it.dayName == day }?.periods?.getOrNull(pIndex) ?: continue
                    if (period.subtitle in absentTeachers) {
                        val cTeachers = classSubjectTeachers[c] ?: emptySet()
                        
                        // Priority 1: Subject teachers of that class
                        var sub = freeTeachers.firstOrNull { it in cTeachers && it != "sports2" && it != "Games" }
                        
                        // Priority 2: Other free teachers
                        if (sub == null) {
                            sub = freeTeachers.firstOrNull { it != "sports2" && it != "Games" }
                        }

                        if (sub != null) {
                            val originalTeacher = period.subtitle
                            period.title = "${period.title} (Sub)"
                            period.subtitle = sub
                            freeTeachers.remove(sub) // mark as busy now
                            busyTeachers.add(sub)
                            classNotifications.add(
                                NotificationItem(
                                    targetClass = c,
                                    message = "Instead of $originalTeacher, $sub is coming in the ${pIndex + 1} period.",
                                    timestamp = System.currentTimeMillis()
                                )
                            )
                        } else {
                            val originalTeacher = period.subtitle
                            // Priority 3: Games period (sports2 can handle multiple)
                            period.title = "GAMES (Arrangement)"
                            period.subtitle = "sports2"
                            classNotifications.add(
                                NotificationItem(
                                    targetClass = c,
                                    message = "Instead of $originalTeacher, sports2 is coming in the ${pIndex + 1} period.",
                                    timestamp = System.currentTimeMillis()
                                )
                            )
                        }
                    }
                }
            }
        }

        // Final silent check to guarantee 100% conflict-free arrangements
        enforceTimetableIntegrity(newSchedules)

        // Apply new schedules back
        for ((c, sched) in newSchedules) {
            editedSchedules[c] = sched
        }
        
        // Notify
        timetableUpdateTrigger.value += 1
    }

    private fun enforceTimetableIntegrity(schedules: MutableMap<String, MutableList<DaySchedule>>) {
        val days = listOf("Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday")
        
        for (day in days) {
            for (pIndex in 0 until 8) {
                // 1. Check for Teacher Double Booking
                val teacherAssignedToClass = mutableMapOf<String, String>()
                val conflictClasses = mutableListOf<String>()
                
                for (c in classes) {
                    val period = schedules[c]?.find { it.dayName == day }?.periods?.getOrNull(pIndex)
                    if (period != null && period.subtitle.isNotBlank() && period.subtitle != "sports2" && period.subtitle != "Games" && period.subtitle != "Any Teacher") {
                        val teacher = period.subtitle
                        if (teacherAssignedToClass.containsKey(teacher)) {
                            // Conflict detected silently!
                            conflictClasses.add(c)
                        } else {
                            teacherAssignedToClass[teacher] = c
                        }
                    }
                }
                
                for (conflictC in conflictClasses) {
                    val period = schedules[conflictC]?.find { it.dayName == day }?.periods?.getOrNull(pIndex) ?: continue
                    
                    // Find a free teacher
                    val busyTeachers = mutableSetOf<String>()
                    for (c in classes) {
                        val p = schedules[c]?.find { it.dayName == day }?.periods?.getOrNull(pIndex)
                        if (p != null && p.subtitle.isNotBlank() && p.subtitle != "sports2" && p.subtitle != "Games" && p.subtitle != "Any Teacher") {
                            busyTeachers.add(p.subtitle)
                        }
                    }
                    
                    val freeTeachers = allPreRegisteredTeachers.filter { 
                        it !in busyTeachers && it !in absentTeachers && it != "sports2" && it != "Games"
                    }.toMutableList()
                    
                    val sub = freeTeachers.firstOrNull()
                    if (sub != null) {
                        period.title = getSubjectForTeacher(sub)
                        period.subtitle = sub
                    } else {
                        period.title = "GAMES (Arrangement)"
                        period.subtitle = "sports2"
                    }
                }
                
                // 2. Check for Room Double Booking
                val roomAssignedToClass = mutableMapOf<String, String>()
                val roomConflictClasses = mutableListOf<String>()
                for (c in classes) {
                    val period = schedules[c]?.find { it.dayName == day }?.periods?.getOrNull(pIndex)
                    if (period != null) {
                        val roomRequirements = when(period.title.split(" ").firstOrNull() ?: "") {
                            "COMPUTER/AI" -> "Computer Lab"
                            "ART" -> "Art Room"
                            "LIB" -> "Library"
                            "YOGA" -> "Yoga Studio"
                            "DANCE" -> "Dance Hall"
                            else -> "Classroom $c" 
                        }
                        
                        if (roomAssignedToClass.containsKey(roomRequirements)) {
                            if (roomRequirements != "Classroom $c") { 
                                roomConflictClasses.add(c)
                            }
                        } else {
                            roomAssignedToClass[roomRequirements] = c
                        }
                    }
                }
                
                for (conflictC in roomConflictClasses) {
                     val period = schedules[conflictC]?.find { it.dayName == day }?.periods?.getOrNull(pIndex) ?: continue
                     period.title = "GAMES (Room Reassigned)"
                     period.subtitle = "sports2" 
                }
            }
        }
    }
}
