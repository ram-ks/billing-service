package com.linxhealth.common

import com.linxhealth.common.Constants.JUNIOR_MAX
import com.linxhealth.common.Constants.MAX_YEARS
import com.linxhealth.common.Constants.MID_MAX
import com.linxhealth.common.Constants.MID_MIN
import com.linxhealth.common.Constants.MIN_YEARS
import com.linxhealth.common.Constants.SENIOR_MIN

enum class ExperienceTier(val minYears: Int, val maxYears: Int) {
    JUNIOR(MIN_YEARS, JUNIOR_MAX),
    MID(MID_MIN, MID_MAX),
    SENIOR(SENIOR_MIN, MAX_YEARS);

    companion object {
        fun from(years: Int): ExperienceTier {
            return entries.firstOrNull{ years in it.minYears..it.maxYears }
                ?: throw IllegalArgumentException("Experience tier $years not found")
        }
    }
}