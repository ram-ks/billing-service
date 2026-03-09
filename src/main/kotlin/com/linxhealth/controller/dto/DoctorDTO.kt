package com.linxhealth.controller.dto

import com.fasterxml.jackson.annotation.JsonProperty
import com.linxhealth.model.Speciality

data class DoctorRequest(
    @JsonProperty("first_name") val firstName: String,
    @JsonProperty("last_name") val lastName: String,
    @JsonProperty("npi_number") val npiNumber: String,
    val speciality: Speciality,
    @JsonProperty("practice_start_date") val practiceStartDate: String  // dd/MM/yyyy
)

data class DoctorResponse(
    val id: Int,
    @JsonProperty("first_name") val firstName: String,
    @JsonProperty("last_name") val lastName: String,
    @JsonProperty("npi_number") val npiNumber: String,
    @JsonProperty("specialty") val speciality: Speciality,
    @JsonProperty("practice_start_date") val practiceStartDate: String
)