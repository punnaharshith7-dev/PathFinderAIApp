package com.example

import android.content.Context
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.DoneAll
import androidx.compose.material.icons.filled.Explore
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.outlined.AutoAwesome
import androidx.compose.material.icons.outlined.Explore
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.School
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.credentials.CredentialManager
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.data.model.CareerGoalItem
import com.example.ui.components.GlassCard
import com.example.ui.components.MetricPill
import com.example.ui.screens.AiAssistantHubScreen
import com.example.ui.screens.AuthOnboardingScreen
import com.example.ui.screens.ExploreCareersScreen
import com.example.ui.screens.HomeDashboardScreen
import com.example.ui.screens.LearnRoadmapScreen
import com.example.ui.screens.ProfileSettingsScreen
import com.example.ui.theme.CyanAccent
import com.example.ui.theme.ElectricBlue
import com.example.ui.theme.MyApplicationTheme
import com.example.util.PdfReportGenerator
import com.example.util.attemptAutoSignIn
import com.example.util.authStateFlow
import com.example.util.configureAppCheck
import com.example.util.signOut
import com.example.viewmodel.PathfinderViewModel
import com.example.viewmodel.UiState
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // STEP 1: Mandatory App Check configuration with launch intent before UI or Firebase calls
        configureAppCheck(this, intent)

        enableEdgeToEdge()

        val themePrefs = getSharedPreferences("pathfinder_theme_prefs", Context.MODE_PRIVATE)

        setContent {
            var isDarkTheme by rememberSaveable {
                mutableStateOf(themePrefs.getBoolean("dark_mode", true))
            }

            MyApplicationTheme(darkTheme = isDarkTheme) {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    AppNavigation(
                        isDarkTheme = isDarkTheme,
                        onToggleDarkTheme = { enabled ->
                            isDarkTheme = enabled
                            themePrefs.edit().putBoolean("dark_mode", enabled).apply()
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun AppNavigation(
    isDarkTheme: Boolean,
    onToggleDarkTheme: (Boolean) -> Unit,
    auth: FirebaseAuth = Firebase.auth
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val credentialManager = remember(context) { CredentialManager.create(context) }
    val currentUser by auth.authStateFlow().collectAsStateWithLifecycle(initialValue = auth.currentUser)

    LaunchedEffect(Unit) {
        if (auth.currentUser == null) {
            attemptAutoSignIn(
                context = context,
                credentialManager = credentialManager,
                onAuthSuccess = {},
                onUnauthenticated = {},
                scope = scope
            )
        }
    }

    val user = currentUser
    if (user == null) {
        AuthOnboardingScreen(
            onAuthSuccess = {}
        )
    } else {
        MainFeatureScreen(
            currentUserId = user.uid,
            isDarkTheme = isDarkTheme,
            onToggleDarkTheme = onToggleDarkTheme,
            onSignOut = {
                signOut(
                    context = context,
                    credentialManager = credentialManager,
                    onSignOutComplete = {},
                    scope = scope
                )
            }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainFeatureScreen(
    currentUserId: String,
    isDarkTheme: Boolean,
    onToggleDarkTheme: (Boolean) -> Unit,
    onSignOut: () -> Unit
) {
    val context = LocalContext.current
    val viewModel: PathfinderViewModel = viewModel(
        key = currentUserId,
        factory = PathfinderViewModel.factory(currentUserId)
    )

    val profile by viewModel.localProfile.collectAsStateWithLifecycle()
    val goalsState by viewModel.goalsState.collectAsStateWithLifecycle()
    val goals: List<CareerGoalItem> = (goalsState as? UiState.Success)?.data ?: emptyList()
    val assessmentAnswers by viewModel.assessmentAnswers.collectAsStateWithLifecycle()
    val assessmentDimensions by viewModel.assessmentDimensions.collectAsStateWithLifecycle()
    val compareCareerIds by viewModel.compareCareerIds.collectAsStateWithLifecycle()
    val bookmarkedCertIds by viewModel.bookmarkedCertIds.collectAsStateWithLifecycle()
    val completedCertIds by viewModel.completedCertIds.collectAsStateWithLifecycle()
    val savedInternshipIds by viewModel.savedInternshipIds.collectAsStateWithLifecycle()
    val internshipStatuses by viewModel.internshipStatuses.collectAsStateWithLifecycle()
    val chatMessages by viewModel.chatMessages.collectAsStateWithLifecycle()
    val aiChatState by viewModel.aiChatState.collectAsStateWithLifecycle()
    val resumeState by viewModel.resumeState.collectAsStateWithLifecycle()
    val interviewFeedbackState by viewModel.interviewFeedbackState.collectAsStateWithLifecycle()
    val notifications by viewModel.notifications.collectAsStateWithLifecycle()
    val dailyChallengeCompleted by viewModel.dailyChallengeCompleted.collectAsStateWithLifecycle()
    val statusBannerMessage by viewModel.statusBannerMessage.collectAsStateWithLifecycle()

    val careerMatches = remember(profile, assessmentDimensions) {
        viewModel.calculateCareerMatches(profile)
    }

    var currentTab by rememberSaveable { mutableIntStateOf(0) }
    var aiSubTab by rememberSaveable { mutableIntStateOf(0) }
    var learnSubTab by rememberSaveable { mutableIntStateOf(0) }
    var profileSubTab by rememberSaveable { mutableIntStateOf(0) }
    var selectedCareerDetailId by rememberSaveable { mutableStateOf<String?>(null) }
    var showCompareCareers by rememberSaveable { mutableStateOf(false) }
    var showNotificationsSheet by remember { mutableStateOf(false) }

    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(statusBannerMessage) {
        val msg = statusBannerMessage
        if (!msg.isNullOrBlank()) {
            snackbarHostState.showSnackbar(msg)
            viewModel.clearBannerMessage()
        }
    }

    if (currentTab != 0 && selectedCareerDetailId == null && !showCompareCareers) {
        BackHandler {
            currentTab = 0
        }
    }

    val navItems = listOf(
        Triple("Home", Icons.Filled.Home, Icons.Outlined.Home),
        Triple("Explore", Icons.Filled.Explore, Icons.Outlined.Explore),
        Triple("AI Assistant", Icons.Filled.AutoAwesome, Icons.Outlined.AutoAwesome),
        Triple("Learn", Icons.Filled.School, Icons.Outlined.School),
        Triple("Profile", Icons.Filled.Person, Icons.Outlined.Person)
    )

    Scaffold(
        contentWindowInsets = WindowInsets.safeDrawing,
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
        bottomBar = {
            NavigationBar(
                containerColor = MaterialTheme.colorScheme.surface
            ) {
                navItems.forEachIndexed { index, (label, filledIcon, outlinedIcon) ->
                    val selected = currentTab == index
                    NavigationBarItem(
                        selected = selected,
                        onClick = {
                            selectedCareerDetailId = null
                            showCompareCareers = false
                            currentTab = index
                        },
                        icon = {
                            Icon(
                                imageVector = if (selected) filledIcon else outlinedIcon,
                                contentDescription = label
                            )
                        },
                        label = { Text(label) },
                        modifier = Modifier.testTag("bottom_nav_${label.lowercase().replace(" ", "_")}")
                    )
                }
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            when (currentTab) {
                0 -> HomeDashboardScreen(
                    profile = profile,
                    careerMatches = careerMatches,
                    goals = goals,
                    unreadNotificationCount = notifications.count { !it.isRead },
                    dailyChallengeCompleted = dailyChallengeCompleted,
                    onOpenNotifications = { showNotificationsSheet = true },
                    onOpenAssessment = {
                        learnSubTab = 2
                        currentTab = 3
                    },
                    onOpenSkillGap = {
                        learnSubTab = 1
                        currentTab = 3
                    },
                    onOpenResumeLab = {
                        aiSubTab = 1
                        currentTab = 2
                    },
                    onOpenMockInterview = {
                        aiSubTab = 2
                        currentTab = 2
                    },
                    onOpenCompareCareers = {
                        showCompareCareers = true
                        currentTab = 1
                    },
                    onOpenGamification = {
                        profileSubTab = 1
                        currentTab = 4
                    },
                    onOpenAnalytics = {
                        profileSubTab = 2
                        currentTab = 4
                    },
                    onExportPdfReport = {
                        PdfReportGenerator.exportAndPrintCareerReport(
                            context = context,
                            profile = profile,
                            topCareers = careerMatches,
                            assessmentSummary = assessmentDimensions,
                            onResult = { msg -> viewModel.postBannerMessage(msg) }
                        )
                    },
                    onSelectCareerDetail = { careerId ->
                        viewModel.recordCareerViewed(careerId)
                        selectedCareerDetailId = careerId
                        currentTab = 1
                    },
                    onToggleBookmarkCareer = { careerId ->
                        viewModel.toggleBookmarkCareer(careerId)
                    },
                    onCompleteDailyChallenge = {
                        viewModel.completeDailyChallenge()
                    },
                    onNavigateToTab = { tabIdx -> currentTab = tabIdx }
                )

                1 -> ExploreCareersScreen(
                    profile = profile,
                    careerMatches = careerMatches,
                    selectedCareerDetailId = selectedCareerDetailId,
                    showCompareSheet = showCompareCareers,
                    compareCareerIds = compareCareerIds,
                    bookmarkedCertIds = bookmarkedCertIds,
                    completedCertIds = completedCertIds,
                    savedInternshipIds = savedInternshipIds,
                    internshipStatuses = internshipStatuses,
                    onSelectCareerDetail = { id ->
                        if (id != null) viewModel.recordCareerViewed(id)
                        selectedCareerDetailId = id
                    },
                    onToggleCompareSheet = { show -> showCompareCareers = show },
                    onUpdateCompareCareers = { a, b -> viewModel.setCompareCareers(a, b) },
                    onToggleBookmarkCareer = { id -> viewModel.toggleBookmarkCareer(id) },
                    onSetTargetCareer = { id ->
                        viewModel.selectTargetCareer(id)
                        viewModel.postBannerMessage("Updated primary target career roadmap!")
                    },
                    onToggleCertBookmark = { id -> viewModel.toggleCertificationBookmark(id) },
                    onToggleCertCompleted = { id -> viewModel.toggleCertificationCompleted(id) },
                    onUpdateInternshipStatus = { id, status -> viewModel.updateInternshipStatus(id, status) }
                )

                2 -> AiAssistantHubScreen(
                    initialSubTab = aiSubTab,
                    profile = profile,
                    chatMessages = chatMessages,
                    aiChatState = aiChatState,
                    resumeState = resumeState,
                    interviewFeedbackState = interviewFeedbackState,
                    onSendMessage = { prompt -> viewModel.sendChatMessage(prompt) },
                    onClearChat = { viewModel.clearChatHistory() },
                    onAnalyzeResume = { fileName, text -> viewModel.analyzeResumeWithAi(fileName, text) },
                    onEvaluateInterview = { role, round, diff, q, ans ->
                        viewModel.evaluateInterviewAnswer(role, round, diff, q, ans)
                    },
                    onResetInterviewFeedback = { viewModel.resetInterviewFeedback() },
                    onCopyFeedback = { msg -> viewModel.postBannerMessage(msg) }
                )

                3 -> LearnRoadmapScreen(
                    initialSubTab = learnSubTab,
                    profile = profile,
                    goals = goals,
                    assessmentAnswers = assessmentAnswers,
                    assessmentDimensions = assessmentDimensions,
                    onSelectTargetCareer = { id -> viewModel.selectTargetCareer(id) },
                    onToggleModuleCompleted = { modId -> viewModel.toggleRoadmapModuleCompleted(modId) },
                    onResetRoadmap = { viewModel.resetRoadmapProgress() },
                    onAddCustomTask = { title, sub ->
                        viewModel.addCustomGoalOrTask(title, "roadmap_task", sub)
                    },
                    onToggleGoalCompleted = { id, done -> viewModel.toggleGoalItem(id, done) },
                    onUpdateSkillProficiency = { skill, pct, isTech ->
                        viewModel.updateSkillProficiency(skill, pct, isTech)
                    },
                    onSelectAssessmentAnswer = { qId, optIdx ->
                        viewModel.selectAssessmentAnswer(qId, optIdx)
                    },
                    onSubmitAssessment = {
                        viewModel.submitAssessment()
                        viewModel.postBannerMessage("Assessment scored! Career matches & readiness updated.")
                    }
                )

                4 -> ProfileSettingsScreen(
                    initialSubTab = profileSubTab,
                    profile = profile,
                    goals = goals,
                    isDarkTheme = isDarkTheme,
                    onToggleDarkTheme = onToggleDarkTheme,
                    onSaveProfile = { updated -> viewModel.updateProfile(updated) },
                    onAddCustomGoal = { title, cat, sub ->
                        viewModel.addCustomGoalOrTask(title, cat, sub)
                    },
                    onExportCareerReportPdf = {
                        PdfReportGenerator.exportAndPrintCareerReport(
                            context = context,
                            profile = profile,
                            topCareers = careerMatches,
                            assessmentSummary = assessmentDimensions,
                            onResult = { msg -> viewModel.postBannerMessage(msg) }
                        )
                    },
                    onSignOut = onSignOut
                )
            }
        }
    }

    if (showNotificationsSheet) {
        ModalBottomSheet(
            onDismissRequest = { showNotificationsSheet = false },
            shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp),
            containerColor = MaterialTheme.colorScheme.surface
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 12.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Notifications & Milestones",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )
                    TextButton(onClick = { viewModel.markAllNotificationsRead() }) {
                        Icon(Icons.Default.DoneAll, contentDescription = null)
                        Text(" Mark All Read")
                    }
                }
                notifications.forEach { n ->
                    GlassCard {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            MetricPill(text = n.category, color = if (n.isRead) ElectricBlue else CyanAccent)
                            Text(n.timeAgo, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(n.title, style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
                        Text(n.body, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                }
                Spacer(modifier = Modifier.height(20.dp))
            }
        }
    }
}
