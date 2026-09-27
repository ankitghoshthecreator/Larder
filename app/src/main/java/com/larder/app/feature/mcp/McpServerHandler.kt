package com.larder.app.feature.mcp

import com.larder.app.data.repository.InventoryRepository
import com.larder.app.domain.calculator.ExpiryCalculator
import com.larder.app.domain.model.Item
import kotlinx.coroutines.flow.first

data class StockCheckResult(
    val itemName: String,
    val isAvailable: Boolean,
    val totalQuantity: Double,
    val unit: String,
    val items: List<Item>
)

data class CategorySummaryItem(
    val categoryName: String,
    val itemCount: Int,
    val totalQuantity: Double
)

class McpServerHandler(
    private val inventoryRepository: InventoryRepository
) {

    /**
     * MCP Tool: list_expiring(days: Int)
     * Day range is capped at 30 days server-side per MCP threat model specs.
     */
    suspend fun listExpiring(householdId: String, days: Int): List<Item> {
        val cappedDays = days.coerceIn(1, 30)
        val allItems = inventoryRepository.getInventoryItems(householdId).first()
        val now = System.currentTimeMillis()
        val thresholdMillis = now + (cappedDays * 24 * 60 * 60 * 1000L)

        return allItems.filter { item ->
            item.expiryEstimate in now..thresholdMillis
        }.sortedBy { it.expiryEstimate }
    }

    /**
     * MCP Tool: check_stock(itemName: String)
     */
    suspend fun checkStock(householdId: String, itemName: String): StockCheckResult {
        val allItems = inventoryRepository.getInventoryItems(householdId).first()
        val matchingItems = allItems.filter { 
            it.name.contains(itemName, ignoreCase = true) 
        }

        val totalQty = matchingItems.sumOf { it.quantity }
        val unit = matchingItems.firstOrNull()?.unit ?: "unit"

        return StockCheckResult(
            itemName = itemName,
            isAvailable = matchingItems.isNotEmpty() && totalQty > 0,
            totalQuantity = totalQty,
            unit = unit,
            items = matchingItems
        )
    }

    /**
     * MCP Tool: get_category_summary()
     */
    suspend fun getCategorySummary(householdId: String): List<CategorySummaryItem> {
        val allItems = inventoryRepository.getInventoryItems(householdId).first()
        return allItems.groupBy { it.category }
            .map { (cat, items) ->
                CategorySummaryItem(
                    categoryName = cat,
                    itemCount = items.size,
                    totalQuantity = items.sumOf { it.quantity }
                )
            }.sortedBy { it.categoryName }
    }
}
