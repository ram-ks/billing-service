package com.linxhealth.controller.mapper

import com.linxhealth.controller.dto.AppointmentResponse
import com.linxhealth.model.Appointment

fun Appointment.toResponse(): AppointmentResponse =
    AppointmentResponse(
        id = id!!,
        patientId = patientId,
        doctorId = doctorId,
        status = appointmentStatus,
    )
