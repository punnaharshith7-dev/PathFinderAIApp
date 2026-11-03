package com.example.data.repository

import com.example.base.FirestoreEmulatorTestBase
import com.example.data.model.UserProfile
import com.google.firebase.firestore.FirebaseFirestoreException
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withTimeout
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test

class PathfinderRepositoryRuleTest : FirestoreEmulatorTestBase() {

    @Test
    fun saveAndGetUserProfile_authenticatedOwner_succeeds() = runBlocking {
        val uid = signInTestUser(ALICE_EMAIL)
        val repository = PathfinderRepository(firestore)

        val saveResult = withTimeout(DEFAULT_TIMEOUT_MS) {
            repository.saveOrUpdateUserProfile(
                UserProfile(
                    fullName = "Alice Sharma",
                    email = ALICE_EMAIL,
                    xpPoints = 250L,
                    streakDays = 4L,
                    readinessScore = 82L
                )
            )
        }
        assertTrue(saveResult.isSuccess)

        val fetched = withTimeout(DEFAULT_TIMEOUT_MS) {
            repository.getUserProfile(uid).getOrThrow()
        }
        assertNotNull(fetched)
        assertEquals("Alice Sharma", fetched?.fullName)
    }

    @Test
    fun createAndObserveGoals_authenticatedOwner_emitsRealtimeUpdates() = runBlocking {
        val uid = signInTestUser(ALICE_EMAIL)
        val repository = PathfinderRepository(firestore)
        val goalId = withTimeout(DEFAULT_TIMEOUT_MS) {
            repository.createGoal(
                title = "Master Kotlin Coroutines",
                category = "roadmap_task",
                subtitle = "Level 3 Concurrency"
            ).getOrThrow()
        }

        val emittedGoals = withTimeout(FLOW_TIMEOUT_MS) {
            repository.observeGoals(uid).first { list -> list.any { it.id == goalId } }
        }
        assertTrue(emittedGoals.any { it.id == goalId })
    }

    @Test
    fun getGoalById_crossUserAccess_failsWithPermissionDenied() = runBlocking {
        val aliceUid = signInTestUser(ALICE_EMAIL)
        val aliceRepo = PathfinderRepository(firestore)
        val goalId = withTimeout(DEFAULT_TIMEOUT_MS) {
            aliceRepo.createGoal(title = "Alice Goal", category = "milestone").getOrThrow()
        }

        signInTestUser(BOB_EMAIL)
        val bobRepo = PathfinderRepository(firestore)
        val result = withTimeout(DEFAULT_TIMEOUT_MS) {
            bobRepo.getGoalById(aliceUid, goalId)
        }
        assertTrue(result.isFailure)
        val exception = result.exceptionOrNull()?.unwrapFirestoreException()
        assertNotNull(exception)
        assertEquals(FirebaseFirestoreException.Code.PERMISSION_DENIED, exception?.code)
    }

    @Test
    fun observeGoals_unauthenticatedUser_failsWithPermissionDenied() = runBlocking {
        val aliceUid = signInTestUser(ALICE_EMAIL)
        auth.signOut()
        val repository = PathfinderRepository(firestore)

        var caught: Exception? = null
        try {
            withTimeout(FLOW_TIMEOUT_MS) {
                repository.observeGoals(aliceUid).first()
            }
        } catch (e: Exception) {
            caught = e
        }
        assertNotNull(caught)
        val firestoreException = checkNotNull(caught?.unwrapFirestoreException()) {
            "Expected nested FirebaseFirestoreException, got $caught"
        }
        assertEquals(FirebaseFirestoreException.Code.PERMISSION_DENIED, firestoreException.code)
    }

    private companion object {
        const val ALICE_EMAIL = "alice@test.com"
        const val BOB_EMAIL = "bob@test.com"
        const val DEFAULT_TIMEOUT_MS = 5000L
        const val FLOW_TIMEOUT_MS = 3000L
    }
}
