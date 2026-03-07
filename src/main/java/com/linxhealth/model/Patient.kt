package com.linxhealth.model

import java.time.LocalDate

data class Patient(
    val id: String,
    val firstName: String,
    val lastName: String,
    val age: Int,
    val dateOfBirth: LocalDate,
    val insurance: Insurance,
)

