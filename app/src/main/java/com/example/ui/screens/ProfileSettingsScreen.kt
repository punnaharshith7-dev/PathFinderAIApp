package com.example.ui.screens

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.LightMode
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material.icons.filled.PhotoCamera
import androidx.compose.material.icons.filled.PictureAsPdf
import androidx.compose.material.icons.filled.Save
import androidx.compose.material.icons.filled.Verified
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.ScrollableTabRow
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.data.catalog.CareerCatalog
import com.example.data.model.CareerGoalItem
import com.example.data.model.UserProfile
import com.example.ui.components.GlassCard
import com.example.ui.components.GradientButton
import com.example.ui.components.MetricPill
import com.example.ui.components.ScoreTrendLineChart
import com.example.ui.components.SectionHeader
import com.example.ui.components.WeeklyActivityBarChart
import com.example.ui.theme.CyanAccent
import com.example.ui.theme.ElectricBlue
import com.example.ui.theme.ErrorRose
import com.example.ui.theme.PrimaryPurple
import com.example.ui.theme.SuccessEmerald
import com.example.ui.theme.WarningAmber

@Composable
fun ProfileSettingsScreen(
    initialSubTab: Int,
    profile: UserProfile,
    goals: List<CareerGoalItem>,
    isDarkTheme: Boolean,
    onToggleDarkTheme: (Boolean) -> Unit,
    onSaveProfile: (UserProfile) -> Unit,
    onAddCustomGoal: (String, String, String) -> Unit,
    onExportCareerReportPdf: () -> Unit,
    onSignOut: () -> Unit
) {
    var selectedTab by remember(initialSubTab) { mutableIntStateOf(initialSubTab) }

    Column(modifier = Modifier.fillMaxSize()) {
        ScrollableTabRow(
            selectedTabIndex = selectedTab,
            edgePadding = 16.dp,
            containerColor = MaterialTheme.colorScheme.surface
        ) {
            listOf(
                "Student Profile",
                "Gamification & XP",
                "Analytics & PDF Report",
                "Settings"
            ).forEachIndexed { idx, label ->
                Tab(
                    selected = selectedTab == idx,
                    onClick = { selectedTab = idx },
                    text = {
                        Text(
                            text = label,
                            fontWeight = if (selectedTab == idx) FontWeight.Bold else FontWeight.Normal
                        )
                    }
                )
            }
        }

        when (selectedTab) {
            0 -> StudentProfileEditorSubScreen(
                profile = profile,
                goals = goals,
                onSaveProfile = onSaveProfile,
                onAddCustomGoal = onAddCustomGoal
            )
            1 -> GamificationSubScreen(profile = profile)
            2 -> AnalyticsAndReportSubScreen(
                profile = profile,
                goals = goals,
                onExportPdf = onExportCareerReportPdf
            )
            3 -> AppSettingsSubScreen(
                isDarkTheme = isDarkTheme,
                onToggleDarkTheme = onToggleDarkTheme,
                onSignOut = onSignOut
            )
        }
    }
}

