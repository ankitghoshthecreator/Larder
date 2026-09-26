package com.larder.app.domain.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.UUID

@Entity(tableName = "receipt_scans")
data class ReceiptScan(
    @PrimaryKey
    val id: String = UUID.randomUUID().toString(),
    val householdId: String,
    val imagePath: String,
    val rawOcrText: String? = null,
    val status: String = "pending", // "pending", "processed", "failed"
    val createdAt: Long = System.currentTimeMillis()
)
