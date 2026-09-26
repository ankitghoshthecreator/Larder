package com.larder.app.feature.inference

import com.larder.app.domain.calculator.ExpiryCalculator
import com.larder.app.domain.model.Category
import com.larder.app.domain.model.Item
import com.larder.app.feature.camera.ScanType
import java.util.UUID

data class DetectedItemResult(
    val rawName: String,
    val categoryName: String,
    val confidence: Double,
    val quantity: Double = 1.0,
    val unit: String = "unit"
)

data class InferenceResponse(
    val scanType: ScanType,
    val rawOcrText: String? = null,
    val detectedItems: List<DetectedItemResult>,
    val processingTimeMs: Long
)

class InferenceClient {

    /**
     * Executes photo inference via Supabase /infer Edge Function payload.
     */
    suspend fun analyzeImage(
        householdId: String,
        signedImagePath: String,
        scanType: ScanType
    ): Result<List<Item>> {
        return try {
            val startTime = System.currentTimeMillis()
            
            // Call simulated Edge Function inference engine
            val response = executeRemoteInferenceCall(signedImagePath, scanType, startTime)
            val now = System.currentTimeMillis()

            val items = response.detectedItems.map { detected ->
                val category = Category.fromString(detected.categoryName)
                val status = ConfidenceThresholdEngine.determineItemStatus(detected.confidence)
                val expiry = ExpiryCalculator.calculateDefaultExpiry(category, now)

                Item(
                    id = UUID.randomUUID().toString(),
                    householdId = householdId,
                    name = detected.rawName,
                    category = category.name,
                    quantity = detected.quantity,
                    unit = detected.unit,
                    expiryEstimate = expiry,
                    sourceImagePath = signedImagePath,
                    status = status.name,
                    createdAt = now,
                    updatedAt = now
                )
            }

            Result.success(items)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    private fun executeRemoteInferenceCall(
        imagePath: String,
        scanType: ScanType,
        startTime: Long
    ): InferenceResponse {
        val detected = when (scanType) {
            ScanType.RECEIPT -> listOf(
                DetectedItemResult("Organic Valley Milk 1L", "Dairy", 0.94, 1.0, "L"),
                DetectedItemResult("Fresh Gala Apples 1kg", "Produce", 0.91, 1.0, "kg"),
                DetectedItemResult("Generic Cereal Box", "Pantry Staples", 0.72, 1.0, "pack") // Low confidence -> NEEDS_REVIEW
            )
            ScanType.SHELF -> listOf(
                DetectedItemResult("Canned Tomato Soup 400g", "Pantry Staples", 0.89, 2.0, "unit"),
                DetectedItemResult("Frozen Cheese Pizza", "Frozen Goods", 0.96, 1.0, "unit")
            )
        }

        return InferenceResponse(
            scanType = scanType,
            rawOcrText = if (scanType == ScanType.RECEIPT) "STORE #402 MILK 1L APPLES 1KG CEREAL" else null,
            detectedItems = detected,
            processingTimeMs = System.currentTimeMillis() - startTime
        )
    }
}
