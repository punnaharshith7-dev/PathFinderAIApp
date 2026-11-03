package com.example.data.model

import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentId
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.IgnoreExtraProperties
import com.google.firebase.firestore.ServerTimestamp

@IgnoreExtraProperties
data class UserProfile(
    @DocumentId val id: String = "",
    val userId: String = "",
    val fullName: String = "",
    val email: String = "",
    val age: Long = 20L,
    val phone: String = "",
    val college: String = "",
    val degree: String = "",
    val branch: String = "",
    val academicYear: String = "",
    val cgpa: String = "",
    val careerGoal: String = "",
    val targetCareerId: String = "",
    val xpPoints: Long = 0L,
    val streakDays: Long = 0L,
    val readinessScore: Long = 0L,
    val assessmentCompleted: Boolean = false,
    val technicalSkills: List<String> = emptyList(),
    val softSkills: List<String> = emptyList(),
    val interests: List<String> = emptyList(),
    val preferredIndustries: List<String> = emptyList(),
    val bookmarkedCareerIds: List<String> = emptyList(),
    val completedLessonIds: List<String> = emptyList(),
    val unlockedBadgeIds: List<String> = emptyList(),
    @ServerTimestamp val createdAt: Timestamp? = null,
    @ServerTimestamp val updatedAt: Timestamp? = null
) {
    fun calculateCompletionPercentage(): Int {
        var filled = 0
        val total = 8
        if (fullName.isNotBlank()) filled++
        if (email.isNotBlank()) filled++
        if (college.isNotBlank()) filled++
        if (degree.isNotBlank()) filled++
        if (branch.isNotBlank()) filled++
        if (careerGoal.isNotBlank()) filled++
        if (technicalSkills.isNotEmpty()) filled++
        if (assessmentCompleted) filled++
        return ((filled * 100) / total).coerceIn(0, 100)
    }

    fun toCreateMap(uid: String): Map<String, Any> {
        return mapOf(
            "userId" to uid,
            "fullName" to fullName.take(100).ifBlank { "Student" },
            "email" to email.take(150).ifBlank { "user@example.com" },
            "age" to age.coerceIn(10L, 100L),
            "phone" to phone.take(30),
            "college" to college.take(150),
            "degree" to degree.take(100),
            "branch" to branch.take(100),
            "academicYear" to academicYear.take(50),
            "cgpa" to cgpa.take(20),
            "careerGoal" to careerGoal.take(300),
            "targetCareerId" to targetCareerId.take(100),
            "xpPoints" to xpPoints.coerceIn(0L, 1000000L),
            "streakDays" to streakDays.coerceIn(0L, 3650L),
            "readinessScore" to readinessScore.coerceIn(0L, 100L),
            "assessmentCompleted" to assessmentCompleted,
            "technicalSkills" to technicalSkills.take(30).map { it.take(150) },
            "softSkills" to softSkills.take(20).map { it.take(150) },
            "interests" to interests.take(20).map { it.take(150) },
            "preferredIndustries" to preferredIndustries.take(15).map { it.take(150) },
            "bookmarkedCareerIds" to bookmarkedCareerIds.take(30).map { it.take(150) },
            "completedLessonIds" to completedLessonIds.take(100).map { it.take(150) },
            "unlockedBadgeIds" to unlockedBadgeIds.take(30).map { it.take(150) },
            "createdAt" to FieldValue.serverTimestamp(),
            "updatedAt" to FieldValue.serverTimestamp()
        )
    }

    fun toUpdateMap(): Map<String, Any> {
        return mapOf(
            "fullName" to fullName.take(100).ifBlank { "Student" },
            "email" to email.take(150).ifBlank { "user@example.com" },
            "age" to age.coerceIn(10L, 100L),
            "phone" to phone.take(30),
            "college" to college.take(150),
            "degree" to degree.take(100),
            "branch" to branch.take(100),
            "academicYear" to academicYear.take(50),
            "cgpa" to cgpa.take(20),
            "careerGoal" to careerGoal.take(300),
            "targetCareerId" to targetCareerId.take(100),
            "xpPoints" to xpPoints.coerceIn(0L, 1000000L),
            "streakDays" to streakDays.coerceIn(0L, 3650L),
            "readinessScore" to readinessScore.coerceIn(0L, 100L),
            "assessmentCompleted" to assessmentCompleted,
            "technicalSkills" to technicalSkills.take(30).map { it.take(150) },
            "softSkills" to softSkills.take(20).map { it.take(150) },
            "interests" to interests.take(20).map { it.take(150) },
            "preferredIndustries" to preferredIndustries.take(15).map { it.take(150) },
            "bookmarkedCareerIds" to bookmarkedCareerIds.take(30).map { it.take(150) },
            "completedLessonIds" to completedLessonIds.take(100).map { it.take(150) },
            "unlockedBadgeIds" to unlockedBadgeIds.take(30).map { it.take(150) },
            "updatedAt" to FieldValue.serverTimestamp()
        )
    }
}

@IgnoreExtraProperties
data class CareerGoalItem(
    @DocumentId val id: String = "",
    val userId: String = "",
    val title: String = "",
    val category: String = "roadmap_task",
    val subtitle: String = "",
    val completed: Boolean = false,
    val score: Long = 0L,
    @ServerTimestamp val createdAt: Timestamp? = null,
    @ServerTimestamp val updatedAt: Timestamp? = null
)
