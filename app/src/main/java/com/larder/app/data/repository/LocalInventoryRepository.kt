package com.larder.app.data.repository

import com.larder.app.data.local.dao.ItemDao
import com.larder.app.domain.calculator.ExpiryCalculator
import com.larder.app.domain.model.Category
import com.larder.app.domain.model.Item
import com.larder.app.domain.model.ItemStatus
import kotlinx.coroutines.flow.Flow
import java.util.UUID

interface InventoryRepository {
    fun getInventoryItems(householdId: String): Flow<List<Item>>
    fun getExpiringItems(householdId: String, daysThreshold: Int = 3): Flow<List<Item>>
    fun getLowStockItems(householdId: String, minQuantity: Double = 1.0): Flow<List<Item>>
    fun getItemsNeedingReview(householdId: String): Flow<List<Item>>
    suspend fun getItemById(id: String): Item?
    suspend fun addItem(
        householdId: String,
        name: String,
        category: Category,
        quantity: Double = 1.0,
        unit: String = "unit",
        customExpiryMillis: Long? = null,
        sourceImagePath: String? = null,
        status: ItemStatus = ItemStatus.CONFIRMED
    ): Item
    suspend fun updateItem(item: Item)
    suspend fun confirmItemReview(itemId: String, correctedName: String? = null, correctedCategory: Category? = null)
    suspend fun deleteItem(id: String)
    suspend fun clearHousehold(householdId: String)
}

class LocalInventoryRepository(
    private val itemDao: ItemDao
) : InventoryRepository {

    override fun getInventoryItems(householdId: String): Flow<List<Item>> {
        return itemDao.getItemsForHousehold(householdId)
    }

    override fun getExpiringItems(householdId: String, daysThreshold: Int): Flow<List<Item>> {
        val thresholdMillis = System.currentTimeMillis() + (daysThreshold * 24 * 60 * 60 * 1000L)
        return itemDao.getExpiringItems(householdId, thresholdMillis)
    }

    override fun getLowStockItems(householdId: String, minQuantity: Double): Flow<List<Item>> {
        return itemDao.getLowStockItems(householdId, minQuantity)
    }

    override fun getItemsNeedingReview(householdId: String): Flow<List<Item>> {
        return itemDao.getItemsNeedingReview(householdId)
    }

    override suspend fun getItemById(id: String): Item? {
        return itemDao.getItemById(id)
    }

    override suspend fun addItem(
        householdId: String,
        name: String,
        category: Category,
        quantity: Double,
        unit: String,
        customExpiryMillis: Long?,
        sourceImagePath: String?,
        status: ItemStatus
    ): Item {
        val now = System.currentTimeMillis()
        val expiry = customExpiryMillis ?: ExpiryCalculator.calculateDefaultExpiry(category, now)
        
        val item = Item(
            id = UUID.randomUUID().toString(),
            householdId = householdId,
            name = name,
            category = category.name,
            quantity = quantity,
            unit = unit,
            expiryEstimate = expiry,
            sourceImagePath = sourceImagePath,
            status = status.name,
            createdAt = now,
            updatedAt = now
        )
        
        itemDao.insertItem(item)
        return item
    }

    override suspend fun updateItem(item: Item) {
        val updated = item.copy(updatedAt = System.currentTimeMillis())
        itemDao.updateItem(updated)
    }

    override suspend fun confirmItemReview(
        itemId: String,
        correctedName: String?,
        correctedCategory: Category?
    ) {
        val existing = itemDao.getItemById(itemId) ?: return
        val newCategory = correctedCategory ?: existing.getCategoryEnum()
        val newName = correctedName ?: existing.name
        val now = System.currentTimeMillis()
        val newExpiry = if (correctedCategory != null) {
            ExpiryCalculator.calculateDefaultExpiry(newCategory, now)
        } else {
            existing.expiryEstimate
        }

        val updated = existing.copy(
            name = newName,
            category = newCategory.name,
            expiryEstimate = newExpiry,
            status = ItemStatus.CONFIRMED.name,
            updatedAt = now
        )
        itemDao.updateItem(updated)
    }

    override suspend fun deleteItem(id: String) {
        itemDao.deleteItemById(id)
    }

    override suspend fun clearHousehold(householdId: String) {
        itemDao.clearHouseholdItems(householdId)
    }
}
