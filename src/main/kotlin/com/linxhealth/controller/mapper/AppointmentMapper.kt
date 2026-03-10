package com.linxhealth.controller.mapper

import com.linxhealth.controller.dto.AppointmentResponse
import com.linxhealth.controller.dto.BillResponse
import com.linxhealth.model.Appointment
import com.linxhealth.model.Bill

fun Appointment.toResponse(): AppointmentResponse =
    AppointmentResponse(
        id = id!!,
        patientId = patientId,
        doctorId = doctorId,
        status = appointmentStatus,
    )

fun Bill.toResponse(): BillResponse =
    BillResponse(
        fee = fee,
        discountPercentage = discountPercentage,
        discountAmount = discountAmount,
        amountAfterDiscount = amountAfterDiscount,
        taxAmount = taxAmount,
        totalAmount = afterTaxAndDiscount,
        amountCoveredByInsurance = amountCoveredByInsurance,
        coPayAmount = coPayAmount
    )
