package com.larder.app.domain.model

enum class UnitType(val symbol: String) {
    UNIT("unit"),
    PACK("pack"),
    KG("kg"),
    GRAM("g"),
    LITER("L"),
    ML("ml"),
    OZ("oz"),
    LB("lb");

    companion object {
        fun fromString(value: String?): UnitType {
            if (value.isNull_or_blank()) return UNIT
            return entries.find { 
                it.name.equals(value, ignoreCase = true) || 
                it.symbol.equals(value, ignoreCase = true) 
            } ?: UNIT
        }
    }
}

private fun String?.isNull_or_blank(): Boolean = this == null || this.trim().isEmpty()
