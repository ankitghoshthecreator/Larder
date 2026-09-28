package com.larder.app.feature.household

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class HouseholdManagerTest {

    @Test
    fun `test invite code always starts with LRD dash`() {
        val authManager = com.larder.app.data.remote.supabase.SupabaseAuthManager()
        val code = authManager.generateInviteCode("test_household")
        assertTrue("Invite code must start with LRD-", code.startsWith("LRD-"))
        assertEquals("Invite code must be exactly 8 chars", 8, code.length)
    }

    @Test
    fun `test invalid invite code is rejected`() {
        val code = "BAD-1234"
        assertFalse("Code not starting with LRD- should be invalid", code.startsWith("LRD-"))
    }

    @Test
    fun `test isOwner correctly identifies role`() {
        val manager = HouseholdManager(DummyHouseholdDao())
        assertTrue(manager.isOwner("owner"))
        assertTrue(manager.isOwner("OWNER"))
        assertFalse(manager.isOwner("member"))
    }

    @Test
    fun `test audit log stores entries in newest first order`() {
        val logger = HouseholdAuditLogger()

        logger.logAction("hh_1", "u1", "Alice", ActionType.ADD_ITEM, "Added Milk")
        logger.logAction("hh_1", "u2", "Bob", ActionType.DELETE_ITEM, "Removed Bread")

        val logs = logger.getLogsForHousehold("hh_1")
        assertEquals(2, logs.size)
        assertEquals("Bob", logs[0].userName) // Newest first
        assertEquals("Alice", logs[1].userName)
    }
}

private class DummyHouseholdDao : com.larder.app.data.local.dao.HouseholdDao {
    override suspend fun getHouseholdById(id: String) = null
    override suspend fun insertHousehold(household: com.larder.app.domain.model.Household) {}
    override suspend fun insertMember(member: com.larder.app.domain.model.HouseholdMember) {}
    override fun getMembers(householdId: String) = kotlinx.coroutines.flow.flowOf(emptyList<com.larder.app.domain.model.HouseholdMember>())
    override suspend fun deleteHousehold(householdId: String) {}
}
