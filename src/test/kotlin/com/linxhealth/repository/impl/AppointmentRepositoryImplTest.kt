package com.linxhealth.repository.impl

import com.linxhealth.model.Appointment
import com.linxhealth.model.AppointmentStatus
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertNotNull
import kotlin.test.assertEquals
import kotlin.test.assertNotEquals

class AppointmentRepositoryImplTest {
    private lateinit var appointmentRepository: AppointmentRepositoryImpl;

    private fun appointment(
        patientId: Int = 1,
        doctorId: Int = 1,
        status: AppointmentStatus = AppointmentStatus.SCHEDULED
    ): Appointment {
        return Appointment(
            id = null,
            patientId = patientId,
            doctorId = doctorId,
            appointmentStatus = status,
        )
    }

    @BeforeEach
    fun setUp() {
        appointmentRepository = AppointmentRepositoryImpl();
    }

    @Test
    fun `should save a new Appointment`() {
        val result = appointmentRepository.save(appointment());

        assertNotNull(result)
        assertEquals(AppointmentStatus.SCHEDULED.name, result.appointmentStatus.name)
    }

    @Test
    fun `should save and increment ID`() {
        val first = appointmentRepository.save(appointment());
        val second = appointmentRepository.save(appointment().copy(patientId = 2));

        assertNotEquals(first.id, second.id)
        assertEquals(2, appointmentRepository.findAll().size)
    }

    @Test
    fun `update should persist new status for existing appointment`() {
        val result = appointmentRepository.save(appointment())

        val updated = appointmentRepository.update(result.copy(appointmentStatus = AppointmentStatus.COMPLETED))

        assertEquals(AppointmentStatus.COMPLETED, updated.appointmentStatus)
        assertEquals(result.id, updated.id)
    }

    @Test
    fun `update should not create a new record`() {
        val saved = appointmentRepository.save(appointment())
        appointmentRepository.update(saved.copy(appointmentStatus = AppointmentStatus.COMPLETED))

        assertEquals(1, appointmentRepository.findAll().size)
    }

    @Test
    fun `update should reflect latest state in findById`() {
        val saved = appointmentRepository.save(appointment())
        appointmentRepository.update(saved.copy(appointmentStatus = AppointmentStatus.COMPLETED))

        val found = appointmentRepository.findById(saved.id!!)
        assertEquals(AppointmentStatus.COMPLETED, found?.appointmentStatus)
    }

    @Test
    fun `update should throw when appointment id is null`() {
        assertThrows(IllegalArgumentException::class.java) {
            appointmentRepository.update(appointment())   // id = null
        }
    }

    @Test
    fun `update should throw when appointment id does not exist in store`() {
        assertThrows(IllegalArgumentException::class.java) {
            appointmentRepository.update(appointment().copy(id = 999))
        }
    }

    @Test
    fun `findByPatientId should return all appointments for patient`() {
        appointmentRepository.save(appointment(patientId = 1))
        appointmentRepository.save(appointment(patientId = 1))
        appointmentRepository.save(appointment(patientId = 2))

        val result = appointmentRepository.findByPatientId(1)

        assertEquals(2, result.size)
        assertTrue(result.all { it.patientId == 1 })
    }

    @Test
    fun `findByPatientId should return empty list when no appointments for patient`() {
        appointmentRepository.save(appointment(patientId = 2))

        assertTrue(appointmentRepository.findByPatientId(1).isEmpty())
    }

}