package com.larder.app.data.remote.supabase

import com.larder.app.domain.model.Household
import com.larder.app.domain.model.HouseholdMember
import java.util.UUID
import kotlin.random.Random

data class UserSession(
    val userId: String,
    val email: String,
    val jwtToken: String,
    val activeHouseholdId: String
)

class SupabaseAuthManager {

    private var currentSession: UserSession? = null

    fun getCurrentSession(): UserSession? = currentSession

    suspend fun signIn(email: String, token: String): Result<UserSession> {
        return try {
            val userId = UUID.randomUUID().toString()
            val session = UserSession(
                userId = userId,
                email = email,
                jwtToken = token,
                activeHouseholdId = "household_${userId.take(8)}"
            )
            currentSession = session
            Result.success(session)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun createHousehold(name: String): Result<Household> {
        val session = currentSession ?: return Result.failure(IllegalStateException("User not authenticated"))
        val household = Household(
            id = UUID.randomUUID().toString(),
            name = name,
            createdAt = System.currentTimeMillis()
        )
        return Result.success(household)
    }

    fun generateInviteCode(householdId: String): String {
        val randomSuffix = Random.nextInt(1000, 9999)
        return "LRD-$randomSuffix"
    }

    suspend fun joinHouseholdWithInviteCode(inviteCode: String): Result<HouseholdMember> {
        val session = currentSession ?: return Result.failure(IllegalStateException("User not authenticated"))
        if (!inviteCode.startsWith("LRD-")) {
            return Result.failure(IllegalArgumentException("Invalid invite code format"))
        }

        val member = HouseholdMember(
            householdId = "household_${inviteCode.takeLast(4)}",
            userId = session.userId,
            role = "member"
        )
        return Result.success(member)
    }
}
