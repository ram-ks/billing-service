package com.linxhealth.model

import java.time.LocalDate

data class Doctor(
    val id: String,
    val firstName: String,
    val lastName: String,
    val npiNumber: String,
    val practiceStartDate: LocalDate,
    val speciality: Speciality
)

enum class Speciality {
    GENERAL,
    CARDIOLOGY
}
