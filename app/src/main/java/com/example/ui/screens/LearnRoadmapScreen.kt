package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.filled.RadioButtonUnchecked
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Checkbox
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RadioButton
import androidx.compose.material3.ScrollableTabRow
import androidx.compose.material3.Slider
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
import com.example.ui.components.SectionHeader
import com.example.ui.components.SkillRadarCanvas
import com.example.ui.theme.CyanAccent
import com.example.ui.theme.ElectricBlue
import com.example.ui.theme.ErrorRose
import com.example.ui.theme.PrimaryPurple
import com.example.ui.theme.SuccessEmerald
import com.example.ui.theme.WarningAmber

@Composable
fun LearnRoadmapScreen(
    initialSubTab: Int,
    profile: UserProfile,
    goals: List<CareerGoalItem>,
    assessmentAnswers: Map<String, Int>,
    assessmentDimensions: Map<String, Int>,
    onSelectTargetCareer: (String) -> Unit,
    onToggleModuleCompleted: (String) -> Unit,
    onResetRoadmap: () -> Unit,
    onAddCustomTask: (String, String) -> Unit,
    onToggleGoalCompleted: (String, Boolean) -> Unit,
    onUpdateSkillProficiency: (String, Int, Boolean) -> Unit,
    onSelectAssessmentAnswer: (String, Int) -> Unit,
    onSubmitAssessment: () -> Unit
) {
    var selectedTab by remember(initialSubTab) { mutableIntStateOf(initialSubTab) }

    Column(modifier = Modifier.fillMaxSize()) {
        ScrollableTabRow(
            selectedTabIndex = selectedTab,
            edgePadding = 16.dp,
            containerColor = MaterialTheme.colorScheme.surface
        ) {
            listOf(
                "10-Level Roadmap",
                "Skill Gap Analyzer",
                "12-D Assessment"
            ).forEachIndexed { idx, title ->
                Tab(
                    selected = selectedTab == idx,
                    onClick = { selectedTab = idx },
                    text = {
                        Text(
                            text = title,
                            fontWeight = if (selectedTab == idx) FontWeight.Bold else FontWeight.Normal
                        )
                    }
                )
            }
        }

        when (selectedTab) {
            0 -> RoadmapTimelineSubScreen(
                profile = profile,
                goals = goals,
                onSelectTargetCareer = onSelectTargetCareer,
                onToggleModuleCompleted = onToggleModuleCompleted,
                onResetRoadmap = onResetRoadmap,
                onAddCustomTask = onAddCustomTask,
                onToggleGoalCompleted = onToggleGoalCompleted
            )
            1 -> SkillGapAnalyzerSubScreen(
                profile = profile,
                onUpdateSkillProficiency = onUpdateSkillProficiency
            )
            2 -> AssessmentSubScreen(
                assessmentAnswers = assessmentAnswers,
                assessmentDimensions = assessmentDimensions,
                onSelectAnswer = onSelectAssessmentAnswer,
                onSubmitAssessment = onSubmitAssessment
            )
        }
    }
}

