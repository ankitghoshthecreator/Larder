package com.larder.app.domain.model

enum class Category(val displayName: String, val defaultShelfLifeDays: Int) {
    PRODUCE("Produce", 5),
    DAIRY("Dairy", 7),
    MEAT_SEAFOOD("Meat & Seafood", 3),
    BAKERY("Bakery", 4),
    FROZEN("Frozen Goods", 90),
    PANTRY("Pantry Staples", 180),
    BEVERAGES("Beverages", 30),
    SNACKS("Snacks", 60),
    HOUSEHOLD("Household Items", 365),
    OTHER("Other", 14);

    companion object {
        fun fromString(value: String): Category {
            return entries.find { 
                it.name.equals(value, ignoreCase = true) || 
                it.displayName.equals(value, ignoreCase = true) 
            } ?: OTHER
        }
    }
}
