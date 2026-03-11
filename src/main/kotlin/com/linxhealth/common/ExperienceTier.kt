package com.linxhealth.common

enum class ExperienceTier(val minYears: Int, val maxYears: Int) {
    JUNIOR(0, 19),
    MID(20, 29),
    SENIOR(30, 70);

    companion object {
        fun from(years: Int): ExperienceTier {
            return entries.firstOrNull{ years in it.minYears..it.maxYears }
                ?: throw IllegalArgumentException("Experience tier $years not found")
        }
    }
}