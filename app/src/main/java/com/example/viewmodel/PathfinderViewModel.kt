package com.example.viewmodel

import android.content.Context
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.R
import com.example.data.catalog.CareerCatalog
import com.example.data.catalog.CareerOption
import com.example.data.model.CareerGoalItem
import com.example.data.model.UserProfile
import com.example.data.repository.PathfinderRepository
import com.google.firebase.Firebase
import com.google.firebase.ai.ai
import com.google.firebase.ai.type.Schema
import com.google.firebase.ai.type.generationConfig
import com.google.firebase.auth.auth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.TimeoutCancellationException
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withTimeout
import org.json.JSONObject

private const val TAG = "PathfinderVM"

sealed interface UiState<out T> {
    data object Idle : UiState<Nothing>
    data object Loading : UiState<Nothing>
    data class Streaming(val text: String) : UiState<Nothing>
    data class Success<T>(val data: T) : UiState<T>
    data class Error(val message: String) : UiState<Nothing>
}

data class ChatMessage(
    val id: String,
    val sender: String, // "user" or "ai"
    val text: String,
    val timestamp: String,
    val isStreaming: Boolean = false
)

data class ResumeAnalysisResult(
    val fileName: String,
    val completenessScore: Int,
    val careerSuitability: String,
    val extractedTechSkills: List<String>,
    val extractedSoftSkills: List<String>,
    val educationSummary: String,
    val projectHighlights: List<String>,
    val missingSections: List<String>,
    val suggestedImprovements: List<String>,
    val skillGapRecommendations: List<String>
)

data class InterviewFeedbackResult(
    val overallScore: Int,
    val clarityScore: Int,
    val technicalDepthScore: Int,
    val starStructureScore: Int,
    val strengths: List<String>,
    val improvements: List<String>,
    val modelSampleResponse: String
)

data class AppNotificationItem(
    val id: String,
    val title: String,
    val body: String,
    val category: String,
    val timeAgo: String,
    val isRead: Boolean = false
)

