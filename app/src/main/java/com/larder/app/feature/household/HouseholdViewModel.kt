package com.larder.app.feature.household

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.larder.app.data.local.dao.HouseholdDao
import com.larder.app.data.remote.supabase.SupabaseAuthManager
import com.larder.app.domain.model.Household
import com.larder.app.domain.model.HouseholdMember
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class HouseholdUiState(
    val household: Household? = null,
    val members: List<HouseholdMember> = emptyList(),
    val inviteCode: String = "",
    val isOwner: Boolean = false,
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val successMessage: String? = null
)

class HouseholdViewModel(
    private val householdManager: HouseholdManager,
    private val auditLogger: HouseholdAuditLogger,
    private val authManager: SupabaseAuthManager
) : ViewModel() {

    private val _uiState = MutableStateFlow(HouseholdUiState())
    val uiState: StateFlow<HouseholdUiState> = _uiState.asStateFlow()

    val auditLogs = auditLogger.auditLogs

    fun createHousehold(name: String) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, errorMessage = null)
            try {
                val session = authManager.getCurrentSession()
                val userId = session?.userId ?: "local_user"
                val household = householdManager.createHousehold(name, userId)
                val code = authManager.generateInviteCode(household.id)

                _uiState.value = _uiState.value.copy(
                    household = household,
                    inviteCode = code,
                    isOwner = true,
                    isLoading = false,
                    successMessage = "Household \"${household.name}\" created!"
                )

                auditLogger.logAction(
                    householdId = household.id,
                    userId = userId,
                    userName = session?.email ?: "You",
                    actionType = ActionType.JOIN_HOUSEHOLD,
                    details = "Created household \"${household.name}\""
                )
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(isLoading = false, errorMessage = e.message)
            }
        }
    }

    fun joinHousehold(inviteCode: String) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, errorMessage = null)
            try {
                val result = authManager.joinHouseholdWithInviteCode(inviteCode)
                if (result.isSuccess) {
                    val member = result.getOrThrow()
                    householdManager.addMemberToHousehold(
                        householdId = member.householdId,
                        userId = member.userId,
                        role = MemberRole.MEMBER
                    )
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        successMessage = "Joined household successfully!"
                    )
                    loadMembers(member.householdId)
                } else {
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        errorMessage = "Invalid invite code. Please try again."
                    )
                }
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(isLoading = false, errorMessage = e.message)
            }
        }
    }

    fun loadMembers(householdId: String) {
        viewModelScope.launch {
            householdManager.getHouseholdMembers(householdId).collect { members ->
                _uiState.value = _uiState.value.copy(members = members)
            }
        }
    }

    fun dismissMessages() {
        _uiState.value = _uiState.value.copy(errorMessage = null, successMessage = null)
    }
}
