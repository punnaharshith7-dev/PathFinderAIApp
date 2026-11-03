package com.example.ui.screens

import androidx.activity.compose.BackHandler
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
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.BookmarkBorder
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.CompareArrows
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material.icons.filled.WorkOutline
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.ScrollableTabRow
import androidx.compose.material3.Surface
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
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.data.catalog.CareerCatalog
import com.example.data.catalog.CareerOption
import com.example.data.model.UserProfile
import com.example.ui.components.GlassCard
import com.example.ui.components.GradientButton
import com.example.ui.components.MetricPill
import com.example.ui.components.SectionHeader
import com.example.ui.theme.CyanAccent
import com.example.ui.theme.ElectricBlue
import com.example.ui.theme.PrimaryPurple
import com.example.ui.theme.SuccessEmerald
import com.example.ui.theme.WarningAmber

@Composable
fun ExploreCareersScreen(
    profile: UserProfile,
    careerMatches: List<Pair<CareerOption, Int>>,
    selectedCareerDetailId: String?,
    showCompareSheet: Boolean,
    compareCareerIds: Pair<String, String>,
    bookmarkedCertIds: Set<String>,
    completedCertIds: Set<String>,
    savedInternshipIds: Set<String>,
    internshipStatuses: Map<String, String>,
    onSelectCareerDetail: (String?) -> Unit,
    onToggleCompareSheet: (Boolean) -> Unit,
    onUpdateCompareCareers: (String, String) -> Unit,
    onToggleBookmarkCareer: (String) -> Unit,
    onSetTargetCareer: (String) -> Unit,
    onToggleCertBookmark: (String) -> Unit,
    onToggleCertCompleted: (String) -> Unit,
    onUpdateInternshipStatus: (String, String) -> Unit
) {
    if (selectedCareerDetailId != null) {
        val careerPair = careerMatches.firstOrNull { it.first.id == selectedCareerDetailId }
            ?: (CareerCatalog.careers.first() to 90)
        CareerDetailView(
            career = careerPair.first,
            matchPercentage = careerPair.second,
            isBookmarked = profile.bookmarkedCareerIds.contains(careerPair.first.id),
            isTargetCareer = profile.targetCareerId == careerPair.first.id,
            userSkills = profile.technicalSkills,
            onBack = { onSelectCareerDetail(null) },
            onToggleBookmark = { onToggleBookmarkCareer(careerPair.first.id) },
            onSetTargetCareer = { onSetTargetCareer(careerPair.first.id) },
            onOpenCompare = {
                onSelectCareerDetail(null)
                onToggleCompareSheet(true)
            }
        )
        return
    }

    if (showCompareSheet) {
        CareerComparisonView(
            careerMatches = careerMatches,
            compareIds = compareCareerIds,
            onUpdateCompareIds = onUpdateCompareCareers,
            onBack = { onToggleCompareSheet(false) }
        )
        return
    }

    var activeSectionTab by remember { mutableIntStateOf(0) }
    var searchQuery by remember { mutableStateOf("") }
    var selectedCategory by remember { mutableStateOf("All") }
    val categories = listOf("All", "Emerging Technology", "Technology", "Engineering", "Creative", "Business", "Saved")

    Column(modifier = Modifier.fillMaxSize()) {
        ScrollableTabRow(
            selectedTabIndex = activeSectionTab,
            edgePadding = 16.dp,
            containerColor = MaterialTheme.colorScheme.surface
        ) {
            listOf("Career Roles", "Certifications", "Internships", "Industry Trends").forEachIndexed { idx, title ->
                Tab(
                    selected = activeSectionTab == idx,
                    onClick = { activeSectionTab = idx },
                    text = {
                        Text(
                            text = title,
                            fontWeight = if (activeSectionTab == idx) FontWeight.Bold else FontWeight.Normal
                        )
                    }
                )
            }
        }

        when (activeSectionTab) {
            0 -> {
                val filteredCareers = careerMatches.filter { (career, _) ->
                    val matchesQuery = searchQuery.isBlank() ||
                        career.title.contains(searchQuery, ignoreCase = true) ||
                        career.tagline.contains(searchQuery, ignoreCase = true) ||
                        career.requiredTechSkills.keys.any { it.contains(searchQuery, ignoreCase = true) }
                    val matchesCat = when (selectedCategory) {
                        "All" -> true
                        "Saved" -> profile.bookmarkedCareerIds.contains(career.id)
                        else -> career.category.equals(selectedCategory, ignoreCase = true)
                    }
                    matchesQuery && matchesCat
                }

                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(18.dp),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    item {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            OutlinedTextField(
                                value = searchQuery,
                                onValueChange = { searchQuery = it },
                                placeholder = { Text("Search roles, skills, or domains...") },
                                leadingIcon = { Icon(Icons.Default.Search, contentDescription = "Search") },
                                singleLine = true,
                                shape = RoundedCornerShape(16.dp),
                                modifier = Modifier
                                    .weight(1f)
                                    .testTag("career_search_input")
                            )
                            Spacer(modifier = Modifier.width(10.dp))
                            IconButton(
                                onClick = { onToggleCompareSheet(true) },
                                modifier = Modifier.testTag("open_compare_careers_button")
                            ) {
                                Icon(
                                    imageVector = Icons.Default.CompareArrows,
                                    contentDescription = "Compare Careers",
                                    tint = ElectricBlue
                                )
                            }
                        }
                    }

                    item {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .horizontalScroll(rememberScrollState()),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            categories.forEach { cat ->
                                FilterChip(
                                    selected = selectedCategory == cat,
                                    onClick = { selectedCategory = cat },
                                    label = { Text(cat) }
                                )
                            }
                        }
                    }

                    items(filteredCareers, key = { it.first.id }) { (career, matchPct) ->
                        val isSaved = profile.bookmarkedCareerIds.contains(career.id)
                        GlassCard(
                            onClick = { onSelectCareerDetail(career.id) },
                            modifier = Modifier.testTag("explore_career_${career.id}")
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                    MetricPill(text = "$matchPct% Match", color = SuccessEmerald)
                                    MetricPill(text = career.demandIndicator, color = ElectricBlue)
                                }
                                IconButton(onClick = { onToggleBookmarkCareer(career.id) }) {
                                    Icon(
                                        imageVector = if (isSaved) Icons.Default.Bookmark else Icons.Default.BookmarkBorder,
                                        contentDescription = "Bookmark",
                                        tint = if (isSaved) WarningAmber else MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = career.title,
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Text(
                                text = "${career.category} • ${career.growthRateSample}",
                                style = MaterialTheme.typography.labelSmall,
                                color = CyanAccent
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = career.tagline,
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Spacer(modifier = Modifier.height(10.dp))
                            Text(
                                text = "Required Skills: ${career.requiredTechSkills.keys.joinToString(" • ")}",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = "Salary Range: ${career.salaryRangeSample}",
                                style = MaterialTheme.typography.labelMedium,
                                fontWeight = FontWeight.SemiBold,
                                color = PrimaryPurple
                            )
                        }
                    }
                }
            }

            1 -> {
                // Certification Explorer
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(18.dp),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    item {
                        SectionHeader(
                            title = "Industry Certification Explorer",
                            subtitle = "Track verified credentials aligned with your target career"
                        )
                    }
                    items(CareerCatalog.certifications, key = { it.id }) { cert ->
                        val isSaved = bookmarkedCertIds.contains(cert.id)
                        val isDone = completedCertIds.contains(cert.id)
                        GlassCard {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                MetricPill(text = cert.category, color = ElectricBlue)
                                Row {
                                    IconButton(onClick = { onToggleCertBookmark(cert.id) }) {
                                        Icon(
                                            imageVector = if (isSaved) Icons.Default.Bookmark else Icons.Default.BookmarkBorder,
                                            contentDescription = "Bookmark certification",
                                            tint = if (isSaved) WarningAmber else MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    }
                                }
                            }
                            Text(
                                text = cert.title,
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Text(
                                text = "${cert.provider} • ${cert.difficulty} • ${cert.duration}",
                                style = MaterialTheme.typography.bodySmall,
                                color = CyanAccent
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "Skills: ${cert.skillsCovered.joinToString(", ")}",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Spacer(modifier = Modifier.height(12.dp))
                            OutlinedButton(
                                onClick = { onToggleCertCompleted(cert.id) },
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Icon(
                                    imageVector = Icons.Default.CheckCircle,
                                    contentDescription = null,
                                    tint = if (isDone) SuccessEmerald else MaterialTheme.colorScheme.primary,
                                    modifier = Modifier.size(18.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(if (isDone) "Completed & Added to Profile" else "Mark as Completed")
                            }
                        }
                    }
                }
            }

            2 -> {
                // Internship Explorer
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(18.dp),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    item {
                        SectionHeader(
                            title = "Internship Role Explorer (Sample Listings)",
                            subtitle = "Explore sample internship benchmarks and track preparation status"
                        )
                    }
                    items(CareerCatalog.internships, key = { it.id }) { intern ->
                        val currentStatus = internshipStatuses[intern.id]
                            ?: if (savedInternshipIds.contains(intern.id)) "Saved" else "Not Applied"
                        GlassCard {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                MetricPill(text = intern.category, color = PrimaryPurple)
                                MetricPill(text = currentStatus, color = SuccessEmerald)
                            }
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = intern.roleTitle,
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Text(
                                text = "${intern.companyType} • ${intern.duration}",
                                style = MaterialTheme.typography.labelMedium,
                                color = ElectricBlue
                            )
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = intern.description,
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = "Eligibility: ${intern.eligibility}",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Text(
                                text = "Stipend Benchmark: ${intern.stipendSample}",
                                style = MaterialTheme.typography.labelSmall,
                                color = WarningAmber
                            )
                            Spacer(modifier = Modifier.height(10.dp))
                            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                listOf("Saved", "Applied", "Shortlisted").forEach { statusOpt ->
                                    FilterChip(
                                        selected = currentStatus == statusOpt,
                                        onClick = { onUpdateInternshipStatus(intern.id, statusOpt) },
                                        label = { Text(statusOpt) }
                                    )
                                }
                            }
                        }
                    }
                }
            }

            3 -> {
                // Industry Trends & Career News UI
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(18.dp),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    item {
                        SectionHeader(
                            title = "Emerging Industry Signals",
                            subtitle = "Curated technology & hiring landscape trends"
                        )
                    }
                    val trends = listOf(
                        Triple(
                            "Agentic AI & Multimodal Engineering Surge",
                            "AI & Machine Learning",
                            "Organizations are prioritizing engineers who can combine foundation models with tool calling, structured outputs, and production evaluation suites."
                        ),
                        Triple(
                            "Full-Stack Kotlin & Cross-Platform Architecture",
                            "Software Engineering",
                            "Declarative UI frameworks (Jetpack Compose) and strongly-typed cloud backends continue to dominate modern mobile product teams."
                        ),
                        Triple(
                            "Zero-Trust Cloud Security & FinOps",
                            "Cloud & Cybersecurity",
                            "With rising cloud and AI inference workloads, DevSecOps and cost-aware cloud architecture skills are commanding strong premiums."
                        ),
                        Triple(
                            "AI-Native Product Management & Design Systems",
                            "Product & UX",
                            "Product builders who can prototype rapidly and design trustworthy AI human-in-the-loop experiences are in high demand."
                        )
                    )
                    items(trends) { (title, tag, desc) ->
                        GlassCard {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.TrendingUp,
                                    contentDescription = null,
                                    tint = CyanAccent
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                MetricPill(text = tag, color = CyanAccent)
                            }
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = title,
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = desc,
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun CareerDetailView(
    career: CareerOption,
    matchPercentage: Int,
    isBookmarked: Boolean,
    isTargetCareer: Boolean,
    userSkills: List<String>,
    onBack: () -> Unit,
    onToggleBookmark: () -> Unit,
    onSetTargetCareer: () -> Unit,
    onOpenCompare: () -> Unit
) {
    BackHandler { onBack() }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(18.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                    Text(
                        text = career.title,
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold
                    )
                }
                Row {
                    IconButton(onClick = onOpenCompare) {
                        Icon(Icons.Default.CompareArrows, contentDescription = "Compare", tint = ElectricBlue)
                    }
                    IconButton(onClick = onToggleBookmark) {
                        Icon(
                            imageVector = if (isBookmarked) Icons.Default.Bookmark else Icons.Default.BookmarkBorder,
                            contentDescription = "Bookmark",
                            tint = if (isBookmarked) WarningAmber else MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }

        item {
            GlassCard(borderColor = PrimaryPurple.copy(alpha = 0.6f)) {
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    MetricPill(text = "$matchPercentage% Compatibility", color = SuccessEmerald)
                    MetricPill(text = career.demandIndicator, color = ElectricBlue)
                }
                Spacer(modifier = Modifier.height(10.dp))
                Text(
                    text = career.overview,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    text = "Sample Compensation: ${career.salaryRangeSample}",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    color = CyanAccent
                )
                Text(
                    text = "Education Requirement: ${career.educationReq}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.height(14.dp))
                GradientButton(
                    text = if (isTargetCareer) "Active Target Career Roadmap" else "Set as Primary Target Career",
                    icon = Icons.Default.WorkOutline,
                    enabled = !isTargetCareer,
                    onClick = onSetTargetCareer,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }

        item {
            GlassCard(borderColor = SuccessEmerald.copy(alpha = 0.5f)) {
                Text(
                    text = "Why This Career Matches You",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = SuccessEmerald
                )
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = career.whyItMatchesTemplate,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }
        }

        item {
            GlassCard {
                Text(
                    text = "Daily Responsibilities",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(8.dp))
                career.dailyResponsibilities.forEach { duty ->
                    Text(
                        text = "• $duty",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(vertical = 3.dp)
                    )
                }
            }
        }

        item {
            GlassCard {
                Text(
                    text = "Required Technical & Soft Skills",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(8.dp))
                career.requiredTechSkills.forEach { (skill, targetPct) ->
                    val userLevel = userSkills.firstOrNull {
                        it.substringBefore(":").contains(skill, ignoreCase = true)
                    }?.substringAfter(":")?.toIntOrNull() ?: 50
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(skill, style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.SemiBold)
                        Text(
                            "Yours: $userLevel% / Required: $targetPct%",
                            style = MaterialTheme.typography.labelSmall,
                            color = if (userLevel >= targetPct) SuccessEmerald else WarningAmber
                        )
                    }
                }
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Soft Skills: ${career.requiredSoftSkills.joinToString(" • ")}",
                    style = MaterialTheme.typography.labelMedium,
                    color = ElectricBlue
                )
            }
        }

        item {
            GlassCard {
                Text(
                    text = "Career Progression & Certifications",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = career.careerProgression.joinToString("  →  "),
                    style = MaterialTheme.typography.bodySmall,
                    color = CyanAccent,
                    fontWeight = FontWeight.SemiBold
                )
                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    text = "Recommended Certifications:",
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.SemiBold
                )
                career.recommendedCertifications.forEach { cert ->
                    Text("• $cert", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
                Spacer(modifier = Modifier.height(10.dp))
                Text(
                    text = "Top Learning Resources:",
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.SemiBold
                )
                career.learningResources.forEach { res ->
                    Text("• $res", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }
        }
    }
}

@Composable
private fun CareerComparisonView(
    careerMatches: List<Pair<CareerOption, Int>>,
    compareIds: Pair<String, String>,
    onUpdateCompareIds: (String, String) -> Unit,
    onBack: () -> Unit
) {
    BackHandler { onBack() }
    val firstPair = careerMatches.firstOrNull { it.first.id == compareIds.first } ?: careerMatches.first()
    val secondPair = careerMatches.firstOrNull { it.first.id == compareIds.second } ?: careerMatches.getOrElse(1) { careerMatches.first() }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(18.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(onClick = onBack) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                }
                Text(
                    text = "Compare Two Careers",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold
                )
            }
        }

        item {
            Text("Select Career A:", style = MaterialTheme.typography.labelMedium, color = ElectricBlue)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                CareerCatalog.careers.forEach { c ->
                    FilterChip(
                        selected = compareIds.first == c.id,
                        onClick = { onUpdateCompareIds(c.id, compareIds.second) },
                        label = { Text(c.title) }
                    )
                }
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text("Select Career B:", style = MaterialTheme.typography.labelMedium, color = PrimaryPurple)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                CareerCatalog.careers.forEach { c ->
                    FilterChip(
                        selected = compareIds.second == c.id,
                        onClick = { onUpdateCompareIds(compareIds.first, c.id) },
                        label = { Text(c.title) }
                    )
                }
            }
        }

        item {
            val rows = listOf(
                "Match Score" to Pair("${firstPair.second}%", "${secondPair.second}%"),
                "Category" to Pair(firstPair.first.category, secondPair.first.category),
                "Sample Salary" to Pair(firstPair.first.salaryRangeSample, secondPair.first.salaryRangeSample),
                "Demand" to Pair(firstPair.first.demandIndicator, secondPair.first.demandIndicator),
                "Growth (Sample)" to Pair(firstPair.first.growthRateSample, secondPair.first.growthRateSample),
                "Core Tech Skills" to Pair(
                    firstPair.first.requiredTechSkills.keys.joinToString(", "),
                    secondPair.first.requiredTechSkills.keys.joinToString(", ")
                ),
                "Education" to Pair(firstPair.first.educationReq, secondPair.first.educationReq)
            )
            GlassCard {
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text(
                        text = firstPair.first.title,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = ElectricBlue,
                        modifier = Modifier.weight(1f)
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(
                        text = secondPair.first.title,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = PrimaryPurple,
                        modifier = Modifier.weight(1f)
                    )
                }
                Spacer(modifier = Modifier.height(12.dp))
                rows.forEach { (metric, values) ->
                    Surface(
                        color = MaterialTheme.colorScheme.background.copy(alpha = 0.45f),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 5.dp)
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Text(
                                text = metric.uppercase(),
                                style = MaterialTheme.typography.labelSmall,
                                color = CyanAccent,
                                fontWeight = FontWeight.Bold
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Row(modifier = Modifier.fillMaxWidth()) {
                                Text(
                                    text = values.first,
                                    style = MaterialTheme.typography.bodySmall,
                                    modifier = Modifier.weight(1f)
                                )
                                Spacer(modifier = Modifier.width(12.dp))
                                Text(
                                    text = values.second,
                                    style = MaterialTheme.typography.bodySmall,
                                    modifier = Modifier.weight(1f)
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
