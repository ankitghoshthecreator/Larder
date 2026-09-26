package com.larder.app.feature.inference

import com.larder.app.domain.model.ItemStatus

object ConfidenceThresholdEngine {

    // Target confidence threshold (0.85 = 85%)
    const val DEFAULT_CONFIDENCE_THRESHOLD = 0.85

    fun determineItemStatus(
        confidenceScore: Double,
        threshold: Double = DEFAULT_CONFIDENCE_THRESHOLD
    ): ItemStatus {
        return if (confidenceScore >= threshold) {
            ItemStatus.CONFIRMED
        } else {
            ItemStatus.NEEDS_REVIEW
        }
    }

    fun requiresReview(
        confidenceScore: Double,
        threshold: Double = DEFAULT_CONFIDENCE_THRESHOLD
    ): Boolean {
        return confidenceScore < threshold
    }
}
