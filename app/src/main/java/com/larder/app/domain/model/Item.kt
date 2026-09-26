package com.larder.app.domain.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.UUID

@Entity(tableName = "items")
data class Item(
    @PrimaryKey
    val id: String = UUID.randomUUID().toString(),
    val householdId: String,
    val name: String,
    val category: String,
    val quantity: Double = 1.0,
    val unit: String = "unit",
    val expiryEstimate: Long, // Epoch timestamp in milliseconds
    val sourceImagePath: String? = null,
    val status: String = ItemStatus.CONFIRMED.name, // "CONFIRMED" or "NEEDS_REVIEW"
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis()
) {
    fun getCategoryEnum(): Category = Category.fromString(category)
    fun getUnitTypeEnum(): UnitType = UnitType.fromString(unit)
    fun getItemStatusEnum(): ItemStatus = ItemStatus.fromString(status)
}
