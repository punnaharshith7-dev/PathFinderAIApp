package com.example.ui.screens

import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Explore
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material.icons.filled.Route
import androidx.compose.material.icons.filled.VerifiedUser
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.ui.components.GlassCard
import com.example.ui.components.GradientButton
import com.example.ui.components.MetricPill
import com.example.ui.theme.CyanAccent
import com.example.ui.theme.ElectricBlue
import com.example.ui.theme.PrimaryPurple
import com.example.ui.theme.SuccessEmerald
import com.example.util.GoogleSignInButton

private data class OnboardingPageData(
    val badge: String,
    val title: String,
    val description: String,
    val icon: ImageVector,
    val accentColor: Color,
    val highlights: List<String>
)

@Composable
fun AuthOnboardingScreen(
    onAuthSuccess: () -> Unit
) {
    val pages = remember {
        listOf(
            OnboardingPageData(
                badge = "STEP 01 • DISCOVER STRENGTHS",
                title = "Discover Your Strengths & Career DNA",
                description = "Take our interactive 12-dimension assessment to map your technical aptitude, problem-solving style, and leadership strengths.",
                icon = Icons.Default.Psychology,
                accentColor = PrimaryPurple,
                highlights = listOf("12-Dimension Assessment Engine", "Transparent Compatibility Scoring", "Skill Gap Radar Analytics")
            ),
            OnboardingPageData(
                badge = "STEP 02 • AI CAREER ENGINE",
                title = "Explore High-Growth Career Opportunities",
                description = "Compare 10+ technology, AI, design, and product roles with personalized match scores, skill requirements, and industry trajectories.",
                icon = Icons.Default.Explore,
                accentColor = ElectricBlue,
                highlights = listOf("Side-by-Side Role Comparison", "AI Resume Lab & Feedback", "Live Mock Interview Simulator")
            ),
            OnboardingPageData(
                badge = "STEP 03 • PERSONALIZED ROADMAP",
                title = "Build Your Future With Guided Roadmaps",
                description = "Follow a 10-level interactive learning roadmap with real-world capstone projects, certifications, XP streaks, and Gemini AI mentorship.",
                icon = Icons.Default.Route,
                accentColor = CyanAccent,
                highlights = listOf("10-Level Interactive Roadmaps", "24/7 Pathfinder AI Assistant", "Exportable PDF Career Reports")
            )
        )
    }

    var currentStep by remember { mutableIntStateOf(0) }
    var showSignInCard by remember { mutableStateOf(false) }
    var authError by remember { mutableStateOf<String?>(null) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        MaterialTheme.colorScheme.background,
                        MaterialTheme.colorScheme.surface,
                        MaterialTheme.colorScheme.background
                    )
                )
            )
            .windowInsetsPadding(WindowInsets.safeDrawing)
    ) {
        // Futuristic Ambient Constellation Canvas
        Canvas(modifier = Modifier.fillMaxSize()) {
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(PrimaryPurple.copy(alpha = 0.18f), Color.Transparent),
                    center = Offset(size.width * 0.85f, size.height * 0.15f),
                    radius = size.minDimension * 0.65f
                ),
                center = Offset(size.width * 0.85f, size.height * 0.15f),
                radius = size.minDimension * 0.65f
            )
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(ElectricBlue.copy(alpha = 0.14f), Color.Transparent),
                    center = Offset(size.width * 0.15f, size.height * 0.8f),
                    radius = size.minDimension * 0.6f
                ),
                center = Offset(size.width * 0.15f, size.height * 0.8f),
                radius = size.minDimension * 0.6f
            )
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 24.dp, vertical = 18.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            // Top Bar: Brand Logo & Skip
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = PrimaryPurple.copy(alpha = 0.2f),
                        modifier = Modifier.size(40.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(
                                imageVector = Icons.Default.AutoAwesome,
                                contentDescription = "Pathfinder AI Logo",
                                tint = ElectricBlue,
                                modifier = Modifier.size(22.dp)
                            )
                        }
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(
                            text = "PATHFINDER AI",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onBackground
                        )
                        Text(
                            text = "Career Intelligence Platform",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
                if (!showSignInCard) {
                    TextButton(
                        onClick = { showSignInCard = true },
                        modifier = Modifier.testTag("skip_onboarding_button")
                    ) {
                        Text("Skip", color = MaterialTheme.colorScheme.secondary)
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            if (!showSignInCard) {
                val page = pages[currentStep]
                AnimatedContent(targetState = page, label = "onboardingPage") { activePage ->
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        // Animated Orbital Hero Graphic
                        Box(
                            modifier = Modifier
                                .size(210.dp)
                                .padding(12.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Canvas(modifier = Modifier.fillMaxSize()) {
                                val r = size.minDimension / 2f
                                drawCircle(
                                    color = activePage.accentColor.copy(alpha = 0.25f),
                                    radius = r,
                                    style = Stroke(width = 2.dp.toPx())
                                )
                                drawCircle(
                                    color = ElectricBlue.copy(alpha = 0.35f),
                                    radius = r * 0.72f,
                                    style = Stroke(width = 1.5.dp.toPx())
                                )
                                drawCircle(
                                    brush = Brush.radialGradient(
                                        listOf(activePage.accentColor.copy(alpha = 0.35f), Color.Transparent)
                                    ),
                                    radius = r * 0.58f
                                )
                            }
                            Surface(
                                shape = CircleShape,
                                color = activePage.accentColor.copy(alpha = 0.22f),
                                modifier = Modifier.size(88.dp)
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Icon(
                                        imageVector = activePage.icon,
                                        contentDescription = activePage.title,
                                        tint = activePage.accentColor,
                                        modifier = Modifier.size(44.dp)
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))
                        MetricPill(text = activePage.badge, color = activePage.accentColor)
                        Spacer(modifier = Modifier.height(14.dp))
                        Text(
                            text = activePage.title,
                            style = MaterialTheme.typography.headlineLarge,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onBackground,
                            textAlign = TextAlign.Center
                        )
                        Spacer(modifier = Modifier.height(10.dp))
                        Text(
                            text = activePage.description,
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            textAlign = TextAlign.Center
                        )
                        Spacer(modifier = Modifier.height(20.dp))

                        GlassCard(modifier = Modifier.fillMaxWidth()) {
                            activePage.highlights.forEach { feat ->
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier.padding(vertical = 5.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.VerifiedUser,
                                        contentDescription = null,
                                        tint = SuccessEmerald,
                                        modifier = Modifier.size(18.dp)
                                    )
                                    Spacer(modifier = Modifier.width(10.dp))
                                    Text(
                                        text = feat,
                                        style = MaterialTheme.typography.bodyMedium,
                                        fontWeight = FontWeight.Medium,
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Page Indicators & Next Button
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    pages.indices.forEach { idx ->
                        val active = idx == currentStep
                        Box(
                            modifier = Modifier
                                .height(8.dp)
                                .width(if (active) 28.dp else 8.dp)
                                .clip(RoundedCornerShape(50))
                                .background(if (active) ElectricBlue else MaterialTheme.colorScheme.outline)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                GradientButton(
                    text = if (currentStep < pages.lastIndex) "Continue" else "Get Started",
                    icon = Icons.AutoMirrored.Filled.ArrowForward,
                    onClick = {
                        if (currentStep < pages.lastIndex) {
                            currentStep++
                        } else {
                            showSignInCard = true
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("onboarding_continue_button")
                )
            } else {
                // Cloud Sign-In Screen (Strictly using Google Sign-In via Credential Manager)
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    MetricPill(text = "CLOUD SYNC & GEMINI AI", color = CyanAccent)
                    Spacer(modifier = Modifier.height(14.dp))
                    Text(
                        text = "Welcome to Pathfinder AI",
                        style = MaterialTheme.typography.displayMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onBackground,
                        textAlign = TextAlign.Center
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "\"Discover Your Strengths. Find Your Direction. Build Your Future.\"",
                        style = MaterialTheme.typography.bodyMedium,
                        color = ElectricBlue,
                        textAlign = TextAlign.Center,
                        fontWeight = FontWeight.Medium
                    )
                    Spacer(modifier = Modifier.height(24.dp))

                    GlassCard(modifier = Modifier.fillMaxWidth()) {
                        Text(
                            text = "Sign in with your Google Account",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Your student career profile, 12-dimension assessment scores, personalized 10-level learning roadmaps, and AI mock interview progress are securely synced to Firebase Firestore.",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Spacer(modifier = Modifier.height(22.dp))

                        GoogleSignInButton(
                            onAuthSuccess = onAuthSuccess,
                            onAuthError = { err -> authError = err }
                        )

                        if (!authError.isNullOrBlank()) {
                            Spacer(modifier = Modifier.height(14.dp))
                            Surface(
                                shape = RoundedCornerShape(12.dp),
                                color = MaterialTheme.colorScheme.error.copy(alpha = 0.15f),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text(
                                    text = authError!!,
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.error,
                                    modifier = Modifier.padding(12.dp)
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))
                    TextButton(onClick = {
                        showSignInCard = false
                        currentStep = 0
                    }) {
                        Text("Back to Tour", color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                }
            }
        }
    }
}
