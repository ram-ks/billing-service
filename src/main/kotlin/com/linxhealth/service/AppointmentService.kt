package com.linxhealth.service

import com.linxhealth.exception.NotFoundException
import com.linxhealth.exception.ValidationException
import com.linxhealth.model.Appointment
import com.linxhealth.model.AppointmentStatus
import com.linxhealth.repository.AppointmentRepository
import com.linxhealth.repository.DoctorRepository
import com.linxhealth.repository.PatientRepository
import jakarta.inject.Singleton

@Singleton
class AppointmentService(
    private val appointmentRepository: AppointmentRepository,
    private val patientRepository: PatientRepository,
    private val doctorRepository: DoctorRepository
) {
    fun book(patientId: Int, doctorId: Int): Appointment {
        patientRepository.findById(patientId)
            ?: throw NotFoundException("Patient not found with id: $patientId")
        doctorRepository.findById(doctorId)
            ?: throw NotFoundException("Doctor not found with id: $doctorId")

        // TODO: should we have another status for Appointment, like schedule, checked-in, completed, cancelled
        return appointmentRepository.save(
            Appointment(patientId = patientId, doctorId = doctorId, appointmentStatus = AppointmentStatus.SCHEDULED)
        )
    }

    fun getByID(appointmentId: Int): Appointment {
        return findAppointment(appointmentId)
    }

    fun delete(appointmentId: Int) {
        findAppointment(appointmentId)
        appointmentRepository.delete(appointmentId)
    }

    fun getAll(): List<Appointment> {
        return appointmentRepository.findAll()
    }

    fun updateStatus(appointmentId: Int, status: AppointmentStatus): Appointment {
        val appointment = findAppointment(appointmentId)
        if (appointment.appointmentStatus == AppointmentStatus.CANCELLED) {
            throw ValidationException("Cannot update cancelled appointment")
        }

        return appointmentRepository.update(
            appointment.copy(appointmentStatus = status),
        )
    }

    private fun findAppointment(appointmentId: Int): Appointment {
        return appointmentRepository.findById(appointmentId) ?: throw NotFoundException("Appointment $appointmentId not found")
    }
}