class PathfinderViewModel(
    private val repository: PathfinderRepository,
    private val currentUserId: String,
    appContext: Context
) : ViewModel() {

    private val prefs = appContext.getSharedPreferences("pathfinder_prefs_$currentUserId", Context.MODE_PRIVATE)

    private fun buildFreshUserProfile(): UserProfile {
        val firebaseUser = try {
            Firebase.auth.currentUser
        } catch (e: Exception) {
            null
        }
        val realEmail = firebaseUser?.email?.takeIf { it.isNotBlank() } ?: ""
        val fallbackNameFromEmail = realEmail.substringBefore("@").replace(".", " ").replaceFirstChar { it.uppercase() }
        val realName = firebaseUser?.displayName?.takeIf { it.isNotBlank() }
            ?: fallbackNameFromEmail.takeIf { it.isNotBlank() }
            ?: "New Student"

        return UserProfile(
            userId = currentUserId,
            fullName = realName,
            email = realEmail
        )
    }

    // Lazy initialization of Firebase AI Logic Gemini 3.8 Flash models
    private val chatModel by lazy {
        Firebase.ai.generativeModel("gemini-3.8-flash")
    }

    private val resumeAnalysisSchema = Schema.obj(
        properties = mapOf(
            "completenessScore" to Schema.integer(description = "Resume completeness score from 0 to 100"),
            "careerSuitability" to Schema.string(description = "Best matching career path and suitability summary"),
            "extractedTechSkills" to Schema.array(Schema.string(), description = "Identified technical skills"),
            "extractedSoftSkills" to Schema.array(Schema.string(), description = "Identified soft skills"),
            "educationSummary" to Schema.string(description = "Extracted education details"),
            "projectHighlights" to Schema.array(Schema.string(), description = "Project and experience analysis points"),
            "missingSections" to Schema.array(Schema.string(), description = "Missing or weak resume sections"),
            "suggestedImprovements" to Schema.array(Schema.string(), description = "Actionable resume improvements"),
            "skillGapRecommendations" to Schema.array(Schema.string(), description = "Skills to add for target career")
        )
    )

    private val resumeModel by lazy {
        Firebase.ai.generativeModel(
            modelName = "gemini-3.8-flash",
            generationConfig = generationConfig {
                responseMimeType = "application/json"
                responseSchema = resumeAnalysisSchema
            }
        )
    }

    private val interviewFeedbackSchema = Schema.obj(
        properties = mapOf(
            "overallScore" to Schema.integer(description = "Overall answer quality score 0-100"),
            "clarityScore" to Schema.integer(description = "Communication clarity score 0-100"),
            "technicalDepthScore" to Schema.integer(description = "Technical depth score 0-100"),
            "starStructureScore" to Schema.integer(description = "Structure and impact score 0-100"),
            "strengths" to Schema.array(Schema.string(), description = "Key strengths in the student's answer"),
            "improvements" to Schema.array(Schema.string(), description = "Specific ways to improve the answer"),
            "modelSampleResponse" to Schema.string(description = "Polished high-scoring sample response")
        )
    )

    private val interviewModel by lazy {
        Firebase.ai.generativeModel(
            modelName = "gemini-3.8-flash",
            generationConfig = generationConfig {
                responseMimeType = "application/json"
                responseSchema = interviewFeedbackSchema
            }
        )
    }

    private val _localProfile = MutableStateFlow(buildFreshUserProfile())
    val localProfile: StateFlow<UserProfile> = _localProfile.asStateFlow()

    val profileState: StateFlow<UiState<UserProfile>> = repository.observeUserProfile(currentUserId)
        .map<UserProfile?, UiState<UserProfile>> { remoteProfile ->
            if (remoteProfile != null &&
                remoteProfile.fullName != "Aarav Mehta" &&
                remoteProfile.email != "aarav.mehta@university.edu"
            ) {
                _localProfile.value = remoteProfile
                UiState.Success(remoteProfile)
            } else {
                val fresh = buildFreshUserProfile()
                _localProfile.value = fresh
                UiState.Success(fresh)
            }
        }
        .catch { e ->
            Log.w(TAG, "Error observing user profile", e)
            emit(UiState.Error(e.message ?: "Failed to observe user profile"))
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000L),
            initialValue = UiState.Loading
        )

    val goalsState: StateFlow<UiState<List<CareerGoalItem>>> = repository.observeGoals(currentUserId)
        .map<List<CareerGoalItem>, UiState<List<CareerGoalItem>>> { list ->
            UiState.Success(list)
        }
        .catch { e ->
            Log.w(TAG, "Error observing goals", e)
            emit(UiState.Error(e.message ?: "Failed to observe goals"))
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000L),
            initialValue = UiState.Loading
        )

    // Assessment state (empty until user takes assessment)
    private val _assessmentAnswers = MutableStateFlow<Map<String, Int>>(emptyMap())
    val assessmentAnswers: StateFlow<Map<String, Int>> = _assessmentAnswers.asStateFlow()

    private val _assessmentDimensions = MutableStateFlow<Map<String, Int>>(emptyMap())
    val assessmentDimensions: StateFlow<Map<String, Int>> = _assessmentDimensions.asStateFlow()

    // Recently viewed careers & Compare selection
    private val _recentlyViewedCareerIds = MutableStateFlow<List<String>>(emptyList())
    val recentlyViewedCareerIds: StateFlow<List<String>> = _recentlyViewedCareerIds.asStateFlow()

    private val _compareCareerIds = MutableStateFlow(Pair("ai_ml_engineer", "full_stack_dev"))
    val compareCareerIds: StateFlow<Pair<String, String>> = _compareCareerIds.asStateFlow()

    // Saved certifications & internships (empty for a new account)
    private val _bookmarkedCertIds = MutableStateFlow<Set<String>>(emptySet())
    val bookmarkedCertIds: StateFlow<Set<String>> = _bookmarkedCertIds.asStateFlow()

    private val _completedCertIds = MutableStateFlow<Set<String>>(emptySet())
    val completedCertIds: StateFlow<Set<String>> = _completedCertIds.asStateFlow()

    private val _savedInternshipIds = MutableStateFlow<Set<String>>(emptySet())
    val savedInternshipIds: StateFlow<Set<String>> = _savedInternshipIds.asStateFlow()

    private val _internshipStatuses = MutableStateFlow<Map<String, String>>(emptyMap())
    val internshipStatuses: StateFlow<Map<String, String>> = _internshipStatuses.asStateFlow()

    // AI Chat Assistant State
    private val _chatMessages = MutableStateFlow(
        listOf(
            ChatMessage(
                id = "welcome_1",
                sender = "ai",
                text = "Hello! I'm **PATHFINDER AI ASSISTANT**. Complete your student profile or take the 12-Dimension Assessment, and ask me anything about career paths, learning roadmaps, skill gaps, or interview preparation!",
                timestamp = "Just now"
            )
        )
    )
    val chatMessages: StateFlow<List<ChatMessage>> = _chatMessages.asStateFlow()

    private val _aiChatState = MutableStateFlow<UiState<String>>(UiState.Idle)
    val aiChatState: StateFlow<UiState<String>> = _aiChatState.asStateFlow()

    // Resume Analyzer State (Idle until user uploads/analyzes a resume)
    private val _resumeState = MutableStateFlow<UiState<ResumeAnalysisResult>>(UiState.Idle)
    val resumeState: StateFlow<UiState<ResumeAnalysisResult>> = _resumeState.asStateFlow()

    // Mock Interview State
    private val _interviewFeedbackState = MutableStateFlow<UiState<InterviewFeedbackResult>>(UiState.Idle)
    val interviewFeedbackState: StateFlow<UiState<InterviewFeedbackResult>> = _interviewFeedbackState.asStateFlow()

    // Notifications (Clean welcome notification only)
    private val _notifications = MutableStateFlow(
        listOf(
            AppNotificationItem(
                id = "welcome_notif",
                title = "Welcome to Pathfinder AI!",
                body = "Your Google account is connected. Complete your profile and take the 12-D Assessment to unlock personalized career matches.",
                category = "Account",
                timeAgo = "Just now"
            )
        )
    )
    val notifications: StateFlow<List<AppNotificationItem>> = _notifications.asStateFlow()

    private val _dailyChallengeCompleted = MutableStateFlow(prefs.getBoolean("daily_challenge_done", false))
    val dailyChallengeCompleted: StateFlow<Boolean> = _dailyChallengeCompleted.asStateFlow()

    private val _statusBannerMessage = MutableStateFlow<String?>(null)
    val statusBannerMessage: StateFlow<String?> = _statusBannerMessage.asStateFlow()

    init {
        viewModelScope.launch {
            val res = repository.getUserProfile(currentUserId)
            val existing = res.getOrNull()
            if (res.isSuccess) {
                if (existing == null) {
                    // Brand new Google user: create a clean profile with their real Google name & email
                    val fresh = buildFreshUserProfile()
                    _localProfile.value = fresh
                    repository.saveOrUpdateUserProfile(fresh)
                } else if (existing.fullName == "Aarav Mehta" || existing.email == "aarav.mehta@university.edu") {
                    // Automatically wipe any previously seeded trial "Aarav Mehta" profile & goals
                    val fresh = buildFreshUserProfile()
                    _localProfile.value = fresh
                    repository.saveOrUpdateUserProfile(fresh)
                    val existingGoals = repository.getUserGoals(currentUserId).getOrNull().orEmpty()
                    existingGoals.forEach { goal ->
                        if (goal.title in listOf(
                                "Complete Level 4: Transformers & LLMs",
                                "Build RAG Portfolio Capstone Project",
                                "Achieve 85%+ Career Readiness Score"
                            )
                        ) {
                            repository.deleteGoal(goal.id)
                        }
                    }
                }
            }
        }
    }

    fun clearBannerMessage() {
        _statusBannerMessage.value = null
    }

    fun postBannerMessage(msg: String) {
        _statusBannerMessage.value = msg
    }

    // Dynamic Career Match Calculation
    fun calculateCareerMatches(profile: UserProfile): List<Pair<CareerOption, Int>> {
        val userSkillMap = profile.technicalSkills.associate { entry ->
            val parts = entry.split(":")
            val name = parts.getOrNull(0)?.trim().orEmpty()
            val score = parts.getOrNull(1)?.trim()?.toIntOrNull() ?: 70
            name.lowercase() to score
        }
        val dims = _assessmentDimensions.value

        return CareerCatalog.careers.map { career ->
            if (userSkillMap.isEmpty() && dims.isEmpty()) {
                career to career.baseMatchScore
            } else {
                var skillScoreSum = 0
                var count = 0
                career.requiredTechSkills.forEach { (reqSkill, reqLevel) ->
                    val matchedUserLevel = userSkillMap.entries.firstOrNull { (k, _) ->
                        k.isNotBlank() && (k.contains(reqSkill.lowercase()) || reqSkill.lowercase().contains(k))
                    }?.value ?: 40
                    val ratio = (matchedUserLevel.toFloat() / reqLevel.toFloat()).coerceAtMost(1.08f)
                    skillScoreSum += (ratio * 100).toInt()
                    count++
                }
                val avgSkillAlign = if (count > 0) skillScoreSum / count else 60
                val dimBonus = if (dims.isEmpty()) 0 else when (career.id) {
                    "ai_ml_engineer", "data_scientist" -> (dims["AI & Data Science"] ?: 70) - 70
                    "full_stack_dev", "software_developer" -> (dims["Full Stack & Software"] ?: 70) - 70
                    "ui_ux_designer" -> (dims["Creative & Product Design"] ?: 70) - 70
                    "product_manager", "data_analyst" -> (dims["Leadership & Strategy"] ?: 70) - 70
                    else -> (dims["Cloud & Security"] ?: 70) - 70
                }.coerceIn(-10, 12)

                val computed = ((career.baseMatchScore * 0.5f) + (avgSkillAlign * 0.5f) + dimBonus)
                    .toInt()
                    .coerceIn(50, 98)
                career to computed
            }
        }.sortedByDescending { it.second }
    }

    fun updateProfile(updated: UserProfile) {
        _localProfile.value = updated
        viewModelScope.launch {
            val res = repository.saveOrUpdateUserProfile(updated)
            if (res.isSuccess) {
                _statusBannerMessage.value = "Profile saved and synced to cloud."
            } else {
                _statusBannerMessage.value = res.exceptionOrNull()?.localizedMessage ?: "Failed to sync profile."
            }
        }
    }

    fun updateSkillProficiency(skillName: String, newProficiency: Int, isTechnical: Boolean = true) {
        val current = _localProfile.value
        val clamped = newProficiency.coerceIn(10, 100)
        val updatedProfile = if (isTechnical) {
            val existingIdx = current.technicalSkills.indexOfFirst {
                it.substringBefore(":").equals(skillName, ignoreCase = true)
            }
            val newList = current.technicalSkills.toMutableList()
            if (existingIdx >= 0) {
                newList[existingIdx] = "$skillName:$clamped"
            } else {
                newList.add("$skillName:$clamped")
            }
            val highSkillsCount = newList.count { (it.substringAfter(":", "0").toIntOrNull() ?: 0) >= 80 }
            val badges = current.unlockedBadgeIds.toMutableSet()
            if (highSkillsCount >= 3) badges.add("badge_skill_master")
            current.copy(
                technicalSkills = newList,
                xpPoints = current.xpPoints + 25L,
                readinessScore = (current.readinessScore + 3L).coerceAtMost(99L),
                unlockedBadgeIds = badges.toList()
            )
        } else {
            val existingIdx = current.softSkills.indexOfFirst {
                it.substringBefore(":").equals(skillName, ignoreCase = true)
            }
            val newList = current.softSkills.toMutableList()
            if (existingIdx >= 0) {
                newList[existingIdx] = "$skillName:$clamped"
            } else {
                newList.add("$skillName:$clamped")
            }
            current.copy(
                softSkills = newList,
                xpPoints = current.xpPoints + 20L
            )
        }
        updateProfile(updatedProfile)
    }

    fun selectTargetCareer(careerId: String) {
        val current = _localProfile.value
        updateProfile(current.copy(targetCareerId = careerId))
    }

    fun toggleBookmarkCareer(careerId: String) {
        val current = _localProfile.value
        val currentBookmarks = current.bookmarkedCareerIds.toMutableList()
        if (currentBookmarks.contains(careerId)) {
            currentBookmarks.remove(careerId)
        } else {
            currentBookmarks.add(careerId)
        }
        val badges = current.unlockedBadgeIds.toMutableSet()
        if (currentBookmarks.size >= 3) badges.add("badge_career_explorer")
        updateProfile(
            current.copy(
                bookmarkedCareerIds = currentBookmarks,
                unlockedBadgeIds = badges.toList()
            )
        )
    }

    fun recordCareerViewed(careerId: String) {
        _recentlyViewedCareerIds.update { list ->
            (listOf(careerId) + list.filter { it != careerId }).take(6)
        }
    }

    fun setCompareCareers(firstId: String, secondId: String) {
        _compareCareerIds.value = Pair(firstId, secondId)
    }

    fun toggleRoadmapModuleCompleted(moduleId: String) {
        val current = _localProfile.value
        val completed = current.completedLessonIds.toMutableList()
        val added: Boolean
        if (completed.contains(moduleId)) {
            completed.remove(moduleId)
            added = false
        } else {
            completed.add(moduleId)
            added = true
        }
        val badges = current.unlockedBadgeIds.toMutableSet()
        if (completed.size >= 3) badges.add("badge_roadmap_starter")
        val newXp = if (added) current.xpPoints + 75L else (current.xpPoints - 75L).coerceAtLeast(0L)
        val newReadiness = (completed.size * 8L + if (current.assessmentCompleted) 20L else 0L).coerceIn(0L, 99L)
        updateProfile(
            current.copy(
                completedLessonIds = completed,
                xpPoints = newXp,
                readinessScore = newReadiness,
                unlockedBadgeIds = badges.toList()
            )
        )
    }

    fun resetRoadmapProgress() {
        val current = _localProfile.value
        updateProfile(current.copy(completedLessonIds = emptyList()))
    }

    fun addCustomGoalOrTask(title: String, category: String, subtitle: String, score: Int = 0) {
        viewModelScope.launch {
            val res = repository.createGoal(
                title = title,
                category = category,
                subtitle = subtitle,
                completed = false,
                score = score
            )
            if (res.isSuccess) {
                val current = _localProfile.value
                updateProfile(current.copy(xpPoints = current.xpPoints + 30L))
            } else {
                _statusBannerMessage.value = res.exceptionOrNull()?.localizedMessage ?: "Could not add goal."
            }
        }
    }

    fun toggleGoalItem(goalId: String, completed: Boolean) {
        viewModelScope.launch {
            repository.toggleGoalCompleted(goalId, completed)
        }
    }

    fun deleteGoalItem(goalId: String) {
        viewModelScope.launch {
            repository.deleteGoal(goalId)
        }
    }

    fun completeDailyChallenge() {
        if (!_dailyChallengeCompleted.value) {
            _dailyChallengeCompleted.value = true
            prefs.edit().putBoolean("daily_challenge_done", true).apply()
            val current = _localProfile.value
            updateProfile(
                current.copy(
                    xpPoints = current.xpPoints + 50L,
                    streakDays = current.streakDays + 1L
                )
            )
            _statusBannerMessage.value = "Daily Challenge completed! +50 XP & Streak extended!"
        }
    }

    // Assessment Scoring Engine
    fun selectAssessmentAnswer(questionId: String, optionIndex: Int) {
        _assessmentAnswers.update { it + (questionId to optionIndex) }
    }

    fun submitAssessment() {
        val answers = _assessmentAnswers.value
        val tallies = mutableMapOf(
            "AI" to 40,
            "FullStack" to 40,
            "Analytical" to 40,
            "Creative" to 40,
            "Design" to 40,
            "Leadership" to 40,
            "Business" to 40,
            "Cloud" to 40,
            "Security" to 40,
            "Tech" to 40,
            "Communication" to 40
        )

        CareerCatalog.assessmentQuestions.forEach { q ->
            val selectedIdx = answers[q.id] ?: 0
            val opt = q.options.getOrNull(selectedIdx) ?: q.options.first()
            opt.dimensionWeights.forEach { (dim, weight) ->
                tallies[dim] = (tallies[dim] ?: 40) + weight
            }
        }

        val normalized = mapOf(
            "AI & Data Science" to ((tallies["AI"] ?: 60) * 100 / 210).coerceIn(55, 98),
            "Full Stack & Software" to (((tallies["FullStack"] ?: 60) + (tallies["Tech"] ?: 60)) * 100 / 320).coerceIn(55, 98),
            "Analytical Thinking" to ((tallies["Analytical"] ?: 60) * 100 / 210).coerceIn(55, 98),
            "Creative & Product Design" to (((tallies["Creative"] ?: 60) + (tallies["Design"] ?: 60)) * 100 / 300).coerceIn(52, 96),
            "Leadership & Strategy" to (((tallies["Leadership"] ?: 60) + (tallies["Business"] ?: 60)) * 100 / 300).coerceIn(52, 96),
            "Cloud & Security" to (((tallies["Cloud"] ?: 60) + (tallies["Security"] ?: 60)) * 100 / 280).coerceIn(50, 96)
        )
        _assessmentDimensions.value = normalized

        val current = _localProfile.value
        val topCareer = calculateCareerMatches(current).firstOrNull()?.first?.id ?: "ai_ml_engineer"
        val badges = (current.unlockedBadgeIds + "badge_first_assessment").distinct()
        val avgAssessmentScore = normalized.values.average().toLong()
        updateProfile(
            current.copy(
                assessmentCompleted = true,
                targetCareerId = current.targetCareerId.ifBlank { topCareer },
                xpPoints = current.xpPoints + 150L,
                readinessScore = ((current.readinessScore + avgAssessmentScore) / 2L).coerceIn(45L, 98L),
                unlockedBadgeIds = badges
            )
        )
        addCustomGoalOrTask(
            title = "Completed 12-Category Assessment (Top Match: $topCareer)",
            category = "assessment_record",
            subtitle = "AI & Data: ${normalized["AI & Data Science"]}% • Software: ${normalized["Full Stack & Software"]}%",
            score = avgAssessmentScore.toInt()
        )
    }

    // Certifications & Internships
    fun toggleCertificationBookmark(certId: String) {
        _bookmarkedCertIds.update { if (it.contains(certId)) it - certId else it + certId }
    }

    fun toggleCertificationCompleted(certId: String) {
        val currentSet = _completedCertIds.value
        val isNowCompleted = !currentSet.contains(certId)
        _completedCertIds.value = if (isNowCompleted) currentSet + certId else currentSet - certId
        if (isNowCompleted) {
            val cert = CareerCatalog.certifications.firstOrNull { it.id == certId }
            if (cert != null) {
                addCustomGoalOrTask(
                    title = cert.title,
                    category = "certification",
                    subtitle = "${cert.provider} • ${cert.duration}",
                    score = 100
                )
            }
        }
    }

    fun toggleInternshipSaved(internId: String) {
        _savedInternshipIds.update { if (it.contains(internId)) it - internId else it + internId }
    }

    fun updateInternshipStatus(internId: String, status: String) {
        _internshipStatuses.update { it + (internId to status) }
        _savedInternshipIds.update { it + internId }
    }

    fun markAllNotificationsRead() {
        _notifications.update { list -> list.map { it.copy(isRead = true) } }
    }

    // Firebase AI Logic: Streaming Conversational Career Assistant
    fun sendChatMessage(userPrompt: String) {
        if (userPrompt.isBlank()) return
        val userMsg = ChatMessage(
            id = "u_${System.currentTimeMillis()}",
            sender = "user",
            text = userPrompt.trim(),
            timestamp = "Now"
        )
        val streamingId = "ai_${System.currentTimeMillis()}"
        val placeholderAiMsg = ChatMessage(
            id = streamingId,
            sender = "ai",
            text = "Thinking...",
            timestamp = "Now",
            isStreaming = true
        )
        _chatMessages.update { it + userMsg + placeholderAiMsg }

        val profile = _localProfile.value
        val contextualSystemPrompt = """
            You are PATHFINDER AI ASSISTANT, a world-class career advisor and engineering mentor.
            Student Profile Context:
            - Name: ${profile.fullName.ifBlank { "Student" }}
            - Degree: ${profile.degree.ifBlank { "Not specified yet" }} in ${profile.branch.ifBlank { "Not specified yet" }} at ${profile.college.ifBlank { "Not specified yet" }} (${profile.academicYear}, CGPA: ${profile.cgpa})
            - Career Goal: ${profile.careerGoal.ifBlank { "Exploring career options" }}
            - Technical Skills: ${profile.technicalSkills.joinToString(", ").ifBlank { "None added yet" }}
            - Soft Skills: ${profile.softSkills.joinToString(", ").ifBlank { "None added yet" }}
            - Career Readiness Score: ${profile.readinessScore}%
            
            Provide a structured, encouraging, highly actionable response tailored specifically to this student's profile.
            Use concise bullet points and clear headings where helpful.
            
            User Question: $userPrompt
        """.trimIndent()

        viewModelScope.launch {
            _aiChatState.value = UiState.Loading
            val accumulatedText = StringBuilder()
            try {
                withTimeout(60_000L) {
                    chatModel.generateContentStream(contextualSystemPrompt).collect { chunk ->
                        val chunkText = chunk.text.orEmpty()
                        accumulatedText.append(chunkText)
                        val currentText = accumulatedText.toString()
                        _aiChatState.value = UiState.Streaming(currentText)
                        _chatMessages.update { list ->
                            list.map { msg ->
                                if (msg.id == streamingId) msg.copy(text = currentText, isStreaming = true) else msg
                            }
                        }
                    }
                }
                val finalResponse = accumulatedText.toString()
                if (finalResponse.isNotBlank()) {
                    _aiChatState.value = UiState.Success(finalResponse)
                    _chatMessages.update { list ->
                        list.map { msg ->
                            if (msg.id == streamingId) msg.copy(text = finalResponse, isStreaming = false) else msg
                        }
                    }
                } else {
                    val errMsg = "Model returned an empty response."
                    _aiChatState.value = UiState.Error(errMsg)
                    _chatMessages.update { list ->
                        list.map { msg ->
                            if (msg.id == streamingId) msg.copy(text = "Error: $errMsg", isStreaming = false) else msg
                        }
                    }
                }
            } catch (e: TimeoutCancellationException) {
                Log.e(TAG, "AI Assistant streaming timed out", e)
                val msg = "Generation timed out after 60 seconds. Please try again."
                _aiChatState.value = UiState.Error(msg)
                _chatMessages.update { list ->
                    list.map { item ->
                        if (item.id == streamingId) item.copy(text = msg, isStreaming = false) else item
                    }
                }
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                Log.e(TAG, "AI Assistant generation failed", e)
                val msg = e.localizedMessage ?: "AI generation failed."
                _aiChatState.value = UiState.Error(msg)
                _chatMessages.update { list ->
                    list.map { item ->
                        if (item.id == streamingId) item.copy(text = "AI Service Notice: $msg", isStreaming = false) else item
                    }
                }
            }
        }
    }

    fun clearChatHistory() {
        _chatMessages.value = listOf(
            ChatMessage(
                id = "welcome_${System.currentTimeMillis()}",
                sender = "ai",
                text = "Conversation cleared! How can I help you plan your career, skills, or interview prep today?",
                timestamp = "Just now"
            )
        )
        _aiChatState.value = UiState.Idle
    }

    // Firebase AI Logic: Structured JSON Resume Analysis
    fun analyzeResumeWithAi(fileName: String, resumeTextSummary: String) {
        if (resumeTextSummary.isBlank()) {
            _resumeState.value = UiState.Error("Please paste or enter your resume details or select a PDF before analyzing.")
            return
        }
        val profile = _localProfile.value
        val prompt = """
            Analyze the following student resume / profile for career readiness and skill gaps:
            File Name: ${fileName.ifBlank { "Student_Resume.pdf" }}
            Student: ${profile.fullName} (${profile.degree} - ${profile.branch}, CGPA ${profile.cgpa})
            Target Career ID: ${profile.targetCareerId}
            Skills Listed: ${profile.technicalSkills.joinToString(", ")}
            Resume Content / Summary: $resumeTextSummary
        """.trimIndent()

        viewModelScope.launch {
            _resumeState.value = UiState.Loading
            try {
                val response = withTimeout(60_000L) {
                    resumeModel.generateContent(prompt)
                }
                val jsonString = response.text ?: throw IllegalStateException("Model returned empty JSON.")
                val json = JSONObject(jsonString)

                fun parseList(key: String): List<String> {
                    val arr = json.optJSONArray(key) ?: return emptyList()
                    return (0 until arr.length()).map { arr.getString(it) }
                }

                val result = ResumeAnalysisResult(
                    fileName = fileName.ifBlank { "Uploaded_Resume.pdf" },
                    completenessScore = json.optInt("completenessScore", 75).coerceIn(0, 100),
                    careerSuitability = json.optString("careerSuitability", "Suitability analysis complete"),
                    extractedTechSkills = parseList("extractedTechSkills"),
                    extractedSoftSkills = parseList("extractedSoftSkills"),
                    educationSummary = json.optString("educationSummary", profile.degree.ifBlank { "Education extracted" }),
                    projectHighlights = parseList("projectHighlights"),
                    missingSections = parseList("missingSections"),
                    suggestedImprovements = parseList("suggestedImprovements"),
                    skillGapRecommendations = parseList("skillGapRecommendations")
                )
                _resumeState.value = UiState.Success(result)

                val badges = (profile.unlockedBadgeIds + "badge_resume_completed").distinct()
                updateProfile(profile.copy(xpPoints = profile.xpPoints + 100L, unlockedBadgeIds = badges))
            } catch (e: TimeoutCancellationException) {
                Log.e(TAG, "Resume analysis timed out", e)
                _resumeState.value = UiState.Error("Resume analysis timed out after 60 seconds. Please try again.")
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                Log.e(TAG, "Resume analysis failed", e)
                _resumeState.value = UiState.Error(e.localizedMessage ?: "Failed to analyze resume.")
            }
        }
    }

    // Firebase AI Logic: Structured JSON Mock Interview Evaluation
    fun evaluateInterviewAnswer(
        careerTitle: String,
        roundType: String,
        difficulty: String,
        question: String,
        userAnswer: String
    ) {
        if (userAnswer.isBlank()) {
            _interviewFeedbackState.value = UiState.Error("Please enter your response before submitting.")
            return
        }
        val prompt = """
            Evaluate this candidate's mock interview response:
            Target Role: $careerTitle
            Round Type: $roundType ($difficulty)
            Question: $question
            Candidate's Response: $userAnswer
        """.trimIndent()

        viewModelScope.launch {
            _interviewFeedbackState.value = UiState.Loading
            try {
                val response = withTimeout(60_000L) {
                    interviewModel.generateContent(prompt)
                }
                val jsonString = response.text ?: throw IllegalStateException("Model returned empty JSON.")
                val json = JSONObject(jsonString)

                fun parseList(key: String): List<String> {
                    val arr = json.optJSONArray(key) ?: return emptyList()
                    return (0 until arr.length()).map { arr.getString(it) }
                }

                val feedback = InterviewFeedbackResult(
                    overallScore = json.optInt("overallScore", 80).coerceIn(0, 100),
                    clarityScore = json.optInt("clarityScore", 80).coerceIn(0, 100),
                    technicalDepthScore = json.optInt("technicalDepthScore", 80).coerceIn(0, 100),
                    starStructureScore = json.optInt("starStructureScore", 80).coerceIn(0, 100),
                    strengths = parseList("strengths"),
                    improvements = parseList("improvements"),
                    modelSampleResponse = json.optString("modelSampleResponse", "")
                )
                _interviewFeedbackState.value = UiState.Success(feedback)

                val current = _localProfile.value
                val badges = (current.unlockedBadgeIds + "badge_interview_ready").distinct()
                updateProfile(
                    current.copy(
                        xpPoints = current.xpPoints + 120L,
                        readinessScore = (current.readinessScore + 5L).coerceAtMost(99L),
                        unlockedBadgeIds = badges
                    )
                )
                addCustomGoalOrTask(
                    title = "Mock Interview: $careerTitle ($roundType)",
                    category = "interview_session",
                    subtitle = "Score: ${feedback.overallScore}/100 • Question: ${question.take(60)}...",
                    score = feedback.overallScore
                )
            } catch (e: TimeoutCancellationException) {
                Log.e(TAG, "Interview evaluation timed out", e)
                _interviewFeedbackState.value = UiState.Error("Evaluation timed out after 60 seconds. Please try again.")
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                Log.e(TAG, "Interview evaluation failed", e)
                _interviewFeedbackState.value = UiState.Error(e.localizedMessage ?: "Failed to evaluate interview answer.")
            }
        }
    }

    fun resetInterviewFeedback() {
        _interviewFeedbackState.value = UiState.Idle
    }

    companion object {
        fun factory(currentUserId: String): ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val app = checkNotNull(this[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY]) {
                    "APPLICATION_KEY missing from CreationExtras"
                }
                val databaseId = app.getString(R.string.firestore_database_id)
                val db = FirebaseFirestore.getInstance(databaseId)
                PathfinderViewModel(PathfinderRepository(db), currentUserId, app.applicationContext)
            }
        }
    }
}
