package com.larder.app.data.sync

import com.larder.app.data.local.dao.ItemDao
import com.larder.app.data.remote.api.RemoteInventoryDataSource
import com.larder.app.domain.model.Item
import kotlinx.coroutines.flow.first

sealed class SyncResult {
    data class Success(val itemsPushed: Int, val itemsPulled: Int) : SyncResult()
    data class Error(val message: String, val cause: Throwable? = null) : SyncResult()
}

class SyncManager(
    private val localItemDao: ItemDao,
    private val remoteDataSource: RemoteInventoryDataSource
) {

    private var lastSyncTimestamp: Long = 0L

    /**
     * Executes bidirectional sync using Last-Write-Wins based on `updatedAt`.
     */
    suspend fun syncHouseholdInventory(householdId: String): SyncResult {
        return try {
            val localItems = localItemDao.getItemsForHousehold(householdId).first()
            val remoteItems = remoteDataSource.fetchRemoteItems(householdId, lastSyncTimestamp)

            val localMap = localItems.associateBy { it.id }
            val remoteMap = remoteItems.associateBy { it.id }

            val itemsToInsertLocally = mutableListOf<Item>()
            val itemsToPushRemotely = mutableListOf<Item>()

            // 1. Process local items against remote
            localItems.forEach { localItem ->
                val remoteItem = remoteMap[localItem.id]
                if (remoteItem == null) {
                    itemsToPushRemotely.add(localItem)
                } else {
                    // Conflict Resolution: Last-Write-Wins
                    if (localItem.updatedAt > remoteItem.updatedAt) {
                        itemsToPushRemotely.add(localItem)
                    } else if (remoteItem.updatedAt > localItem.updatedAt) {
                        itemsToInsertLocally.add(remoteItem)
                    }
                }
            }

            // 2. Process remote items not present locally
            remoteItems.forEach { remoteItem ->
                if (!localMap.containsKey(remoteItem.id)) {
                    itemsToInsertLocally.add(remoteItem)
                }
            }

            // Execute DB writes & pushes
            if (itemsToInsertLocally.isNotEmpty()) {
                localItemDao.insertAll(itemsToInsertLocally)
            }
            if (itemsToPushRemotely.isNotEmpty()) {
                remoteDataSource.pushItems(itemsToPushRemotely)
            }

            lastSyncTimestamp = System.currentTimeMillis()

            SyncResult.Success(
                itemsPushed = itemsToPushRemotely.size,
                itemsPulled = itemsToInsertLocally.size
            )
        } catch (e: Exception) {
            SyncResult.Error(message = e.message ?: "Sync failed", cause = e)
        }
    }

    /**
     * Resolves conflict between a local and remote item using Last-Write-Wins rules.
     */
    fun resolveConflict(local: Item, remote: Item): Item {
        return if (local.updatedAt >= remote.updatedAt) local else remote
    }
}
