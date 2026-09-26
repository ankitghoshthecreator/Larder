package com.larder.app.domain.calculator

import com.larder.app.domain.model.Category
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import java.util.concurrent.TimeUnit

class ExpiryCalculatorTest {

    @Test
    fun `test dairy default shelf life is 7 days`() {
        val now = 1700000000000L
        val expiry = ExpiryCalculator.calculateDefaultExpiry(Category.DAIRY, now)
        val expected = now + TimeUnit.DAYS.toMillis(7)
        assertEquals(expected, expiry)
    }

    @Test
    fun `test produce default shelf life is 5 days`() {
        val now = 1700000000000L
        val expiry = ExpiryCalculator.calculateDefaultExpiry(Category.PRODUCE, now)
        val expected = now + TimeUnit.DAYS.toMillis(5)
        assertEquals(expected, expiry)
    }

    @Test
    fun `test days remaining calculation`() {
        val now = 1700000000000L
        val expiry = now + TimeUnit.DAYS.toMillis(3)
        val daysLeft = ExpiryCalculator.calculateDaysRemaining(expiry, now)
        assertEquals(3L, daysLeft)
    }

    @Test
    fun `test isExpiringSoon returns true for items expiring within threshold`() {
        val now = 1700000000000L
        val expiryIn2Days = now + TimeUnit.DAYS.toMillis(2)
        assertTrue(ExpiryCalculator.isExpiringSoon(expiryIn2Days, thresholdDays = 3, currentTimestampMillis = now))
    }

    @Test
    fun `test isExpiringSoon returns false for items expiring after threshold`() {
        val now = 1700000000000L
        val expiryIn10Days = now + TimeUnit.DAYS.toMillis(10)
        assertFalse(ExpiryCalculator.isExpiringSoon(expiryIn10Days, thresholdDays = 3, currentTimestampMillis = now))
    }

    @Test
    fun `test isExpired returns true for past dates`() {
        val now = 1700000000000L
        val expiredYesterday = now - TimeUnit.DAYS.toMillis(1)
        assertTrue(ExpiryCalculator.isExpired(expiredYesterday, currentTimestampMillis = now))
    }
}
