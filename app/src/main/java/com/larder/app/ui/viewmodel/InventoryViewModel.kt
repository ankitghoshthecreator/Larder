package com.larder.app.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.larder.app.data.repository.InventoryRepository
import com.larder.app.domain.model.Category
import com.larder.app.domain.model.Item
import com.larder.app.domain.model.ItemStatus
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class InventoryUiState(
    val householdId: String = "default_household",
    val items: List<Item> = emptyList(),
    val expiringItems: List<Item> = emptyList(),
    val itemsNeedingReview: List<Item> = emptyList(),
    val selectedCategory: Category? = null,
    val searchQuery: String = "",
    val isLoading: Boolean = false
)

class InventoryViewModel(
    private val repository: InventoryRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(InventoryUiState())
    val uiState: StateFlow<InventoryUiState> = _uiState.asStateFlow()

    init {
        loadInventory()
    }

    fun loadInventory(householdId: String = _uiState.value.householdId) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, householdId = householdId)
            
            repository.getInventoryItems(householdId).collect { itemList ->
                _uiState.value = _uiState.value.copy(
                    items = itemList,
                    expiringItems = itemList.filter { item ->
                        com.larder.app.domain.calculator.ExpiryCalculator.isExpiringSoon(item.expiryEstimate)
                    },
                    itemsNeedingReview = itemList.filter { item ->
                        item.getItemStatusEnum() == ItemStatus.NEEDS_REVIEW
                    },
                    isLoading = false
                )
            }
        }
    }

    fun filterByCategory(category: Category?) {
        _uiState.value = _uiState.value.copy(selectedCategory = category)
    }

    fun updateSearchQuery(query: String) {
        _uiState.value = _uiState.value.copy(searchQuery = query)
    }

    fun addItem(
        name: String,
        category: Category,
        quantity: Double = 1.0,
        unit: String = "unit",
        customExpiryMillis: Long? = null,
        needsReview: Boolean = false
    ) {
        viewModelScope.launch {
            repository.addItem(
                householdId = _uiState.value.householdId,
                name = name,
                category = category,
                quantity = quantity,
                unit = unit,
                customExpiryMillis = customExpiryMillis,
                status = if (needsReview) ItemStatus.NEEDS_REVIEW else ItemStatus.CONFIRMED
            )
        }
    }

    fun confirmReview(itemId: String, correctedName: String? = null, correctedCategory: Category? = null) {
        viewModelScope.launch {
            repository.confirmItemReview(itemId, correctedName, correctedCategory)
        }
    }

    fun deleteItem(itemId: String) {
        viewModelScope.launch {
            repository.deleteItem(itemId)
        }
    }
}
