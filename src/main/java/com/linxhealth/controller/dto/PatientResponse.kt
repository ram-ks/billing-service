package com.linxhealth.controller.dto

import com.fasterxml.jackson.annotation.JsonProperty

data class InsuranceResponse(
    @JsonProperty("bin_number") val binNumber: Int,
    @JsonProperty("pcn_number") val pcnNumber: Int,
    @JsonProperty("member_id") val memberId: Int,
)

data class PatientResponse(
    val id: String,
    @JsonProperty("first_name") val firstName: String,
    @JsonProperty("last_name") val lastName: String,
    val dob: String,
    val age: Int,
    val insurance: InsuranceResponse,
)