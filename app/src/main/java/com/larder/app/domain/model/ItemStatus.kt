package com.larder.app.domain.model

enum class ItemStatus {
    CONFIRMED,
    NEEDS_REVIEW;

    companion object {
        fun fromString(value: String?): ItemStatus {
            return entries.find { it.name.equals(value, ignoreCase = true) } ?: CONFIRMED
        }
    }
}
