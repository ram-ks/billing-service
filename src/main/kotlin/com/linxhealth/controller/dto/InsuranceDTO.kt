package com.linxhealth.controller.dto

import com.fasterxml.jackson.annotation.JsonProperty
import io.micronaut.serde.annotation.Serdeable

@Serdeable
data class InsuranceRequest(
    @field:JsonProperty("bin_number") val binNumber: Int,
    @field:JsonProperty("pcn_number") val pcnNumber: String,
    @field:JsonProperty("member_id") val memberId: String,
)

@Serdeable
data class InsuranceResponse(
    @field:JsonProperty("bin_number") val binNumber: Int,
    @field:JsonProperty("pcn_number") val pcnNumber: String,
    @field:JsonProperty("member_id") val memberId: String,
)
