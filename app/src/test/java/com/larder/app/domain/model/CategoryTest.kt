package com.larder.app.domain.model

import org.junit.Assert.assertEquals
import org.junit.Test

class CategoryTest {

    @Test
    fun `test Category string parsing matches exact or fallback`() {
        assertEquals(Category.PRODUCE, Category.fromString("PRODUCE"))
        assertEquals(Category.DAIRY, Category.fromString("Dairy"))
        assertEquals(Category.MEAT_SEAFOOD, Category.fromString("Meat & Seafood"))
        assertEquals(Category.OTHER, Category.fromString("Unknown Category Name"))
    }

    @Test
    fun `test UnitType string parsing`() {
        assertEquals(UnitType.KG, UnitType.fromString("kg"))
        assertEquals(UnitType.GRAM, UnitType.fromString("g"))
        assertEquals(UnitType.UNIT, UnitType.fromString(null))
        assertEquals(UnitType.UNIT, UnitType.fromString("invalid"))
    }

    @Test
    fun `test ItemStatus string parsing`() {
        assertEquals(ItemStatus.NEEDS_REVIEW, ItemStatus.fromString("NEEDS_REVIEW"))
        assertEquals(ItemStatus.CONFIRMED, ItemStatus.fromString("CONFIRMED"))
        assertEquals(ItemStatus.CONFIRMED, ItemStatus.fromString("invalid"))
    }
}
