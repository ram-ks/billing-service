package com.linxhealth.service

import com.linxhealth.common.BillCalculator
import com.linxhealth.common.ConsultationFeeKey
import com.linxhealth.common.FeeResolver
import com.linxhealth.exception.NotFoundException
import com.linxhealth.exception.ValidationException
import com.linxhealth.model.AppointmentStatus
import com.linxhealth.model.Bill
import com.linxhealth.model.Doctor
import com.linxhealth.repository.AppointmentRepository
import com.linxhealth.repository.DoctorRepository
import com.linxhealth.repository.PatientRepository
import jakarta.inject.Singleton
import java.time.LocalDate
import java.time.temporal.ChronoUnit

@Singleton
class BillingService(
    private val appointmentRepository: AppointmentRepository,
    private val patientRepository: PatientRepository,
    private val doctorRepository: DoctorRepository,
    private val feeResolver: FeeResolver<ConsultationFeeKey>,
    private val billCalculator: BillCalculator
) {
    fun getBill(appointmentId: Int): Bill {
        val appointment = appointmentRepository.findById(appointmentId)
            ?: throw NotFoundException("Appointment not found with id: $appointmentId")

        if (appointment.appointmentStatus != AppointmentStatus.COMPLETED)
            throw ValidationException("Bill is only available for COMPLETED appointments")
        return calculateBill(appointmentId)
    }

    fun calculateBill(appointmentId: Int): Bill {
        val appointment = appointmentRepository.findById(appointmentId)
            ?: throw NotFoundException("Appointment not found with id: $appointmentId")

        patientRepository.findById(appointment.patientId)
            ?: throw NotFoundException("Patient not found with id: $appointmentId")

        val doctor = doctorRepository.findById(appointment.doctorId)
            ?: throw NotFoundException("Doctor not found with id: $appointmentId")

        val completedAppointmentCount = appointmentRepository.findByPatientId(appointment.patientId)
            .count { it.appointmentStatus == AppointmentStatus.COMPLETED && it.id != appointmentId }

        val baseFee = getConsultationFee(doctor)

        return billCalculator.calculate(baseFee, completedAppointmentCount)
    }

    private fun getConsultationFee(doctor: Doctor): Double {
        val yearsOfExperience = ChronoUnit.YEARS.between(doctor.practiceStartDate, LocalDate.now()).toInt()
        return feeResolver.resolveFee(ConsultationFeeKey(doctor.speciality, yearsOfExperience));
    }
}