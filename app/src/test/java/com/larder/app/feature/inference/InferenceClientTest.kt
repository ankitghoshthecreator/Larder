package com.larder.app.feature.inference

import com.larder.app.domain.model.ItemStatus
import com.larder.app.feature.camera.ScanType
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class InferenceClientTest {

    @Test
    fun `test confidence threshold flags low confidence items as NEEDS_REVIEW`() {
        val lowConfidenceScore = 0.72 // Below 0.85 threshold
        val status = ConfidenceThresholdEngine.determineItemStatus(lowConfidenceScore)
        assertEquals(ItemStatus.NEEDS_REVIEW, status)
        assertTrue(ConfidenceThresholdEngine.requiresReview(lowConfidenceScore))
    }

    @Test
    fun `test confidence threshold confirms high confidence items`() {
        val highConfidenceScore = 0.94 // Above 0.85 threshold
        val status = ConfidenceThresholdEngine.determineItemStatus(highConfidenceScore)
        assertEquals(ItemStatus.CONFIRMED, status)
        assertFalse(ConfidenceThresholdEngine.requiresReview(highConfidenceScore))
    }

    @Test
    fun `test inference client parses items and assigns correct statuses`() = runBlocking {
        val client = InferenceClient()
        val result = client.analyzeImage(
            householdId = "test_hh",
            signedImagePath = "scans/test_receipt.jpg",
            scanType = ScanType.RECEIPT
        )

        assertTrue(result.isSuccess)
        val items = result.getOrThrow()
        assertEquals(3, items.size)

        // Item 1: High confidence Milk -> CONFIRMED
        val milk = items.find { it.name.contains("Milk") }!!
        assertEquals(ItemStatus.CONFIRMED.name, milk.status)

        // Item 3: Low confidence Cereal -> NEEDS_REVIEW
        val cereal = items.find { it.name.contains("Cereal") }!!
        assertEquals(ItemStatus.NEEDS_REVIEW.name, cereal.status)
    }
}
