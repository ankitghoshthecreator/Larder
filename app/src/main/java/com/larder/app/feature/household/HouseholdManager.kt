package com.larder.app.feature.household

import com.larder.app.data.local.dao.HouseholdDao
import com.larder.app.domain.model.Household
import com.larder.app.domain.model.HouseholdMember
import kotlinx.coroutines.flow.Flow
import java.util.UUID

enum class MemberRole { OWNER, MEMBER }

class HouseholdManager(
    private val householdDao: HouseholdDao
) {

    suspend fun createHousehold(
        name: String,
        ownerUserId: String
    ): Household {
        val householdId = UUID.randomUUID().toString()
        val household = Household(
            id = householdId,
            name = name,
            createdAt = System.currentTimeMillis()
        )
        val ownerMember = HouseholdMember(
            householdId = householdId,
            userId = ownerUserId,
            role = MemberRole.OWNER.name.lowercase()
        )

        householdDao.insertHousehold(household)
        householdDao.insertMember(ownerMember)
        return household
    }

    suspend fun addMemberToHousehold(
        householdId: String,
        userId: String,
        role: MemberRole = MemberRole.MEMBER
    ): HouseholdMember {
        val member = HouseholdMember(
            householdId = householdId,
            userId = userId,
            role = role.name.lowercase()
        )
        householdDao.insertMember(member)
        return member
    }

    fun getHouseholdMembers(householdId: String): Flow<List<HouseholdMember>> {
        return householdDao.getMembers(householdId)
    }

    fun isOwner(role: String): Boolean {
        return role.equals(MemberRole.OWNER.name, ignoreCase = true)
    }

    suspend fun leaveOrDeleteHousehold(householdId: String) {
        householdDao.deleteHousehold(householdId)
    }
}
