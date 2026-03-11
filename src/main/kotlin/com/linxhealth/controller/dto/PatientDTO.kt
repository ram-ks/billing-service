package com.linxhealth.controller.dto

import com.fasterxml.jackson.annotation.JsonProperty
import io.micronaut.serde.annotation.Serdeable

@Serdeable
data class PatientRequest(
    @field:JsonProperty("first_name") val firstName: String,
    @field:JsonProperty("last_name") val lastName: String,
    val dob: String,
    val age: Int,
    val insurance: InsuranceRequest,
)

@Serdeable
data class PatientResponse(
    val id: Int,
    @field:JsonProperty("first_name") val firstName: String,
    @field:JsonProperty("last_name") val lastName: String,
    val dob: String,
    val age: Int,
    val insurance: InsuranceResponse,
)