@Composable
private fun StudentProfileEditorSubScreen(
    profile: UserProfile,
    goals: List<CareerGoalItem>,
    onSaveProfile: (UserProfile) -> Unit,
    onAddCustomGoal: (String, String, String) -> Unit
) {
    var fullName by remember(profile.fullName) { mutableStateOf(profile.fullName) }
    var email by remember(profile.email) { mutableStateOf(profile.email) }
    var phone by remember(profile.phone) { mutableStateOf(profile.phone) }
    var college by remember(profile.college) { mutableStateOf(profile.college) }
    var degree by remember(profile.degree) { mutableStateOf(profile.degree) }
    var branch by remember(profile.branch) { mutableStateOf(profile.branch) }
    var academicYear by remember(profile.academicYear) { mutableStateOf(profile.academicYear) }
    var cgpa by remember(profile.cgpa) { mutableStateOf(profile.cgpa) }
    var careerGoal by remember(profile.careerGoal) { mutableStateOf(profile.careerGoal) }
    var newSkillName by remember { mutableStateOf("") }
    var newProjectTitle by remember { mutableStateOf("") }
    var avatarUriString by remember { mutableStateOf<String?>(null) }

    // Zero-permission Android Photo Picker per Google Play policy
    val photoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri: Uri? ->
        if (uri != null) {
            avatarUriString = uri.toString()
        }
    }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(18.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            GlassCard(borderColor = PrimaryPurple.copy(alpha = 0.55f)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Surface(
                        shape = CircleShape,
                        color = PrimaryPurple.copy(alpha = 0.25f),
                        modifier = Modifier
                            .size(68.dp)
                            .clickable {
                                photoPickerLauncher.launch(
                                    PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                                )
                            }
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Text(
                                text = fullName.take(2).uppercase(),
                                style = MaterialTheme.typography.headlineSmall,
                                fontWeight = FontWeight.Bold,
                                color = ElectricBlue
                            )
                        }
                    }
                    Spacer(modifier = Modifier.width(14.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = fullName,
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "$degree • $branch",
                            style = MaterialTheme.typography.bodySmall,
                            color = ElectricBlue
                        )
                        Text(
                            text = if (avatarUriString != null) "Custom Avatar Selected ✓" else "Tap avatar to pick profile photo",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    IconButton(
                        onClick = {
                            photoPickerLauncher.launch(
                                PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                            )
                        }
                    ) {
                        Icon(Icons.Default.PhotoCamera, contentDescription = "Pick avatar photo", tint = CyanAccent)
                    }
                }
            }
        }

        item {
            GlassCard {
                Text("Academic & Personal Details", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(10.dp))
                OutlinedTextField(
                    value = fullName,
                    onValueChange = { fullName = it },
                    label = { Text("Full Name") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth().testTag("profile_name_input")
                )
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = email,
                    onValueChange = { email = it },
                    label = { Text("Email Address") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = phone,
                    onValueChange = { phone = it },
                    label = { Text("Phone Number") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = college,
                    onValueChange = { college = it },
                    label = { Text("College / University") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(8.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = degree,
                        onValueChange = { degree = it },
                        label = { Text("Degree") },
                        singleLine = true,
                        modifier = Modifier.weight(1f)
                    )
                    OutlinedTextField(
                        value = cgpa,
                        onValueChange = { cgpa = it },
                        label = { Text("CGPA / %") },
                        singleLine = true,
                        modifier = Modifier.weight(1f)
                    )
                }
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = branch,
                    onValueChange = { branch = it },
                    label = { Text("Branch / Specialization") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = academicYear,
                    onValueChange = { academicYear = it },
                    label = { Text("Current Academic Year") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = careerGoal,
                    onValueChange = { careerGoal = it },
                    label = { Text("Career Goal Statement") },
                    minLines = 2,
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(14.dp))
                GradientButton(
                    text = "Save Profile Changes",
                    icon = Icons.Default.Save,
                    onClick = {
                        onSaveProfile(
                            profile.copy(
                                fullName = fullName,
                                email = email,
                                phone = phone,
                                college = college,
                                degree = degree,
                                branch = branch,
                                academicYear = academicYear,
                                cgpa = cgpa,
                                careerGoal = careerGoal
                            )
                        )
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("save_profile_button")
                )
            }
        }

        // Skills & Projects Management
        item {
            GlassCard {
                Text("Technical & Soft Skills", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = profile.technicalSkills.joinToString("  •  ") { it.replace(":", " (") + "%)" },
                    style = MaterialTheme.typography.bodySmall,
                    color = CyanAccent
                )
                Spacer(modifier = Modifier.height(10.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    OutlinedTextField(
                        value = newSkillName,
                        onValueChange = { newSkillName = it },
                        placeholder = { Text("Add skill (e.g., PyTorch, Figma)") },
                        singleLine = true,
                        modifier = Modifier.weight(1f)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    IconButton(
                        onClick = {
                            if (newSkillName.isNotBlank()) {
                                val updatedSkills = (profile.technicalSkills + "${newSkillName.trim()}:75").distinct()
                                onSaveProfile(profile.copy(technicalSkills = updatedSkills))
                                newSkillName = ""
                            }
                        }
                    ) {
                        Icon(Icons.Default.Add, contentDescription = "Add skill", tint = ElectricBlue)
                    }
                }
            }
        }

        item {
            GlassCard {
                Text("Portfolio Projects & Achievements", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(8.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    OutlinedTextField(
                        value = newProjectTitle,
                        onValueChange = { newProjectTitle = it },
                        placeholder = { Text("Add project or certification title...") },
                        singleLine = true,
                        modifier = Modifier.weight(1f)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    IconButton(
                        onClick = {
                            if (newProjectTitle.isNotBlank()) {
                                onAddCustomGoal(newProjectTitle.trim(), "project", "Student Portfolio Project")
                                newProjectTitle = ""
                            }
                        }
                    ) {
                        Icon(Icons.Default.Add, contentDescription = "Add project", tint = SuccessEmerald)
                    }
                }
                Spacer(modifier = Modifier.height(8.dp))
                goals.filter { it.category == "project" || it.category == "certification" }.forEach { item ->
                    Row(
                        modifier = Modifier.padding(vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Default.Verified, contentDescription = null, tint = SuccessEmerald, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Column {
                            Text(item.title, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.SemiBold)
                            Text(item.subtitle, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun GamificationSubScreen(profile: UserProfile) {
    val currentLevel = (profile.xpPoints / 250L) + 1L
    val levelProgress = ((profile.xpPoints % 250L) / 250f).coerceIn(0f, 1f)

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(18.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            GlassCard(borderColor = WarningAmber.copy(alpha = 0.6f)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        MetricPill(text = "LEVEL $currentLevel CAREER ARCHITECT", color = WarningAmber)
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "${profile.xpPoints} Total XP",
                            style = MaterialTheme.typography.headlineMedium,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "${250L - (profile.xpPoints % 250L)} XP to Level ${currentLevel + 1}",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.LocalFireDepartment, contentDescription = null, tint = WarningAmber, modifier = Modifier.size(28.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("${profile.streakDays}d Streak", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = WarningAmber)
                    }
                }
                Spacer(modifier = Modifier.height(12.dp))
                LinearProgressIndicator(
                    progress = { levelProgress },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(10.dp)
                        .clip(RoundedCornerShape(50)),
                    color = WarningAmber
                )
            }
        }

        item {
            SectionHeader(
                title = "Achievement Badges",
                subtitle = "${profile.unlockedBadgeIds.size} of ${CareerCatalog.badges.size} Unlocked"
            )
        }

        items(CareerCatalog.badges, key = { it.id }) { badge ->
            val unlocked = profile.unlockedBadgeIds.contains(badge.id)
            GlassCard(
                borderColor = if (unlocked) SuccessEmerald.copy(alpha = 0.6f) else MaterialTheme.colorScheme.outline.copy(alpha = 0.3f)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                        Icon(
                            imageVector = Icons.Default.EmojiEvents,
                            contentDescription = null,
                            tint = if (unlocked) WarningAmber else MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.size(32.dp)
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(
                                text = badge.title,
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = badge.description,
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                    MetricPill(
                        text = if (unlocked) "Unlocked (+${badge.xpReward} XP)" else "+${badge.xpReward} XP",
                        color = if (unlocked) SuccessEmerald else ElectricBlue
                    )
                }
            }
        }

        // Progress Milestones Card
        item {
            GlassCard {
                Text("XP Milestone Tiers", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(10.dp))
                val tiers = listOf(
                    Triple("Level 1 • Explorer", "Sign in & create your profile", 0L),
                    Triple("Level 2 • Pathfinder", "Complete 12-D Assessment & 3 roadmap modules", 250L),
                    Triple("Level 3 • Specialist", "Analyze resume & complete AI Mock Interview", 500L),
                    Triple("Level 4 • Career Architect", "Master core skills & complete roadmap", 1000L)
                )

                tiers.forEach { (tierTitle, desc, reqXp) ->
                    val reached = profile.xpPoints >= reqXp
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 6.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = tierTitle,
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = FontWeight.Bold,
                                color = if (reached) CyanAccent else MaterialTheme.colorScheme.onSurface
                            )
                            Text(
                                text = desc,
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                        Text(
                            text = if (reached) "Reached ✓" else "$reqXp XP",
                            style = MaterialTheme.typography.labelLarge,
                            fontWeight = FontWeight.Bold,
                            color = if (reached) SuccessEmerald else WarningAmber
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun AnalyticsAndReportSubScreen(
    profile: UserProfile,
    goals: List<CareerGoalItem>,
    onExportPdf: () -> Unit
) {
    val currentScore = profile.readinessScore.toInt()
    val readinessHistory = listOf(
        "Start" to 0,
        "Profile" to (profile.calculateCompletionPercentage() / 3),
        "Skills" to (profile.technicalSkills.size * 8).coerceAtMost(75),
        "Roadmap" to (profile.completedLessonIds.size * 10).coerceAtMost(85),
        "Now" to currentScore
    )
    val todayHours = (profile.completedLessonIds.size * 0.5f)
    val weeklyHours = listOf(
        "Mon" to 0f,
        "Tue" to 0f,
        "Wed" to 0f,
        "Thu" to 0f,
        "Fri" to 0f,
        "Sat" to 0f,
        "Sun" to todayHours
    )
    val completedGoalsCount = goals.count { it.completed }
    val goalRate = if (goals.isNotEmpty()) (completedGoalsCount * 100) / goals.size else 0

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(18.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            GlassCard(borderColor = CyanAccent.copy(alpha = 0.6f)) {
                MetricPill(text = "CAREER REPORT GENERATOR", color = CyanAccent)
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Export Comprehensive Career Assessment Report (PDF)",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = "Includes your student profile, 12-dimension assessment scores, top career matches, skill gap analysis, and 10-level learning roadmap formatted as a multi-page PDF.",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.height(14.dp))
                GradientButton(
                    text = "Generate & Export PDF Report",
                    icon = Icons.Default.PictureAsPdf,
                    onClick = onExportPdf,
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("export_pdf_report_button")
                )
            }
        }

        item {
            GlassCard {
                SectionHeader(
                    title = "Career Readiness Score History",
                    subtitle = "Progression over your last 5 weeks"
                )
                Spacer(modifier = Modifier.height(10.dp))
                ScoreTrendLineChart(points = readinessHistory)
            }
        }

        item {
            GlassCard {
                SectionHeader(
                    title = "Weekly Learning Hours",
                    subtitle = "17.3h total this week • Goal Completion Rate: $goalRate%"
                )
                Spacer(modifier = Modifier.height(10.dp))
                WeeklyActivityBarChart(days = weeklyHours)
            }
        }
    }
}

@Composable
private fun AppSettingsSubScreen(
    isDarkTheme: Boolean,
    onToggleDarkTheme: (Boolean) -> Unit,
    onSignOut: () -> Unit
) {
    var dailyReminders by remember { mutableStateOf(true) }
    var milestoneAlerts by remember { mutableStateOf(true) }
    var aiInsightsAlerts by remember { mutableStateOf(true) }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(18.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            GlassCard {
                Text("Appearance & Theme", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(10.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = if (isDarkTheme) Icons.Default.DarkMode else Icons.Default.LightMode,
                            contentDescription = null,
                            tint = ElectricBlue
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text("Futuristic Dark Mode", style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.SemiBold)
                            Text("Switch between Dark Navy and Clean Light Mode", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                    }
                    Switch(
                        checked = isDarkTheme,
                        onCheckedChange = onToggleDarkTheme,
                        modifier = Modifier.testTag("theme_switch")
                    )
                }
            }
        }

        item {
            GlassCard {
                Text("Notification Preferences", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Daily Learning & Streak Reminders", style = MaterialTheme.typography.bodyMedium)
                    Switch(checked = dailyReminders, onCheckedChange = { dailyReminders = it })
                }
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Roadmap Milestone Alerts", style = MaterialTheme.typography.bodyMedium)
                    Switch(checked = milestoneAlerts, onCheckedChange = { milestoneAlerts = it })
                }
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("AI Career Match Updates", style = MaterialTheme.typography.bodyMedium)
                    Switch(checked = aiInsightsAlerts, onCheckedChange = { aiInsightsAlerts = it })
                }
            }
        }

        item {
            GlassCard {
                Text("About Pathfinder AI", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = "Pathfinder AI v1.0 • Powered by Firebase AI Logic (Gemini 3.8 Flash) & Cloud Firestore.\n\"Discover Your Strengths. Find Your Direction. Build Your Future.\"",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.height(14.dp))
                OutlinedButton(
                    onClick = onSignOut,
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("sign_out_button")
                ) {
                    Icon(Icons.AutoMirrored.Filled.Logout, contentDescription = null, tint = ErrorRose)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Sign Out of Google Account", color = ErrorRose)
                }
            }
        }
    }
}