@Composable
private fun RoadmapTimelineSubScreen(
    profile: UserProfile,
    goals: List<CareerGoalItem>,
    onSelectTargetCareer: (String) -> Unit,
    onToggleModuleCompleted: (String) -> Unit,
    onResetRoadmap: () -> Unit,
    onAddCustomTask: (String, String) -> Unit,
    onToggleGoalCompleted: (String, Boolean) -> Unit
) {
    val activeCareer = CareerCatalog.careers.firstOrNull { it.id == profile.targetCareerId }
        ?: CareerCatalog.careers.first()
    var expandedModuleId by remember { mutableStateOf<String?>(activeCareer.roadmap.firstOrNull()?.id) }
    var customTaskTitle by remember { mutableStateOf("") }

    val completedCount = activeCareer.roadmap.count { mod ->
        profile.completedLessonIds.contains(mod.id) || profile.completedLessonIds.contains("lvl_${mod.level}")
    }
    val totalCount = activeCareer.roadmap.size.coerceAtLeast(1)
    val progress = completedCount.toFloat() / totalCount.toFloat()
    val remainingWeeks = activeCareer.roadmap.filterNot { mod ->
        profile.completedLessonIds.contains(mod.id) || profile.completedLessonIds.contains("lvl_${mod.level}")
    }.sumOf { it.durationWeeks }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(18.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        item {
            SectionHeader(
                title = "Personalized Learning Roadmap",
                subtitle = "Switch career path or check off completed levels",
                actionLabel = "Reset",
                onActionClick = onResetRoadmap
            )
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                CareerCatalog.careers.forEach { career ->
                    FilterChip(
                        selected = activeCareer.id == career.id,
                        onClick = { onSelectTargetCareer(career.id) },
                        label = { Text(career.title) }
                    )
                }
            }
        }

        item {
            GlassCard(borderColor = CyanAccent.copy(alpha = 0.55f)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        MetricPill(text = "${activeCareer.title.uppercase()} PATH", color = CyanAccent)
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = "$completedCount / $totalCount Modules Completed",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "Estimated Remaining Duration: ~$remainingWeeks Weeks (Daily Goal: 90 mins)",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    Text(
                        text = "${(progress * 100).toInt()}%",
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Bold,
                        color = SuccessEmerald
                    )
                }
                Spacer(modifier = Modifier.height(10.dp))
                LinearProgressIndicator(
                    progress = { progress },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(10.dp)
                        .clip(RoundedCornerShape(50)),
                    color = SuccessEmerald
                )
            }
        }

        items(activeCareer.roadmap, key = { it.id }) { module ->
            val isCompleted = profile.completedLessonIds.contains(module.id) ||
                profile.completedLessonIds.contains("lvl_${module.level}")
            val isExpanded = expandedModuleId == module.id

            GlassCard(
                borderColor = if (isCompleted) SuccessEmerald.copy(alpha = 0.55f) else MaterialTheme.colorScheme.outline.copy(alpha = 0.5f),
                onClick = { expandedModuleId = if (isExpanded) null else module.id }
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                        IconButton(
                            onClick = { onToggleModuleCompleted(module.id) },
                            modifier = Modifier.testTag("toggle_module_${module.level}")
                        ) {
                            Icon(
                                imageVector = if (isCompleted) Icons.Default.CheckCircle else Icons.Default.RadioButtonUnchecked,
                                contentDescription = "Complete module",
                                tint = if (isCompleted) SuccessEmerald else ElectricBlue
                            )
                        }
                        Column {
                            Text(
                                text = module.title,
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Text(
                                text = "${module.difficulty} • ${module.durationWeeks} Weeks",
                                style = MaterialTheme.typography.labelSmall,
                                color = CyanAccent
                            )
                        }
                    }
                    Icon(
                        imageVector = if (isExpanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                        contentDescription = "Expand"
                    )
                }

                AnimatedVisibility(visible = isExpanded) {
                    Column(modifier = Modifier.padding(top = 12.dp)) {
                        Text(
                            text = "Subtopics: ${module.subtopics.joinToString(" • ")}",
                            style = MaterialTheme.typography.bodySmall,
                            color = ElectricBlue,
                            fontWeight = FontWeight.Medium
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text("Learning Objectives:", style = MaterialTheme.typography.labelLarge, fontWeight = FontWeight.Bold)
                        module.objectives.forEach { obj ->
                            Text("• $obj", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        Text("📺 Video Course: ${module.videoResource}", style = MaterialTheme.typography.bodySmall)
                        Text("📘 Documentation: ${module.docResource}", style = MaterialTheme.typography.bodySmall)
                        Text("🛠️ Capstone Mini-Project: ${module.miniProject}", style = MaterialTheme.typography.bodySmall, color = WarningAmber)
                        Text("🏅 Certification Target: ${module.certification}", style = MaterialTheme.typography.bodySmall, color = SuccessEmerald)
                    }
                }
            }
        }

        // Custom Learning Tasks Section
        item {
            GlassCard {
                Text(
                    text = "Custom Learning Tasks & Milestones",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(8.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    OutlinedTextField(
                        value = customTaskTitle,
                        onValueChange = { customTaskTitle = it },
                        placeholder = { Text("Add custom study task or project milestone...") },
                        singleLine = true,
                        modifier = Modifier
                            .weight(1f)
                            .testTag("custom_roadmap_task_input")
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    IconButton(
                        onClick = {
                            if (customTaskTitle.isNotBlank()) {
                                onAddCustomTask(customTaskTitle.trim(), "${activeCareer.title} Custom Task")
                                customTaskTitle = ""
                            }
                        },
                        modifier = Modifier.testTag("add_custom_task_button")
                    ) {
                        Icon(Icons.Default.Add, contentDescription = "Add custom task", tint = ElectricBlue)
                    }
                }
                Spacer(modifier = Modifier.height(10.dp))
                goals.filter { it.category == "roadmap_task" || it.category == "milestone" }.forEach { task ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Checkbox(
                            checked = task.completed,
                            onCheckedChange = { checked -> onToggleGoalCompleted(task.id, checked) }
                        )
                        Column(modifier = Modifier.weight(1f)) {
                            Text(task.title, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Medium)
                            if (task.subtitle.isNotBlank()) {
                                Text(task.subtitle, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun SkillGapAnalyzerSubScreen(
    profile: UserProfile,
    onUpdateSkillProficiency: (String, Int, Boolean) -> Unit
) {
    val targetCareer = CareerCatalog.careers.firstOrNull { it.id == profile.targetCareerId }
        ?: CareerCatalog.careers.first()

    val userSkillsMap = profile.technicalSkills.associate { entry ->
        val parts = entry.split(":")
        (parts.getOrNull(0) ?: entry) to (parts.getOrNull(1)?.toIntOrNull() ?: 70)
    }

    val gapItems = targetCareer.requiredTechSkills.map { (skillName, requiredPct) ->
        val currentPct = userSkillsMap.entries.firstOrNull {
            it.key.equals(skillName, ignoreCase = true) || skillName.contains(it.key, ignoreCase = true)
        }?.value ?: 45
        val gap = (requiredPct - currentPct).coerceAtLeast(0)
        Triple(skillName, currentPct, requiredPct to gap)
    }

    val avgCompatibility = gapItems.map { (_, cur, reqPair) ->
        ((cur.toFloat() / reqPair.first.toFloat()).coerceAtMost(1f) * 100).toInt()
    }.average().toInt()

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(18.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            SectionHeader(
                title = "Skill Gap Analyzer",
                subtitle = "Your skills vs ${targetCareer.title} requirements"
            )
        }

        item {
            GlassCard(borderColor = PrimaryPurple.copy(alpha = 0.55f)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        MetricPill(text = "SKILL COMPATIBILITY: $avgCompatibility%", color = SuccessEmerald)
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = "Target Role: ${targetCareer.title}",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "Adjust sliders below as you practice and level up your skills.",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
                Spacer(modifier = Modifier.height(10.dp))
                SkillRadarCanvas(
                    skills = gapItems.associate { it.first to it.second }
                )
            }
        }

        items(gapItems, key = { it.first }) { (skillName, currentPct, reqAndGap) ->
            val requiredPct = reqAndGap.first
            val gap = reqAndGap.second
            val priorityLabel = when {
                gap >= 20 -> "High Priority Gap (-$gap%)"
                gap > 0 -> "Medium Priority Gap (-$gap%)"
                else -> "Target Met ✓"
            }
            val priorityColor = when {
                gap >= 20 -> ErrorRose
                gap > 0 -> WarningAmber
                else -> SuccessEmerald
            }

            GlassCard {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = skillName,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    MetricPill(text = priorityLabel, color = priorityColor)
                }
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = "Current Proficiency: $currentPct%   •   Role Target: $requiredPct%",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Slider(
                    value = currentPct.toFloat(),
                    onValueChange = { newVal ->
                        onUpdateSkillProficiency(skillName, newVal.toInt(), true)
                    },
                    valueRange = 10f..100f
                )
            }
        }
    }
}

@Composable
private fun AssessmentSubScreen(
    assessmentAnswers: Map<String, Int>,
    assessmentDimensions: Map<String, Int>,
    onSelectAnswer: (String, Int) -> Unit,
    onSubmitAssessment: () -> Unit
) {
    val questions = CareerCatalog.assessmentQuestions
    var currentQuestionIdx by remember { mutableIntStateOf(0) }
    var showSummary by remember { mutableStateOf(false) }

    val activeQuestion = questions[currentQuestionIdx]
    val selectedOptionIdx = assessmentAnswers[activeQuestion.id] ?: 0

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(18.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            SectionHeader(
                title = "12-Dimension Career Assessment",
                subtitle = "Transparent multi-factor interest, aptitude & work style engine",
                actionLabel = if (showSummary) "Retake" else "Results",
                onActionClick = { showSummary = !showSummary }
            )
        }

        if (!showSummary) {
            item {
                GlassCard(borderColor = ElectricBlue.copy(alpha = 0.55f)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        MetricPill(
                            text = "Question ${currentQuestionIdx + 1} of ${questions.size}",
                            color = ElectricBlue
                        )
                        MetricPill(text = activeQuestion.category, color = PrimaryPurple)
                    }
                    Spacer(modifier = Modifier.height(10.dp))
                    LinearProgressIndicator(
                        progress = { (currentQuestionIdx + 1).toFloat() / questions.size.toFloat() },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(8.dp)
                            .clip(RoundedCornerShape(50)),
                        color = ElectricBlue
                    )
                    Spacer(modifier = Modifier.height(14.dp))
                    Text(
                        text = activeQuestion.prompt,
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(14.dp))

                    activeQuestion.options.forEachIndexed { idx, opt ->
                        val isSelected = selectedOptionIdx == idx
                        GlassCard(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 5.dp),
                            borderColor = if (isSelected) CyanAccent else MaterialTheme.colorScheme.outline.copy(alpha = 0.35f),
                            onClick = { onSelectAnswer(activeQuestion.id, idx) },
                            contentPadding = PaddingValues(12.dp)
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                RadioButton(
                                    selected = isSelected,
                                    onClick = { onSelectAnswer(activeQuestion.id, idx) }
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = opt.label,
                                    style = MaterialTheme.typography.bodyMedium,
                                    fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        OutlinedButton(
                            onClick = { if (currentQuestionIdx > 0) currentQuestionIdx-- },
                            enabled = currentQuestionIdx > 0
                        ) {
                            Text("Previous")
                        }
                        if (currentQuestionIdx < questions.lastIndex) {
                            GradientButton(
                                text = "Next Question",
                                onClick = { currentQuestionIdx++ }
                            )
                        } else {
                            GradientButton(
                                text = "Calculate Results",
                                onClick = {
                                    onSubmitAssessment()
                                    showSummary = true
                                },
                                modifier = Modifier.testTag("submit_assessment_button")
                            )
                        }
                    }
                }
            }
        } else {
            item {
                GlassCard(borderColor = SuccessEmerald.copy(alpha = 0.6f)) {
                    MetricPill(text = "TRANSPARENT ASSESSMENT SUMMARY", color = SuccessEmerald)
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Your Multi-Dimensional Career Profile",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "Note: Scores reflect weighted alignment across your 12 responses and are designed for exploratory career guidance.",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    SkillRadarCanvas(skills = assessmentDimensions)
                }
            }

            item {
                GlassCard {
                    Text("Dimension Breakdown", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(10.dp))
                    assessmentDimensions.forEach { (dim, score) ->
                        Column(modifier = Modifier.padding(vertical = 6.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(dim, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.SemiBold)
                                Text("$score%", style = MaterialTheme.typography.bodyMedium, color = CyanAccent, fontWeight = FontWeight.Bold)
                            }
                            Spacer(modifier = Modifier.height(4.dp))
                            LinearProgressIndicator(
                                progress = { score / 100f },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(8.dp)
                                    .clip(RoundedCornerShape(50)),
                                color = ElectricBlue
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(12.dp))
                    OutlinedButton(
                        onClick = {
                            currentQuestionIdx = 0
                            showSummary = false
                        },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Icon(Icons.Default.Refresh, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Retake 12-Question Assessment")
                    }
                }
            }
        }
    }
}
