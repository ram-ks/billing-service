package com.linxhealth.controller.dto

import com.fasterxml.jackson.annotation.JsonProperty

data class InsuranceRequest(
    @get:JsonProperty("bin_number") val binNumber: Int,
    @get:JsonProperty("pcn_number") val pcnNumber: String,
    @get:JsonProperty("member_id") val memberId: String,
)

data class PatientRequest(
    @get:JsonProperty("first_name") val firstName: String,
    @get:JsonProperty("last_name") val lastName: String,
    val dob: String,
    val age: Int,
    val insurance: InsuranceRequest,
)
