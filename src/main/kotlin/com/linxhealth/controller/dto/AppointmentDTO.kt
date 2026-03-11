package com.linxhealth.controller.dto

import com.fasterxml.jackson.annotation.JsonProperty
import com.linxhealth.model.AppointmentStatus
import io.micronaut.serde.annotation.Serdeable
import java.math.BigDecimal

@Serdeable
data class AppointmentRequest(
    @field:JsonProperty("patient_id") val patientId: Int,
    @field:JsonProperty("doctor_id") val doctorId: Int
)

@Serdeable
data class UpdateStatusRequest(
    val status: AppointmentStatus
)

@Serdeable
data class AppointmentResponse(
    val id: Int,
    @field:JsonProperty("patient_id") val patientId: Int,
    @field:JsonProperty("doctor_id") val doctorId: Int,
    val status: AppointmentStatus,
    val bill: BillResponse? = null
)

@Serdeable
data class BillResponse(
    val fee: BigDecimal,
    @field:JsonProperty("discount_percentage") val discountPercentage: BigDecimal,
    @field:JsonProperty("discount_amount") val discountAmount: BigDecimal,
    @field:JsonProperty("amount_after_discount") val amountAfterDiscount: BigDecimal,
    @field:JsonProperty("tax_amount") val taxAmount: BigDecimal,
    @field:JsonProperty("total_amount") val totalAmount: BigDecimal,
    @field:JsonProperty("insurance_amount") val amountCoveredByInsurance: BigDecimal,
    @field:JsonProperty("co_pay_amount") val coPayAmount: BigDecimal
)