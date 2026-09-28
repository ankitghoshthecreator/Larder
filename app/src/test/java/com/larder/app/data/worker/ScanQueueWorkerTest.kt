package com.larder.app.data.worker

import com.larder.app.domain.model.ReceiptScan
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Test

class ScanQueueWorkerTest {

    @Test
    fun `test pending scan is queued and has pending status`() = runBlocking {
        val dao = FakeReceiptScanDao()
        val worker = ScanQueueWorker(dao, com.larder.app.feature.inference.InferenceClient())

        worker.queueScanForRetry("hh_test", "/storage/receipt_001.jpg")

        val pending = dao.getPendingScans()
        assertEquals(1, pending.size)
        assertEquals("pending", pending[0].status)
        assertEquals("/storage/receipt_001.jpg", pending[0].imagePath)
    }

    @Test
    fun `test process pending queue marks scans as processed`() = runBlocking {
        val dao = FakeReceiptScanDao()
        val worker = ScanQueueWorker(dao, com.larder.app.feature.inference.InferenceClient())

        worker.queueScanForRetry("hh_test", "/storage/receipt_002.jpg")
        val processed = worker.processPendingQueue()

        assertEquals(1, processed)
        val remaining = dao.getPendingScans()
        assertEquals(0, remaining.size)
    }
}

private class FakeReceiptScanDao : com.larder.app.data.local.dao.ReceiptScanDao {
    private val store = mutableListOf<ReceiptScan>()

    override fun getScansForHousehold(householdId: String) = flowOf(store.toList())
    override suspend fun getPendingScans() = store.filter { it.status == "pending" }
    override suspend fun insertScan(scan: ReceiptScan) { store.add(scan) }
    override suspend fun updateScan(scan: ReceiptScan) {
        val idx = store.indexOfFirst { it.id == scan.id }
        if (idx >= 0) store[idx] = scan
    }
    override suspend fun deleteScanById(id: String) { store.removeAll { it.id == id } }
}
