package com.linxhealth.repository.impl

import com.linxhealth.model.Appointment
import com.linxhealth.repository.AppointmentRepository
import jakarta.inject.Singleton
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.atomic.AtomicInteger

@Singleton
class AppointmentRepositoryImpl: AppointmentRepository {
    private val store: ConcurrentHashMap<Int, Appointment> = ConcurrentHashMap();
    private val idCounter: AtomicInteger = AtomicInteger()

    override fun save(appointment: Appointment): Appointment {
        val id = appointment.id ?: idCounter.incrementAndGet()
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