package com.larder.app.data.worker

import com.larder.app.data.local.dao.ReceiptScanDao
import com.larder.app.domain.model.ReceiptScan
import com.larder.app.feature.camera.ScanType
import com.larder.app.feature.inference.InferenceClient

class ScanQueueWorker(
    private val receiptScanDao: ReceiptScanDao,
    private val inferenceClient: InferenceClient
) {

    /**
     * Queues a scan for offline processing if device is disconnected.
     */
    suspend fun queueScanForRetry(
        householdId: String,
        imagePath: String
    ): ReceiptScan {
        val scan = ReceiptScan(
            householdId = householdId,
            imagePath = imagePath,
            status = "pending",
            createdAt = System.currentTimeMillis()
        )
        receiptScanDao.insertScan(scan)
        return scan
    }

    /**
     * Process pending scans in background queue when network is reconnected.
     */
    suspend fun processPendingQueue(): Int {
        val pendingScans = receiptScanDao.getPendingScans()
        var processedCount = 0

        pendingScans.forEach { scan ->
            val result = inferenceClient.analyzeImage(
                householdId = scan.householdId,
                signedImagePath = scan.imagePath,
                scanType = ScanType.RECEIPT
            )

            if (result.isSuccess) {
                val updatedScan = scan.copy(status = "processed")
                receiptScanDao.updateScan(updatedScan)
                processedCount++
            }
        }

        return processedCount
    }
}
