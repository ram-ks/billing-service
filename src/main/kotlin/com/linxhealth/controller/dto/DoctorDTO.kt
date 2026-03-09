package com.linxhealth.controller.dto

import com.fasterxml.jackson.annotation.JsonProperty
import com.linxhealth.model.Speciality
import io.micronaut.serde.annotation.Serdeable

@Serdeable
data class DoctorRequest(
    @field:JsonProperty("first_name") val firstName: String,
    @field:JsonProperty("last_name") val lastName: String,
    @field:JsonProperty("npi_number") val npiNumber: String,
    val speciality: Speciality,
    @field:JsonProperty("practice_start_date") val practiceStartDate: String  // dd/MM/yyyy
)

@Serdeable
data class DoctorResponse(
    val id: Int,
    @field:JsonProperty("first_name") val firstName: String,
    @field:JsonProperty("last_name") val lastName: String,
    @field:JsonProperty("npi_number") val npiNumber: String,
    val speciality: Speciality,
    @field:JsonProperty("practice_start_date") val practiceStartDate: String
)