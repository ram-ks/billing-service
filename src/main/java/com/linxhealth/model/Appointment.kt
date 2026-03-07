package com.linxhealth.model

data class Appointment(
    val id: String,
    val patientId: String,
    val doctorId: String,
    val appointmentStatus: AppointmentStatus
)

enum class AppointmentStatus {
    SCHEDULED,
    COMPLETED,
}
