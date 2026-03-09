package com.linxhealth.service

import com.linxhealth.exception.NotFoundException
import com.linxhealth.model.Insurance
import com.linxhealth.model.Patient
import com.linxhealth.repository.PatientRepository
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.mockito.kotlin.mock
import org.mockito.kotlin.whenever
import java.time.LocalDate

class PatientServiceTest {

    private lateinit var patientRepository: PatientRepository
    private lateinit var service: PatientService

    private fun validPatient() = Patient(
        id = null,
        firstName = "Ram",
        lastName = "S",
        dateOfBirth = LocalDate.of(1990, 1, 15),
        age = 34,
        insurance = Insurance(121, "PCN001", "MEM001")
    )

    @BeforeEach
    fun setup() {
        patientRepository = mock()
        service = PatientService(patientRepository)
    }

    @Test
    fun `register should save and return patient`() {
        val patient = validPatient()
        val saved = patient.copy(id = 1)
        whenever(patientRepository.save(patient)).thenReturn(saved)

        val result = service.save(patient)

        assertEquals(1, result.id)
        assertEquals("Ram", result.firstName)
    }

    @Test
    fun `getById should return patient when found`() {
        val patient = validPatient().copy(id = 1)
        whenever(patientRepository.findById(1)).thenReturn(patient)

        val result = service.getById(1)

        assertEquals(1, result.id)
        assertEquals("Ram", result.firstName)
    }

    @Test
    fun `getById should throw NotFoundException when patient does not exist`() {
        whenever(patientRepository.findById(99)).thenReturn(null)

        assertThrows<NotFoundException> {
            service.getById(99)
        }
    }

    @Test
    fun `getById exception message should contain the id`() {
        whenever(patientRepository.findById(99)).thenReturn(null)

        val exception = assertThrows<NotFoundException> {
            service.getById(99)
        }

        assertTrue(exception.message!!.contains("99"))
    }

    @Test
    fun `getAll should return all registered patients`() {
        val patients = listOf(validPatient().copy(id = 1), validPatient().copy(id = 2))
        whenever(patientRepository.findAll()).thenReturn(patients)

        val result = service.getAll()

        assertEquals(2, result.size)
    }

    @Test
    fun `getAll should return empty list when no patients registered`() {
        whenever(patientRepository.findAll()).thenReturn(emptyList())

        val result = service.getAll()
        assertTrue(result.isEmpty())
    }
}