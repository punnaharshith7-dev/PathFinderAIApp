package com.example.ui.screens

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.DeleteSweep
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.UploadFile
import androidx.compose.material3.CircularProgressIndicator
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
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.data.catalog.CareerCatalog
import com.example.data.model.UserProfile
import com.example.ui.components.AnimatedCircularScore
import com.example.ui.components.GlassCard
import com.example.ui.components.GradientButton
import com.example.ui.components.MetricPill
import com.example.ui.components.SectionHeader
import com.example.ui.theme.CyanAccent
import com.example.ui.theme.ElectricBlue
import com.example.ui.theme.ErrorRose
import com.example.ui.theme.PrimaryPurple
import com.example.ui.theme.SuccessEmerald
import com.example.ui.theme.WarningAmber
import com.example.viewmodel.ChatMessage
import com.example.viewmodel.InterviewFeedbackResult
import com.example.viewmodel.ResumeAnalysisResult
import com.example.viewmodel.UiState

@Composable
fun AiAssistantHubScreen(
    initialSubTab: Int,
    profile: UserProfile,
    chatMessages: List<ChatMessage>,
    aiChatState: UiState<String>,
    resumeState: UiState<ResumeAnalysisResult>,
    interviewFeedbackState: UiState<InterviewFeedbackResult>,
    onSendMessage: (String) -> Unit,
    onClearChat: () -> Unit,
    onAnalyzeResume: (String, String) -> Unit,
    onEvaluateInterview: (String, String, String, String, String) -> Unit,
    onResetInterviewFeedback: () -> Unit,
    onCopyFeedback: (String) -> Unit
) {
    var selectedTab by remember(initialSubTab) { mutableIntStateOf(initialSubTab) }

    Column(modifier = Modifier.fillMaxSize()) {
        ScrollableTabRow(
            selectedTabIndex = selectedTab,
            edgePadding = 16.dp,
            containerColor = MaterialTheme.colorScheme.surface
        ) {
            listOf(
                "AI Career Assistant",
                "Resume Lab",
                "Mock Interview"
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
            0 -> AiChatSubScreen(
                chatMessages = chatMessages,
                aiChatState = aiChatState,
                onSendMessage = onSendMessage,
                onClearChat = onClearChat,
                onCopyText = onCopyFeedback
            )
            1 -> ResumeLabSubScreen(
                profile = profile,
                resumeState = resumeState,
                onAnalyzeResume = onAnalyzeResume
            )
            2 -> MockInterviewSubScreen(
                profile = profile,
                interviewFeedbackState = interviewFeedbackState,
                onEvaluateInterview = onEvaluateInterview,
                onResetFeedback = onResetInterviewFeedback
            )
        }
    }
}

@Composable
private fun AiChatSubScreen(
    chatMessages: List<ChatMessage>,
    aiChatState: UiState<String>,
    onSendMessage: (String) -> Unit,
    onClearChat: () -> Unit,
    onCopyText: (String) -> Unit
) {
    var inputPrompt by remember { mutableStateOf("") }
    val clipboardManager = LocalClipboardManager.current

    val suggestedPrompts = listOf(
        "Which career suits my skills?",
        "How can I become an AI engineer?",
        "Compare software development and data science",
        "What skills am I missing?",
        "Create a 6-month learning roadmap",
        "How should I prepare for placements?",
        "Review my career goals"
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp, vertical = 10.dp)
    ) {
        // Header row with Clear action
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.AutoAwesome,
                    contentDescription = null,
                    tint = ElectricBlue,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "PATHFINDER AI ASSISTANT (Gemini 3.8)",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onBackground
                )
            }
            IconButton(
                onClick = onClearChat,
                modifier = Modifier.testTag("clear_chat_button")
            ) {
                Icon(
                    imageVector = Icons.Default.DeleteSweep,
                    contentDescription = "Clear conversation",
                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        // Suggested Prompts horizontal chips
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState())
                .padding(vertical = 6.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            suggestedPrompts.forEach { prompt ->
                FilterChip(
                    selected = false,
                    onClick = { onSendMessage(prompt) },
                    label = { Text(prompt, style = MaterialTheme.typography.labelSmall) }
                )
            }
        }

        // Messages list
        LazyColumn(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            contentPadding = PaddingValues(vertical = 8.dp)
        ) {
            items(chatMessages, key = { it.id }) { msg ->
                val isUser = msg.sender == "user"
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = if (isUser) Arrangement.End else Arrangement.Start,
                    verticalAlignment = Alignment.Top
                ) {
                    if (!isUser) {
                        Surface(
                            shape = CircleShape,
                            color = PrimaryPurple.copy(alpha = 0.25f),
                            modifier = Modifier.size(34.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(
                                    imageVector = Icons.Default.AutoAwesome,
                                    contentDescription = "AI Avatar",
                                    tint = CyanAccent,
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                    }

                    GlassCard(
                        modifier = Modifier.weight(1f, fill = false),
                        containerColor = if (isUser) {
                            PrimaryPurple.copy(alpha = 0.28f)
                        } else {
                            MaterialTheme.colorScheme.surfaceVariant
                        },
                        borderColor = if (isUser) PrimaryPurple else MaterialTheme.colorScheme.outline.copy(alpha = 0.5f),
                        contentPadding = PaddingValues(14.dp)
                    ) {
                        Text(
                            text = msg.text,
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                text = if (msg.isStreaming) "Streaming response..." else msg.timestamp,
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            if (!isUser && !msg.isStreaming) {
                                IconButton(
                                    onClick = {
                                        clipboardManager.setText(AnnotatedString(msg.text))
                                        onCopyText("Response copied to clipboard")
                                    },
                                    modifier = Modifier.size(26.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.ContentCopy,
                                        contentDescription = "Copy response",
                                        tint = ElectricBlue,
                                        modifier = Modifier.size(15.dp)
                                    )
                                }
                            }
                        }
                    }

                    if (isUser) {
                        Spacer(modifier = Modifier.width(8.dp))
                        Surface(
                            shape = CircleShape,
                            color = ElectricBlue.copy(alpha = 0.25f),
                            modifier = Modifier.size(34.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(
                                    imageVector = Icons.Default.Person,
                                    contentDescription = "User Avatar",
                                    tint = ElectricBlue,
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                        }
                    }
                }
            }
        }

        if (aiChatState is UiState.Error) {
            Text(
                text = aiChatState.message,
                style = MaterialTheme.typography.labelSmall,
                color = ErrorRose,
                modifier = Modifier.padding(vertical = 4.dp)
            )
        }

        // Message Input Bar
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 6.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            OutlinedTextField(
                value = inputPrompt,
                onValueChange = { inputPrompt = it },
                placeholder = { Text("Ask Pathfinder AI about careers, skills, or roadmaps...") },
                shape = RoundedCornerShape(18.dp),
                modifier = Modifier
                    .weight(1f)
                    .testTag("ai_chat_input")
            )
            Spacer(modifier = Modifier.width(8.dp))
            IconButton(
                onClick = {
                    if (inputPrompt.isNotBlank()) {
                        onSendMessage(inputPrompt)
                        inputPrompt = ""
                    }
                },
                modifier = Modifier
                    .size(50.dp)
                    .testTag("ai_chat_send_button")
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.Send,
                    contentDescription = "Send prompt",
                    tint = ElectricBlue
                )
            }
        }
    }
}

@Composable
private fun ResumeLabSubScreen(
    profile: UserProfile,
    resumeState: UiState<ResumeAnalysisResult>,
    onAnalyzeResume: (String, String) -> Unit
) {
    var selectedFileName by remember { mutableStateOf("") }
    var resumeTextSummary by remember { mutableStateOf("") }

    val pdfPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        if (uri != null) {
            selectedFileName = uri.lastPathSegment?.substringAfterLast("/") ?: "Uploaded_Resume.pdf"
        }
    }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(18.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            SectionHeader(
                title = "AI Resume Lab",
                subtitle = "Select a PDF resume & run Gemini structured gap analysis"
            )
        }

        item {
            GlassCard {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                        Icon(
                            imageVector = Icons.Default.Description,
                            contentDescription = null,
                            tint = ElectricBlue,
                            modifier = Modifier.size(28.dp)
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                text = selectedFileName.ifBlank { "No PDF resume selected yet" },
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "Select a PDF or paste your resume summary below",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                    OutlinedButton(
                        onClick = { pdfPickerLauncher.launch("application/pdf") },
                        modifier = Modifier.testTag("pick_resume_pdf_button")
                    ) {
                        Icon(Icons.Default.UploadFile, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Select PDF")
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))
                OutlinedTextField(
                    value = resumeTextSummary,
                    onValueChange = { resumeTextSummary = it },
                    label = { Text("Resume Highlights / Extracted Text Preview") },
                    minLines = 3,
                    maxLines = 6,
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(14.dp))
                GradientButton(
                    text = if (resumeState is UiState.Loading) "Analyzing with Gemini AI..." else "Analyze Resume with AI",
                    icon = Icons.Default.AutoAwesome,
                    enabled = resumeState !is UiState.Loading,
                    onClick = { onAnalyzeResume(selectedFileName, resumeTextSummary) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("analyze_resume_button")
                )
            }
        }

        when (resumeState) {
            is UiState.Loading -> {
                item {
                    Box(modifier = Modifier.fillMaxWidth().padding(24.dp), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator(color = ElectricBlue)
                    }
                }
            }
            is UiState.Error -> {
                item {
                    GlassCard(borderColor = ErrorRose) {
                        Text("Resume Analysis Error", style = MaterialTheme.typography.titleSmall, color = ErrorRose, fontWeight = FontWeight.Bold)
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(resumeState.message, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurface)
                    }
                }
            }
            is UiState.Success -> {
                val data = resumeState.data
                item {
                    GlassCard(borderColor = SuccessEmerald.copy(alpha = 0.55f)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                MetricPill(text = "RESUME SCORE", color = SuccessEmerald)
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(
                                    text = data.careerSuitability,
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = data.educationSummary,
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                            AnimatedCircularScore(
                                score = data.completenessScore,
                                label = "Completeness",
                                size = 105.dp
                            )
                        }
                    }
                }

                item {
                    GlassCard {
                        Text("Identified Technical & Soft Skills", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                        Spacer(modifier = Modifier.height(6.dp))
                        Text("Technical: ${data.extractedTechSkills.joinToString(" • ")}", style = MaterialTheme.typography.bodySmall, color = CyanAccent)
                        Spacer(modifier = Modifier.height(4.dp))
                        Text("Soft Skills: ${data.extractedSoftSkills.joinToString(" • ")}", style = MaterialTheme.typography.bodySmall, color = ElectricBlue)
                    }
                }

                item {
                    GlassCard {
                        Text("Suggested Improvements & Missing Sections", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                        Spacer(modifier = Modifier.height(8.dp))
                        data.suggestedImprovements.forEach { tip ->
                            Text("✓ $tip", style = MaterialTheme.typography.bodySmall, color = SuccessEmerald, modifier = Modifier.padding(vertical = 3.dp))
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        data.missingSections.forEach { miss ->
                            Text("⚠ Missing: $miss", style = MaterialTheme.typography.bodySmall, color = WarningAmber, modifier = Modifier.padding(vertical = 2.dp))
                        }
                    }
                }
            }
            else -> {}
        }
    }
}

@Composable
private fun MockInterviewSubScreen(
    profile: UserProfile,
    interviewFeedbackState: UiState<InterviewFeedbackResult>,
    onEvaluateInterview: (String, String, String, String, String) -> Unit,
    onResetFeedback: () -> Unit
) {
    var selectedCareerId by remember { mutableStateOf(profile.targetCareerId) }
    var selectedDifficulty by remember { mutableStateOf("Intermediate") }
    var questionIndex by remember { mutableIntStateOf(0) }
    var userAnswer by remember { mutableStateOf("") }

    val careerTitle = CareerCatalog.careers.firstOrNull { it.id == selectedCareerId }?.title ?: "AI/ML Engineer"
    val matchingQuestions = CareerCatalog.interviewQuestions.filter {
        it.careerId == selectedCareerId || it.careerId == "all"
    }.ifEmpty { CareerCatalog.interviewQuestions }

    val activeQuestion = matchingQuestions[questionIndex % matchingQuestions.size]

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(18.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            SectionHeader(
                title = "AI Mock Interview Simulator",
                subtitle = "Practice technical, behavioral & HR rounds with instant scoring"
            )
        }

        item {
            Text("Target Role:", style = MaterialTheme.typography.labelMedium, color = ElectricBlue)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                CareerCatalog.careers.take(6).forEach { c ->
                    FilterChip(
                        selected = selectedCareerId == c.id,
                        onClick = {
                            selectedCareerId = c.id
                            questionIndex = 0
                            onResetFeedback()
                        },
                        label = { Text(c.title) }
                    )
                }
            }
            Spacer(modifier = Modifier.height(6.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                listOf("Beginner", "Intermediate", "Advanced").forEach { diff ->
                    FilterChip(
                        selected = selectedDifficulty == diff,
                        onClick = { selectedDifficulty = diff },
                        label = { Text(diff) }
                    )
                }
            }
        }

        item {
            GlassCard(borderColor = PrimaryPurple.copy(alpha = 0.6f)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    MetricPill(text = activeQuestion.roundType, color = CyanAccent)
                    OutlinedButton(onClick = {
                        questionIndex++
                        userAnswer = ""
                        onResetFeedback()
                    }) {
                        Text("Next Question")
                    }
                }
                Spacer(modifier = Modifier.height(10.dp))
                Text(
                    text = activeQuestion.question,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Key Topics to Cover: ${activeQuestion.keyTopics.joinToString(" • ")}",
                    style = MaterialTheme.typography.labelSmall,
                    color = ElectricBlue
                )
                Spacer(modifier = Modifier.height(14.dp))
                OutlinedTextField(
                    value = userAnswer,
                    onValueChange = { userAnswer = it },
                    label = { Text("Your Interview Response") },
                    placeholder = { Text("Structure your answer using technical specifics or the STAR method...") },
                    minLines = 4,
                    maxLines = 8,
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("mock_interview_answer_input")
                )
                Spacer(modifier = Modifier.height(12.dp))
                GradientButton(
                    text = if (interviewFeedbackState is UiState.Loading) "Evaluating Response..." else "Submit Answer for AI Feedback",
                    icon = Icons.Default.Mic,
                    enabled = interviewFeedbackState !is UiState.Loading,
                    onClick = {
                        onEvaluateInterview(
                            careerTitle,
                            activeQuestion.roundType,
                            selectedDifficulty,
                            activeQuestion.question,
                            userAnswer
                        )
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("submit_interview_answer_button")
                )
            }
        }

        when (interviewFeedbackState) {
            is UiState.Loading -> {
                item {
                    Box(modifier = Modifier.fillMaxWidth().padding(20.dp), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator(color = ElectricBlue)
                    }
                }
            }
            is UiState.Error -> {
                item {
                    GlassCard(borderColor = ErrorRose) {
                        Text(interviewFeedbackState.message, style = MaterialTheme.typography.bodySmall, color = ErrorRose)
                    }
                }
            }
            is UiState.Success -> {
                val fb = interviewFeedbackState.data
                item {
                    GlassCard(borderColor = SuccessEmerald.copy(alpha = 0.6f)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                MetricPill(text = "AI INTERVIEW SCORECARD", color = SuccessEmerald)
                                Spacer(modifier = Modifier.height(8.dp))
                                Text("Clarity: ${fb.clarityScore}/100", style = MaterialTheme.typography.bodySmall)
                                Text("Technical Depth: ${fb.technicalDepthScore}/100", style = MaterialTheme.typography.bodySmall)
                                Text("STAR Structure: ${fb.starStructureScore}/100", style = MaterialTheme.typography.bodySmall)
                            }
                            AnimatedCircularScore(
                                score = fb.overallScore,
                                label = "Overall",
                                size = 96.dp
                            )
                        }
                        Spacer(modifier = Modifier.height(12.dp))
                        Text("Strengths:", style = MaterialTheme.typography.labelLarge, color = SuccessEmerald)
                        fb.strengths.forEach { s ->
                            Text("• $s", style = MaterialTheme.typography.bodySmall)
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        Text("Areas to Improve:", style = MaterialTheme.typography.labelLarge, color = WarningAmber)
                        fb.improvements.forEach { imp ->
                            Text("• $imp", style = MaterialTheme.typography.bodySmall)
                        }
                        if (fb.modelSampleResponse.isNotBlank()) {
                            Spacer(modifier = Modifier.height(10.dp))
                            Text("High-Scoring Sample Answer:", style = MaterialTheme.typography.labelLarge, color = CyanAccent)
                            Text(fb.modelSampleResponse, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                    }
                }
            }
            else -> {
                item {
                    GlassCard {
                        Text("Reference Talking Points for This Question", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
                        Spacer(modifier = Modifier.height(6.dp))
                        activeQuestion.sampleIdealPoints.forEach { pt ->
                            Text("• $pt", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.padding(vertical = 2.dp))
                        }
                    }
                }
            }
        }
    }
}
