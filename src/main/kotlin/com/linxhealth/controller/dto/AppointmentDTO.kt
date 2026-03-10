package com.linxhealth.controller.dto

import com.fasterxml.jackson.annotation.JsonProperty
import com.linxhealth.model.AppointmentStatus
import io.micronaut.serde.annotation.Serdeable

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
    @field:JsonProperty("base_fee") val baseFee: Double,
    @field:JsonProperty("discount_percent") val discountPercent: Double,
    @field:JsonProperty("discount_amount") val discountAmount: Double,
    @field:JsonProperty("amount_after_discount") val amountAfterDiscount: Double,
    @field:JsonProperty("gst_amount") val gstAmount: Double,
    @field:JsonProperty("total_amount") val totalAmount: Double,
    @field:JsonProperty("insurance_amount") val insuranceAmount: Double,
    @field:JsonProperty("co_pay_amount") val coPayAmount: Double
)