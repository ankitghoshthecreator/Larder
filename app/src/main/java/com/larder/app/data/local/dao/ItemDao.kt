package com.larder.app.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.larder.app.domain.model.Item
import kotlinx.coroutines.flow.Flow

@Dao
interface ItemDao {

    @Query("SELECT * FROM items WHERE householdId = :householdId ORDER BY expiryEstimate ASC")
    fun getItemsForHousehold(householdId: String): Flow<List<Item>>

    @Query("SELECT * FROM items WHERE id = :id")
    suspend fun getItemById(id: String): Item?

    @Query("SELECT * FROM items WHERE householdId = :householdId AND expiryEstimate <= :thresholdMillis ORDER BY expiryEstimate ASC")
    fun getExpiringItems(householdId: String, thresholdMillis: Long): Flow<List<Item>>

    @Query("SELECT * FROM items WHERE householdId = :householdId AND quantity <= :minQuantity")
    fun getLowStockItems(householdId: String, minQuantity: Double = 1.0): Flow<List<Item>>

    @Query("SELECT * FROM items WHERE householdId = :householdId AND status = 'NEEDS_REVIEW'")
    fun getItemsNeedingReview(householdId: String): Flow<List<Item>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertItem(item: Item)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(items: List<Item>)

    @Update
    suspend fun updateItem(item: Item)

    @Delete
    suspend fun deleteItem(item: Item)

    @Query("DELETE FROM items WHERE id = :id")
    suspend fun deleteItemById(id: String)

    @Query("DELETE FROM items WHERE householdId = :householdId")
    suspend fun clearHouseholdItems(householdId: String)
}
