package com.larder.app.data.sync

import com.larder.app.data.remote.supabase.SupabaseAuthManager
import com.larder.app.domain.model.Category
import com.larder.app.domain.model.Item
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import java.util.UUID

class SyncManagerTest {

    @Test
    fun `test conflict resolution prefers local item if local updatedAt is newer`() {
        val now = System.currentTimeMillis()
        val itemId = UUID.randomUUID().toString()

        val localItem = Item(
            id = itemId,
            householdId = "hh_1",
            name = "Local Milk (Newer)",
            category = Category.DAIRY.name,
            expiryEstimate = now + 100000,
            updatedAt = now + 5000 // Newer
        )

        val remoteItem = Item(
            id = itemId,
            householdId = "hh_1",
            name = "Remote Milk (Older)",
            category = Category.DAIRY.name,
            expiryEstimate = now + 100000,
            updatedAt = now // Older
        )

        val syncManager = SyncManager(
            localItemDao = DummyItemDao(),
            remoteDataSource = DummyRemoteDataSource()
        )

        val winner = syncManager.resolveConflict(localItem, remoteItem)
        assertEquals("Local Milk (Newer)", winner.name)
    }

    @Test
    fun `test conflict resolution prefers remote item if remote updatedAt is newer`() {
        val now = System.currentTimeMillis()
        val itemId = UUID.randomUUID().toString()

        val localItem = Item(
            id = itemId,
            householdId = "hh_1",
            name = "Local Bread (Older)",
            category = Category.BAKERY.name,
            expiryEstimate = now + 100000,
            updatedAt = now // Older
        )

        val remoteItem = Item(
            id = itemId,
            householdId = "hh_1",
            name = "Remote Bread (Newer)",
            category = Category.BAKERY.name,
            expiryEstimate = now + 100000,
            updatedAt = now + 10000 // Newer
        )

        val syncManager = SyncManager(
            localItemDao = DummyItemDao(),
            remoteDataSource = DummyRemoteDataSource()
        )

        val winner = syncManager.resolveConflict(localItem, remoteItem)
        assertEquals("Remote Bread (Newer)", winner.name)
    }

    @Test
    fun `test household invite code format`() {
        val authManager = SupabaseAuthManager()
        val code = authManager.generateInviteCode("hh_100")
        assertTrue(code.startsWith("LRD-"))
        assertEquals(8, code.length)
    }
}

// Dummy DAO & DataSource for unit test isolation
private class DummyItemDao : com.larder.app.data.local.dao.ItemDao {
    override fun getItemsForHousehold(householdId: String) = kotlinx.coroutines.flow.flowOf(emptyList<Item>())
    override suspend fun getItemById(id: String): Item? = null
    override fun getExpiringItems(householdId: String, thresholdMillis: Long) = kotlinx.coroutines.flow.flowOf(emptyList<Item>())
    override fun getLowStockItems(householdId: String, minQuantity: Double) = kotlinx.coroutines.flow.flowOf(emptyList<Item>())
    override fun getItemsNeedingReview(householdId: String) = kotlinx.coroutines.flow.flowOf(emptyList<Item>())
    override suspend fun insertItem(item: Item) {}
    override suspend fun insertAll(items: List<Item>) {}
    override suspend fun updateItem(item: Item) {}
    override suspend fun deleteItem(item: Item) {}
    override suspend fun deleteItemById(id: String) {}
    override suspend fun clearHouseholdItems(householdId: String) {}
}

private class DummyRemoteDataSource : com.larder.app.data.remote.api.RemoteInventoryDataSource {
    override suspend fun fetchRemoteItems(householdId: String, sinceTimestamp: Long) = emptyList<Item>()
    override suspend fun pushItem(item: Item) = true
    override suspend fun pushItems(items: List<Item>) = true
    override suspend fun deleteRemoteItem(itemId: String) = true
}
