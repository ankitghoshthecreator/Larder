package com.larder.app.feature.household

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.util.UUID

enum class ActionType { ADD_ITEM, EDIT_ITEM, DELETE_ITEM, CONFIRM_REVIEW, JOIN_HOUSEHOLD }

data class AuditLogEntry(
    val id: String = UUID.randomUUID().toString(),
    val householdId: String,
    val userId: String,
    val userName: String,
    val actionType: ActionType,
    val details: String,
    val timestamp: Long = System.currentTimeMillis()
)

class HouseholdAuditLogger {

    private val _auditLogs = MutableStateFlow<List<AuditLogEntry>>(emptyList())
    val auditLogs: StateFlow<List<AuditLogEntry>> = _auditLogs.asStateFlow()

    fun logAction(
        householdId: String,
        userId: String,
        userName: String,
        actionType: ActionType,
        details: String
    ) {
        val entry = AuditLogEntry(
            householdId = householdId,
            userId = userId,
            userName = userName,
            actionType = actionType,
            details = details
        )
        val currentList = _auditLogs.value.toMutableList()
        currentList.add(0, entry) // Newest first
        _auditLogs.value = currentList
    }

    fun getLogsForHousehold(householdId: String): List<AuditLogEntry> {
        return _auditLogs.value.filter { it.householdId == householdId }
    }
}
