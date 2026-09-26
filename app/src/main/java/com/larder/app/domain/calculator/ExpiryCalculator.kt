package com.larder.app.domain.calculator

import com.larder.app.domain.model.Category
import java.util.Calendar
import java.util.concurrent.TimeUnit

object ExpiryCalculator {

    /**
     * Calculates estimated expiry timestamp (epoch millis) based on Category default shelf life.
     */
    fun calculateDefaultExpiry(
        category: Category,
        fromDateMillis: Long = System.currentTimeMillis()
    ): Long {
        val calendar = Calendar.getInstance().apply {
            timeInMillis = fromDateMillis
            add(Calendar.DAY_OF_YEAR, category.defaultShelfLifeDays)
        }
        return calendar.timeInMillis
    }

    /**
     * Calculates remaining days from now until the expiry timestamp.
     * Returns negative number if item has already expired.
     */
    fun calculateDaysRemaining(
        expiryTimestampMillis: Long,
        currentTimestampMillis: Long = System.currentTimeMillis()
    ): Long {
        val diffMillis = expiryTimestampMillis - currentTimestampMillis
        return TimeUnit.MILLISECONDS.toDays(diffMillis)
    }

    /**
     * Checks if an item expires within [thresholdDays] (default: 3 days).
     */
    fun isExpiringSoon(
        expiryTimestampMillis: Long,
        thresholdDays: Int = 3,
        currentTimestampMillis: Long = System.currentTimeMillis()
    ): Boolean {
        val daysLeft = calculateDaysRemaining(expiryTimestampMillis, currentTimestampMillis)
        return daysLeft in 0..thresholdDays
    }

    /**
     * Checks if an item is already expired.
     */
    fun isExpired(
        expiryTimestampMillis: Long,
        currentTimestampMillis: Long = System.currentTimeMillis()
    ): Boolean {
        return calculateDaysRemaining(expiryTimestampMillis, currentTimestampMillis) < 0
    }
}
