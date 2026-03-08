package com.linxhealth.controller

import com.linxhealth.controller.dto.InsuranceRequest
import com.linxhealth.controller.dto.PatientRequest
import com.linxhealth.exception.NotFoundException
import com.linxhealth.model.Insurance
import com.linxhealth.model.Patient
import com.linxhealth.service.PatientService
import io.micronaut.http.HttpStatus
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.Test
import org.mockito.kotlin.any
import org.mockito.kotlin.mock
import org.mockito.kotlin.whenever
import java.time.LocalDate

class PatientControllerTest {

    private val patientService: PatientService = mock()
    private val controller = PatientController(patientService)

    // ── Fixtures ──────────────────────────────────────────────────────────────

    private fun validRequest() = PatientRequest(
        firstName = "John",
        lastName = "Doe",
        dob = "15/01/1990",
        age = 34,
        insurance = InsuranceRequest(121, "PCN001", "MEM001")
    )

    private fun savedPatient() = Patient(
        id = 1,
        firstName = "John",
        lastName = "Doe",
        dateOfBirth = LocalDate.of(1990, 1, 15),
        age = 34,
        insurance = Insurance(121, "PCN001", "MEM001")
    )

    // ── POST /patients ────────────────────────────────────────────────────────

    @Test
    fun `register should return 201 with saved patient`() {
        whenever(patientService.save(any())).thenReturn(savedPatient())

        val response = controller.save(validRequest())

        assertEquals(HttpStatus.CREATED, response.status)
        assertEquals(1, response.body()?.id)
        assertEquals("John", response.body()?.firstName)
        assertEquals("15/01/1990", response.body()?.dob)
    }

    // ── GET /patients/{id} ────────────────────────────────────────────────────

    @Test
    fun `getById should return 200 with patient when found`() {
        whenever(patientService.getById(1)).thenReturn(savedPatient())

        val response = controller.getById(1)

        assertEquals(HttpStatus.OK, response.status)
        assertEquals(1, response.body()?.id)
        assertEquals("John", response.body()?.firstName)
    }

    @Test
    fun `getById should propagate NotFoundException when patient not found`() {
        whenever(patientService.getById(99))
            .thenThrow(NotFoundException("Patient not found with id: 99"))

        assertThrows(NotFoundException::class.java) {
            controller.getById(99)
        }
    }

    // ── GET /patients ─────────────────────────────────────────────────────────

    @Test
    fun `getAll should return 200 with list of patients`() {
        whenever(patientService.getAll())
            .thenReturn(listOf(savedPatient(), savedPatient().copy(id = 2)))

        val response = controller.getAll()

        assertEquals(HttpStatus.OK, response.status)
        assertEquals(2, response.body()?.size)
    }

    @Test
    fun `getAll should return 200 with empty list when no patients exist`() {
        whenever(patientService.getAll()).thenReturn(emptyList())

        val response = controller.getAll()

        assertEquals(HttpStatus.OK, response.status)
        assertEquals(0, response.body()?.size)
    }
}