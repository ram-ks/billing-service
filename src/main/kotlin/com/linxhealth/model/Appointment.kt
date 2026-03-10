package com.linxhealth.model

data class Appointment(
    val id: Int? = null,
    val patientId: Int,
    val doctorId: Int,
    val appointmentStatus: AppointmentStatus
)

enum class AppointmentStatus {
    SCHEDULED,
    COMPLETED,
    CANCELLED
}
