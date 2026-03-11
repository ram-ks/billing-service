package com.linxhealth.common

import java.math.BigDecimal
import java.time.format.DateTimeFormatter

object Constants {
    val DOB_FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy")

    const val MIN_YEARS  = 0
    const val JUNIOR_MAX = 19
    const val MID_MIN    = 20
    const val MID_MAX    = 29
    const val SENIOR_MIN = 30
    const val MAX_YEARS  = 70

    val GST_RATE           = BigDecimal("0.12")
    val INSURANCE_COVERAGE = BigDecimal("0.90")
    val MAX_DISCOUNT       = BigDecimal("10")
    val HUNDRED            = BigDecimal("100")

    const val GENERAL_JUNIOR     = 500.0
    const val GENERAL_MID        = 800.0
    const val GENERAL_SENIOR     = 1200.0

    const val ORTHOPEDICS_JUNIOR = 800.0
    const val ORTHOPEDICS_MID    = 1000.0
    const val ORTHOPEDICS_SENIOR = 1500.0

    const val CARDIOLOGY_JUNIOR  = 1000.0
    const val CARDIOLOGY_MID     = 1500.0
    const val CARDIOLOGY_SENIOR  = 2000.0
}