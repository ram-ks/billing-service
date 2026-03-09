package com.linxhealth.controller.mapper

import com.linxhealth.controller.dto.InsuranceRequest
import com.linxhealth.controller.dto.PatientRequest
import com.linxhealth.model.Insurance
import com.linxhealth.model.Patient
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertNotNull
import java.time.LocalDate
import kotlin.test.assertEquals

class PatientMapperTest {

    @Test
    fun `should convert Patient request`() {
        val request = PatientRequest(
            firstName = "Ram",
            lastName = "S",
            dob = "10/04/1992",
            age = 33,
            insurance = InsuranceRequest(
                binNumber = 123,
                pcnNumber = "45",
                memberId = "345"
            )
        )

        val result = request.toModel()

        assertEquals("Ram", request.firstName)
        assertEquals(result.dateOfBirth, LocalDate.of(1992, 4, 10))
        assertNotNull(result.insurance)
    }

    @Test
    fun `should convert Patient to PatientResponse`() {
        val patient = Patient(
            id = 1,
            firstName = "John",
            lastName = "Doe",
            dateOfBirth = LocalDate.of(1992, 5, 10),
            age = 34,
            insurance = Insurance(
                binNumber = 123,
                pcnNumber = "456",
                memberId = "789"
            )
        )

        val response = patient.toResponse()

        assertEquals(1, response.id)
        assertEquals("John", response.firstName)
        assertEquals(123, response.insurance.binNumber)
    }
}