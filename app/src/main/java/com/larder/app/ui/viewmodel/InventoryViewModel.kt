package com.larder.app.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.larder.app.data.repository.InventoryRepository
import com.larder.app.domain.calculator.ExpiryCalculator
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
                if (itemList.isEmpty()) {
                    seedSampleItems(householdId)
                } else {
                    _uiState.value = _uiState.value.copy(
                        items = itemList,
                        expiringItems = itemList.filter { item ->
                            ExpiryCalculator.isExpiringSoon(item.expiryEstimate)
                        },
                        itemsNeedingReview = itemList.filter { item ->
                            item.getItemStatusEnum() == ItemStatus.NEEDS_REVIEW
                        },
                        isLoading = false
                    )
                }
            }
        }
    }

    private fun seedSampleItems(householdId: String) {
        viewModelScope.launch {
            val now = System.currentTimeMillis()
            
            // 1. Organic Whole Milk (Expiring in 2 days)
            repository.addItem(
                householdId = householdId,
                name = "Organic Whole Milk 1L",
                category = Category.DAIRY,
                quantity = 1.0,
                unit = "L",
                customExpiryMillis = now + (2 * 24 * 60 * 60 * 1000L),
                status = ItemStatus.CONFIRMED
            )

            // 2. Fresh Avocados (Expiring in 3 days)
            repository.addItem(
                householdId = householdId,
                name = "Fresh Avocados (4-pack)",
                category = Category.PRODUCE,
                quantity = 4.0,
                unit = "unit",
                customExpiryMillis = now + (3 * 24 * 60 * 60 * 1000L),
                status = ItemStatus.CONFIRMED
            )

            // 3. Greek Yogurt (Expiring in 7 days)
            repository.addItem(
                householdId = householdId,
                name = "Greek Yogurt 500g",
                category = Category.DAIRY,
                quantity = 1.0,
                unit = "g",
                customExpiryMillis = now + (7 * 24 * 60 * 60 * 1000L),
                status = ItemStatus.CONFIRMED
            )

            // 4. Whole Wheat Bread (Needs Review)
            repository.addItem(
                householdId = householdId,
                name = "Artisan Sliced Bread",
                category = Category.BAKERY,
                quantity = 1.0,
                unit = "pack",
                customExpiryMillis = now + (4 * 24 * 60 * 60 * 1000L),
                status = ItemStatus.NEEDS_REVIEW
            )

            // 5. Frozen Pizza
            repository.addItem(
                householdId = householdId,
                name = "Four Cheese Frozen Pizza",
                category = Category.FROZEN,
                quantity = 2.0,
                unit = "unit",
                customExpiryMillis = now + (60 * 24 * 60 * 60 * 1000L),
                status = ItemStatus.CONFIRMED
            )
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
