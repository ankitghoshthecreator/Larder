package com.larder.app.feature.mcp

import com.larder.app.domain.calculator.ExpiryCalculator
import com.larder.app.domain.model.Category
import com.larder.app.domain.model.Item
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import java.util.UUID

class McpServerHandlerTest {

    private val now = System.currentTimeMillis()

    private val testItems = listOf(
        Item(
            id = UUID.randomUUID().toString(),
            householdId = "hh_test",
            name = "Organic Whole Milk 1L",
            category = Category.DAIRY.name,
            quantity = 2.0,
            unit = "L",
            expiryEstimate = now + (2 * 24 * 60 * 60 * 1000L) // 2 days left
        ),
        Item(
            id = UUID.randomUUID().toString(),
            householdId = "hh_test",
            name = "Greek Yogurt 500g",
            category = Category.DAIRY.name,
            quantity = 1.0,
            unit = "g",
            expiryEstimate = now + (5 * 24 * 60 * 60 * 1000L) // 5 days left
        ),
        Item(
            id = UUID.randomUUID().toString(),
            householdId = "hh_test",
            name = "Fresh Avocados 4-pack",
            category = Category.PRODUCE.name,
            quantity = 4.0,
            unit = "unit",
            expiryEstimate = now + (3 * 24 * 60 * 60 * 1000L) // 3 days left
        )
    )

    private val mockRepository = object : com.larder.app.data.repository.InventoryRepository {
        override fun getInventoryItems(householdId: String) = flowOf(testItems)
        override fun getExpiringItems(householdId: String, daysThreshold: Int) = flowOf(testItems)
        override fun getLowStockItems(householdId: String, minQuantity: Double) = flowOf(emptyList())
        override fun getItemsNeedingReview(householdId: String) = flowOf(emptyList())
        override suspend fun getItemById(id: String) = testItems.find { it.id == id }
        override suspend fun addItem(
            householdId: String,
            name: String,
            category: Category,
            quantity: Double,
            unit: String,
            customExpiryMillis: Long?,
            sourceImagePath: String?,
            status: com.larder.app.domain.model.ItemStatus
        ): Item = testItems.first()
        override suspend fun updateItem(item: Item) {}
        override suspend fun confirmItemReview(itemId: String, correctedName: String?, correctedCategory: Category?) {}
        override suspend fun deleteItem(id: String) {}
        override suspend fun clearHousehold(householdId: String) {}
    }

    @Test
    fun `test list_expiring caps days to 30 days max and filters correctly`() = runBlocking {
        val handler = McpServerHandler(mockRepository)
        val expiringWithin3Days = handler.listExpiring("hh_test", days = 3)
        
        assertEquals(2, expiringWithin3Days.size)
        assertTrue(expiringWithin3Days.any { it.name.contains("Milk") })
        assertTrue(expiringWithin3Days.any { it.name.contains("Avocados") })
    }

    @Test
    fun `test check_stock returns total quantity and availability`() = runBlocking {
        val handler = McpServerHandler(mockRepository)
        val milkStock = handler.checkStock("hh_test", "Milk")

        assertTrue(milkStock.isAvailable)
        assertEquals(2.0, milkStock.totalQuantity, 0.01)
        assertEquals("L", milkStock.unit)
    }

    @Test
    fun `test get_category_summary groups items by category accurately`() = runBlocking {
        val handler = McpServerHandler(mockRepository)
        val summary = handler.getCategorySummary("hh_test")

        assertEquals(2, summary.size) // Dairy and Produce
        val dairySummary = summary.find { it.categoryName == Category.DAIRY.name }!!
        assertEquals(2, dairySummary.itemCount)
        assertEquals(3.0, dairySummary.totalQuantity, 0.01)
    }
}
