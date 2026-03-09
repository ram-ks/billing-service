package com.linxhealth.controller.dto

import com.fasterxml.jackson.annotation.JsonProperty
import io.micronaut.serde.annotation.Serdeable

@Serdeable
data class InsuranceResponse(
    @JsonProperty("bin_number") val binNumber: Int,
    @JsonProperty("pcn_number") val pcnNumber: String,
    @JsonProperty("member_id") val memberId: String,
)

@Serdeable
data class PatientResponse(
    val id: Int,
    @JsonProperty("first_name") val firstName: String,
    @JsonProperty("last_name") val lastName: String,
    val dob: String,
    val age: Int,
    val insurance: InsuranceResponse,
)

// TODO: combine both into 1 PAtientDTO
