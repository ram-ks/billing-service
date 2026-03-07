package com.linxhealth.service

import com.linxhealth.model.Doctor
import java.time.LocalDate
import java.time.temporal.ChronoUnit

class BillingService {
    companion object {
        const val GST_RATE = 0.12
        const val INSURANCE_COVERAGE = 0.90
        const val MAX_DISCOUNT_PERCENT = 10.0
    }

    fun calculateConsultation(doctor: Doctor) {
        val years = ChronoUnit.YEARS.between(doctor.practiceStartDate, LocalDate.now())
    }
}