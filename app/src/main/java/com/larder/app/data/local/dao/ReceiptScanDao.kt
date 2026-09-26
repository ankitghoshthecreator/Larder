package com.larder.app.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.larder.app.domain.model.ReceiptScan
import kotlinx.coroutines.flow.Flow

@Dao
interface ReceiptScanDao {

    @Query("SELECT * FROM receipt_scans WHERE householdId = :householdId ORDER BY createdAt DESC")
    fun getScansForHousehold(householdId: String): Flow<List<ReceiptScan>>

    @Query("SELECT * FROM receipt_scans WHERE status = 'pending'")
    suspend fun getPendingScans(): List<ReceiptScan>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertScan(scan: ReceiptScan)

    @Update
    suspend fun updateScan(scan: ReceiptScan)

    @Query("DELETE FROM receipt_scans WHERE id = :id")
    suspend fun deleteScanById(id: String)
}
