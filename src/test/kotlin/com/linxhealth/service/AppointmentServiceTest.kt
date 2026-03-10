package com.linxhealth.service

import com.linxhealth.exception.NotFoundException
import com.linxhealth.exception.ValidationException
import com.linxhealth.model.Appointment
import com.linxhealth.model.AppointmentStatus
import com.linxhealth.model.Doctor
import com.linxhealth.model.Insurance
import com.linxhealth.model.Patient
import com.linxhealth.model.Speciality
import com.linxhealth.repository.AppointmentRepository
import com.linxhealth.repository.DoctorRepository
import com.linxhealth.repository.PatientRepository
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.mockito.kotlin.anyOrNull
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever
import java.time.LocalDate
import kotlin.test.assertEquals

class AppointmentServiceTest {
    private lateinit var appointmentRepository: AppointmentRepository
    private lateinit var patientRepository: PatientRepository
    private lateinit var doctorRepository: DoctorRepository
    private lateinit var appointmentService: AppointmentService

    private fun patient() = Patient(
        id = 1, firstName = "Thomas", lastName = "Edison",
        dateOfBirth = LocalDate.of(1990, 1, 15), age = 34,
        insurance = Insurance(121, "PCN001", "MEM001")
    )

    private fun doctor() = Doctor(
        id = 1, firstName = "Robert", lastName = "Oppenheimer",
        npiNumber = "NPI001", speciality = Speciality.CARDIOLOGY,
        practiceStartDate = LocalDate.of(2000, 6, 1)
    )

    private fun scheduledAppointment() = Appointment(
        id = 1, patientId = 1, doctorId = 1,
        appointmentStatus = AppointmentStatus.SCHEDULED
    )

    @BeforeEach
    fun setUp() {
        appointmentRepository = mock()
        patientRepository = mock()
        doctorRepository = mock()
        appointmentService = AppointmentService(appointmentRepository, patientRepository, doctorRepository)
    }

    @Test
    fun `should book an appointment`() {
        whenever(patientRepository.findById(1)).thenReturn(patient())
        whenever(doctorRepository.findById(1)).thenReturn(doctor())
        whenever(appointmentRepository.save(anyOrNull())).thenReturn(scheduledAppointment())

        val result = appointmentService.book(1, 1)

        verify(appointmentRepository).save(anyOrNull())
        assertEquals(AppointmentStatus.SCHEDULED, result.appointmentStatus)
        assertEquals(1, result.patientId)
        assertEquals(1, result.doctorId)
    }

    @Test
    fun `book should throw NotFoundException when patient does not exist`() {
        whenever(patientRepository.findById(99)).thenReturn(null)

        assertThrows<NotFoundException> { appointmentService.book(99, 1) }
    }

    @Test
    fun `book should throw NotFoundException when doctor does not exist`() {
        whenever(patientRepository.findById(1)).thenReturn(patient())
        whenever(doctorRepository.findById(99)).thenReturn(null)

        assertThrows<NotFoundException> { appointmentService.book(1, 99) }
    }

    @Test
    fun `updateStatus should call update on repository`() {
        val completed = scheduledAppointment().copy(appointmentStatus = AppointmentStatus.COMPLETED)
        whenever(appointmentRepository.findById(1)).thenReturn(scheduledAppointment())
        whenever(appointmentRepository.update(anyOrNull())).thenReturn(completed)

        appointmentService.updateStatus(1, AppointmentStatus.COMPLETED)

        verify(appointmentRepository).update(anyOrNull())
    }

    @Test
    fun `updateStatus should cancel appointment when appointment marked CANCELLED`() {
        val cancelled = scheduledAppointment().copy(appointmentStatus = AppointmentStatus.CANCELLED)
        whenever(appointmentRepository.findById(1)).thenReturn(scheduledAppointment())
        whenever(appointmentRepository.update(anyOrNull())).thenReturn(cancelled)

        val result = appointmentService.updateStatus(1, AppointmentStatus.CANCELLED)

        assertEquals(AppointmentStatus.CANCELLED, result.appointmentStatus)
    }

    @Test
    fun `updateStatus should throw NotFoundException when appointment does not exist`() {
        whenever(appointmentRepository.findById(99)).thenReturn(null)

        assertThrows<NotFoundException> { appointmentService.updateStatus(99, AppointmentStatus.COMPLETED) }
    }

    @Test
    fun `updateStatus should throw ValidationException for cancelled appointment`() {
        val cancelled = scheduledAppointment().copy(appointmentStatus = AppointmentStatus.CANCELLED)
        whenever(appointmentRepository.findById(1)).thenReturn(cancelled)

        assertThrows<ValidationException> { appointmentService.updateStatus(1, AppointmentStatus.COMPLETED) }
    }

    @Test
    fun `should delete appointment, when appointment exists`() {
        whenever(appointmentRepository.findById(1)).thenReturn(scheduledAppointment())

        appointmentService.delete(1)

        verify(appointmentRepository).delete(1)
    }

    @Test
    fun `delete should throw NotFoundException when appointment does not exist`() {
        whenever(appointmentRepository.findById(99)).thenReturn(null)

        assertThrows<NotFoundException> { appointmentService.delete(99) }
    }

    @Test
    fun `getAll should return all appointments`() {
        whenever(appointmentRepository.findAll())
            .thenReturn(listOf(scheduledAppointment(), scheduledAppointment().copy(id = 2)))

        assertEquals(2, appointmentService.getAll().size)
    }

    @Test
    fun `getAll should return empty list when no appointments exist`() {
        whenever(appointmentRepository.findAll()).thenReturn(emptyList())

        assertTrue(appointmentService.getAll().isEmpty())
    }
}