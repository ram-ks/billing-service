package com.linxhealth.repository.impl

import com.linxhealth.model.Appointment
import com.linxhealth.repository.AppointmentRepository
import jakarta.inject.Singleton

@Singleton
class AppointmentRepositoryImpl: AppointmentRepository {
    private val store = mutableMapOf<Int, Appointment>()
    private var idCounter = 0

    override fun save(appointment: Appointment): Appointment {
        val id = appointment.id ?: ++idCounter
        val appointment = appointment.copy(id = id)
        store[id] = appointment
        return appointment
    }

    override fun update(appointment: Appointment): Appointment {
        val id = appointment.id
            ?: throw IllegalArgumentException("Cannot update appointment without id")
        if (!store.containsKey(id))
            throw IllegalArgumentException("Appointment with id $id not found")
        store[id] = appointment
        return appointment
    }

    override fun delete(appointmentId: Int) {
        if (!store.containsKey(appointmentId))
            throw IllegalArgumentException("Appointment with id $appointmentId not found")
        store.remove(appointmentId)
    }

    override fun findAll(): List<Appointment> {
        return store.values.toList()
    }

    override fun findById(appointmentId: Int): Appointment? {
        return store[appointmentId]
    }

    override fun findByPatientId(patientId: Int): List<Appointment> {
        return store.values.filter { it.patientId == patientId }
    }
}