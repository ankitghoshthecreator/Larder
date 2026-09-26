package com.larder.app.domain.model

import androidx.room.Entity

@Entity(
    tableName = "household_members",
    primaryKeys = ["householdId", "userId"]
)
data class HouseholdMember(
    val householdId: String,
    val userId: String,
    val role: String = "member" // "owner" or "member"
)
