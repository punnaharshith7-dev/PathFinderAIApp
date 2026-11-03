package com.example.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Analytics
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.BookmarkBorder
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.CompareArrows
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.PictureAsPdf
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material.icons.filled.Radar
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.data.catalog.CareerCatalog
import com.example.data.catalog.CareerOption
import com.example.data.model.CareerGoalItem
import com.example.data.model.UserProfile
import com.example.ui.components.AnimatedCircularScore
import com.example.ui.components.GlassCard
import com.example.ui.components.GradientButton
import com.example.ui.components.MetricPill
import com.example.ui.components.SectionHeader
import com.example.ui.components.SkillRadarCanvas
import com.example.ui.components.WeeklyActivityBarChart
import com.example.ui.theme.CyanAccent
import com.example.ui.theme.ElectricBlue
import com.example.ui.theme.PrimaryPurple
import com.example.ui.theme.SuccessEmerald
import com.example.ui.theme.WarningAmber

@Composable
fun HomeDashboardScreen(
    profile: UserProfile,
    careerMatches: List<Pair<CareerOption, Int>>,
    goals: List<CareerGoalItem>,
    unreadNotificationCount: Int,
    dailyChallengeCompleted: Boolean,
    onOpenNotifications: () -> Unit,
    onOpenAssessment: () -> Unit,
    onOpenSkillGap: () -> Unit,
    onOpenResumeLab: () -> Unit,
    onOpenMockInterview: () -> Unit,
    onOpenCompareCareers: () -> Unit,
    onOpenGamification: () -> Unit,
    onOpenAnalytics: () -> Unit,
    onExportPdfReport: () -> Unit,
    onSelectCareerDetail: (String) -> Unit,
    onToggleBookmarkCareer: (String) -> Unit,
    onCompleteDailyChallenge: () -> Unit,
    onNavigateToTab: (Int) -> Unit
) {
    val targetCareer = CareerCatalog.careers.firstOrNull { it.id == profile.targetCareerId }
    val completedModulesCount = profile.completedLessonIds.size
    val totalModulesCount = (targetCareer?.roadmap?.size ?: 10).coerceAtLeast(1)
    val roadmapProgress = (completedModulesCount.toFloat() / totalModulesCount.toFloat()).coerceIn(0f, 1f)
    val profileCompletionPct = profile.calculateCompletionPercentage()

    val skillMapForRadar = profile.technicalSkills.take(6).associate { entry ->
        val parts = entry.split(":")
        (parts.getOrNull(0) ?: entry) to (parts.getOrNull(1)?.toIntOrNull() ?: 75)
    }

    val loggedHoursPerDay = (completedModulesCount * 0.5f) + (if (dailyChallengeCompleted) 0.5f else 0f)
    val weeklyHours = listOf(
        "Mon" to 0f,
        "Tue" to 0f,
        "Wed" to 0f,
        "Thu" to 0f,
        "Fri" to 0f,
        "Sat" to 0f,
        "Sun" to loggedHoursPerDay
    )

    val displayName = profile.fullName.ifBlank { "New Student" }
    val academicSubtitle = if (profile.degree.isNotBlank() || profile.branch.isNotBlank()) {
        listOf(profile.degree, profile.branch).filter { it.isNotBlank() }.joinToString(" • ")
    } else {
        profile.email.ifBlank { "Tap Profile to add your college & degree" }
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .testTag("home_dashboard_list"),
        contentPadding = PaddingValues(horizontal = 18.dp, vertical = 14.dp),
        verticalArrangement = Arrangement.spacedBy(18.dp)
    ) {
        // 1. Personalized Header Bar
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.weight(1f)
                ) {
                    Surface(
                        shape = CircleShape,
                        color = PrimaryPurple.copy(alpha = 0.25f),
                        modifier = Modifier
                            .size(50.dp)
                            .clickable { onNavigateToTab(4) }
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Text(
                                text = displayName.take(2).uppercase(),
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = ElectricBlue
                            )
                        }
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(
                            text = "Welcome,",
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Text(
                            text = displayName,
                            style = MaterialTheme.typography.headlineSmall,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onBackground,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                        Text(
                            text = academicSubtitle,
                            style = MaterialTheme.typography.labelSmall,
                            color = ElectricBlue,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Surface(
                        shape = RoundedCornerShape(50),
                        color = WarningAmber.copy(alpha = 0.16f),
                        modifier = Modifier.clickable { onOpenGamification() }
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.LocalFireDepartment,
                                contentDescription = "Streak",
                                tint = WarningAmber,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "${profile.streakDays}d",
                                style = MaterialTheme.typography.labelLarge,
                                fontWeight = FontWeight.Bold,
                                color = WarningAmber
                            )
                        }
                    }
                    Spacer(modifier = Modifier.width(6.dp))
                    IconButton(
                        onClick = onOpenNotifications,
                        modifier = Modifier.testTag("notifications_button")
                    ) {
                        BadgedBox(
                            badge = {
                                if (unreadNotificationCount > 0) {
                                    Badge(containerColor = PrimaryPurple) {
                                        Text("$unreadNotificationCount")
                                    }
                                }
                            }
                        ) {
                            Icon(
                                imageVector = Icons.Default.Notifications,
                                contentDescription = "Notifications",
                                tint = MaterialTheme.colorScheme.onBackground
                            )
                        }
                    }
                }
            }
        }

        // 2. Hero Career Readiness Score + Profile Completion Card
        item {
            GlassCard(
                borderColor = PrimaryPurple.copy(alpha = 0.55f)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        MetricPill(text = "AI CAREER READINESS", color = CyanAccent)
                        Spacer(modifier = Modifier.height(10.dp))
                        Text(
                            text = if (targetCareer != null) "Target: ${targetCareer.title}" else "No Target Career Selected Yet",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = "XP: ${profile.xpPoints} pts • Level ${(profile.xpPoints / 250L) + 1L} Explorer",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = "Profile Completion ($profileCompletionPct%)",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        LinearProgressIndicator(
                            progress = { profileCompletionPct / 100f },
                            modifier = Modifier
                                .fillMaxWidth(0.9f)
                                .height(8.dp)
                                .clip(RoundedCornerShape(50)),
                            color = SuccessEmerald,
                            trackColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.3f)
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = if (profile.assessmentCompleted) "Assessment Completed ✓" else "Take the 12-D Assessment to calculate readiness",
                            style = MaterialTheme.typography.labelSmall,
                            color = if (profile.assessmentCompleted) SuccessEmerald else WarningAmber
                        )
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    AnimatedCircularScore(
                        score = profile.readinessScore.toInt(),
                        label = "Readiness"
                    )
                }
            }
        }

        // 3. Quick Action Launchpad (8 Modules)
        item {
            SectionHeader(
                title = "AI Career Launchpad",
                subtitle = "Interactive tools for every stage of your career journey"
            )
            Spacer(modifier = Modifier.height(8.dp))
            val actions = listOf(
                Triple("Assessment", Icons.Default.Psychology, onOpenAssessment),
                Triple("Skill Gap", Icons.Default.Radar, onOpenSkillGap),
                Triple("Resume Lab", Icons.Default.Description, onOpenResumeLab),
                Triple("Mock Interview", Icons.Default.Mic, onOpenMockInterview),
                Triple("Compare Roles", Icons.Default.CompareArrows, onOpenCompareCareers),
                Triple("PDF Report", Icons.Default.PictureAsPdf, onExportPdfReport),
                Triple("Achievements", Icons.Default.EmojiEvents, onOpenGamification),
                Triple("Analytics", Icons.Default.Analytics, onOpenAnalytics)
            )
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                actions.chunked(4).forEach { rowItems ->
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        rowItems.forEach { (label, icon, action) ->
                            GlassCard(
                                modifier = Modifier
                                    .weight(1f)
                                    .testTag("quick_action_${label.lowercase().replace(" ", "_")}"),
                                onClick = action,
                                contentPadding = PaddingValues(vertical = 14.dp, horizontal = 6.dp)
                            ) {
                                Column(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalAlignment = Alignment.CenterHorizontally
                                ) {
                                    Surface(
                                        shape = CircleShape,
                                        color = PrimaryPurple.copy(alpha = 0.2f),
                                        modifier = Modifier.size(40.dp)
                                    ) {
                                        Box(contentAlignment = Alignment.Center) {
                                            Icon(
                                                imageVector = icon,
                                                contentDescription = label,
                                                tint = ElectricBlue,
                                                modifier = Modifier.size(20.dp)
                                            )
                                        }
                                    }
                                    Spacer(modifier = Modifier.height(8.dp))
                                    Text(
                                        text = label,
                                        style = MaterialTheme.typography.labelSmall,
                                        fontWeight = FontWeight.SemiBold,
                                        color = MaterialTheme.colorScheme.onSurface,
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

        // 4. Recommended Careers Horizontal Cards
        item {
            SectionHeader(
                title = "Explore Career Paths",
                subtitle = if (profile.assessmentCompleted || profile.technicalSkills.isNotEmpty()) {
                    "Personalized match scores based on your skills & assessment"
                } else {
                    "Complete your profile or assessment to personalize match scores"
                },
                actionLabel = "Explore All",
                onActionClick = { onNavigateToTab(1) }
            )
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                careerMatches.take(5).forEach { (career, matchScore) ->
                    val isBookmarked = profile.bookmarkedCareerIds.contains(career.id)
                    GlassCard(
                        modifier = Modifier
                            .width(275.dp)
                            .testTag("dashboard_career_card_${career.id}"),
                        onClick = { onSelectCareerDetail(career.id) }
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            MetricPill(
                                text = if (profile.assessmentCompleted || profile.technicalSkills.isNotEmpty()) "$matchScore% Match" else "Explore Path",
                                color = SuccessEmerald
                            )
                            IconButton(
                                onClick = { onToggleBookmarkCareer(career.id) },
                                modifier = Modifier.size(32.dp)
                            ) {
                                Icon(
                                    imageVector = if (isBookmarked) Icons.Default.Bookmark else Icons.Default.BookmarkBorder,
                                    contentDescription = "Bookmark ${career.title}",
                                    tint = if (isBookmarked) WarningAmber else MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = career.title,
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = career.category,
                            style = MaterialTheme.typography.labelSmall,
                            color = ElectricBlue
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = career.tagline,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            maxLines = 2,
                            overflow = TextOverflow.Ellipsis
                        )
                        Spacer(modifier = Modifier.height(10.dp))
                        Text(
                            text = career.salaryRangeSample,
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.SemiBold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                }
            }
        }

        // 5. Learning Roadmap Summary
        item {
            GlassCard(
                onClick = { onNavigateToTab(3) }
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        MetricPill(text = "LEARNING ROADMAP", color = PrimaryPurple)
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = if (targetCareer != null) "${targetCareer.title} Path" else "Select a Career Roadmap",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = "$completedModulesCount of $totalModulesCount Levels Completed",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    Text(
                        text = "${(roadmapProgress * 100).toInt()}%",
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Bold,
                        color = CyanAccent
                    )
                }
                Spacer(modifier = Modifier.height(12.dp))
                LinearProgressIndicator(
                    progress = { roadmapProgress },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(10.dp)
                        .clip(RoundedCornerShape(50)),
                    color = CyanAccent,
                    trackColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.3f)
                )
            }
        }

        // 6. Daily Career Challenge
        item {
            GlassCard(
                borderColor = if (dailyChallengeCompleted) SuccessEmerald.copy(alpha = 0.6f) else WarningAmber.copy(alpha = 0.5f)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    MetricPill(
                        text = if (dailyChallengeCompleted) "CHALLENGE COMPLETED (+50 XP)" else "DAILY CAREER CHALLENGE • +50 XP",
                        color = if (dailyChallengeCompleted) SuccessEmerald else WarningAmber
                    )
                    Icon(
                        imageVector = Icons.Default.AutoAwesome,
                        contentDescription = null,
                        tint = WarningAmber
                    )
                }
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Complete 1 Career Assessment or Roadmap Module",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Start your daily learning streak by completing a roadmap topic or practicing 1 question in the Mock Interview simulator.",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.height(12.dp))
                GradientButton(
                    text = if (dailyChallengeCompleted) "Completed for Today" else "Claim +50 XP & Start Streak",
                    icon = Icons.Default.CheckCircle,
                    enabled = !dailyChallengeCompleted,
                    onClick = onCompleteDailyChallenge,
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("complete_daily_challenge_button")
                )
            }
        }

        // 7. Skill Overview Radar Chart (or Empty State for New Account)
        item {
            GlassCard {
                SectionHeader(
                    title = "Technical Skill Radar",
                    subtitle = if (skillMapForRadar.isNotEmpty()) "Live snapshot of your skills" else "No skills added to your profile yet",
                    actionLabel = if (skillMapForRadar.isNotEmpty()) "Skill Gap" else "Add Skills",
                    onActionClick = { if (skillMapForRadar.isNotEmpty()) onOpenSkillGap() else onNavigateToTab(4) }
                )
                Spacer(modifier = Modifier.height(8.dp))
                if (skillMapForRadar.size >= 3) {
                    SkillRadarCanvas(skills = skillMapForRadar)
                } else {
                    Text(
                        text = "Your account has no technical skills recorded yet. Add at least 3 skills in the Profile tab or Skill Gap Analyzer to visualize your radar chart.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    OutlinedButton(
                        onClick = { onNavigateToTab(4) },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Go to Profile to Add Your Skills")
                    }
                }
            }
        }

        // 8. Weekly Learning Activity Bar Chart
        item {
            GlassCard {
                SectionHeader(
                    title = "Weekly Learning Activity",
                    subtitle = "${loggedHoursPerDay}h logged this week",
                    actionLabel = "Analytics",
                    onActionClick = onOpenAnalytics
                )
                Spacer(modifier = Modifier.height(12.dp))
                WeeklyActivityBarChart(days = weeklyHours)
            }
        }

        // 9. Upcoming Milestones & Daily Career Tip
        item {
            SectionHeader(
                title = "Your Goals & Milestones",
                subtitle = if (goals.isEmpty()) "No custom goals added yet" else "Synced with your cloud career roadmap"
            )
            Spacer(modifier = Modifier.height(8.dp))
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                if (goals.isEmpty()) {
                    GlassCard(contentPadding = PaddingValues(16.dp)) {
                        Text(
                            text = "No goals or milestones yet",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.SemiBold
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Take the 12-D Assessment or add custom study tasks in the Learn tab to track your milestones here.",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                } else {
                    goals.take(3).forEach { goal ->
                        GlassCard(contentPadding = PaddingValues(14.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = goal.title,
                                        style = MaterialTheme.typography.titleSmall,
                                        fontWeight = FontWeight.SemiBold,
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                    if (goal.subtitle.isNotBlank()) {
                                        Text(
                                            text = goal.subtitle,
                                            style = MaterialTheme.typography.bodySmall,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    }
                                }
                                MetricPill(
                                    text = if (goal.completed) "Done" else "Active",
                                    color = if (goal.completed) SuccessEmerald else ElectricBlue
                                )
                            }
                        }
                    }
                }

                // Daily Career Tip
                GlassCard(
                    borderColor = CyanAccent.copy(alpha = 0.45f),
                    contentPadding = PaddingValues(16.dp)
                ) {
                    Text(
                        text = "💡 Daily Career Tip",
                        style = MaterialTheme.typography.labelLarge,
                        color = CyanAccent,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Recruiters spend an average of 7 seconds on an initial resume scan. Quantify your project outcomes with concrete latency, accuracy, or user adoption numbers.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
            }
        }
    }
}
