package com.larder.app.data.remote.api

import com.larder.app.domain.model.Item

interface RemoteInventoryDataSource {
    suspend fun fetchRemoteItems(householdId: String, sinceTimestamp: Long = 0L): List<Item>
    suspend fun pushItem(item: Item): Boolean
    suspend fun pushItems(items: List<Item>): Boolean
    suspend fun deleteRemoteItem(itemId: String): Boolean
}

class SupabaseRemoteInventoryDataSource : RemoteInventoryDataSource {

    private val remoteMemoryStore = mutableMapOf<String, Item>()

    override suspend fun fetchRemoteItems(householdId: String, sinceTimestamp: Long): List<Item> {
        return remoteMemoryStore.values.filter { 
            it.householdId == householdId && it.updatedAt > sinceTimestamp 
        }
    }

    override suspend fun pushItem(item: Item): Boolean {
        remoteMemoryStore[item.id] = item
        return true
    }

    override suspend fun pushItems(items: List<Item>): Boolean {
        items.forEach { remoteMemoryStore[it.id] = it }
        return true
    }

    override suspend fun deleteRemoteItem(itemId: String): Boolean {
        remoteMemoryStore.remove(itemId)
        return true
    }
}
