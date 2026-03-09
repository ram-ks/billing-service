package com.linxhealth.model

import io.micronaut.serde.annotation.Serdeable
import java.time.LocalDate

data class Doctor(
    val id: Int? = null,
    val firstName: String,
    val lastName: String,
    val npiNumber: String,
    val practiceStartDate: LocalDate,
    val speciality: Speciality
)

@Serdeable
enum class Speciality {
    GENERAL,
    CARDIOLOGY,
    ORTHOPEDICS,
    DERMATOLOGY,
    NEUROLOGY
}
