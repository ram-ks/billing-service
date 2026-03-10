package com.linxhealth.repository

import com.linxhealth.model.Appointment

interface AppointmentRepository {
    fun save(appointment: Appointment): Appointment
    fun update(appointment: Appointment): Appointment
    fun findById(appointmentId: Int): Appointment?
    fun delete(appointmentId: Int)
    fun findByPatientId(patientId: Int): List<Appointment>
    fun findAll(): List<Appointment> // should return last 10 appointments
}
