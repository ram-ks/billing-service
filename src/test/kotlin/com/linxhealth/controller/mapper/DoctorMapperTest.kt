package com.linxhealth.controller.mapper

import com.linxhealth.controller.dto.DoctorRequest
import com.linxhealth.model.Doctor
import com.linxhealth.model.Speciality
import org.junit.jupiter.api.Test
import java.time.LocalDate
import kotlin.test.assertEquals

class DoctorMapperTest {

    @Test
    fun `should convert doctor request`() {
        val request = DoctorRequest(
            firstName = "Marie",
            lastName = "Curie",
            npiNumber = "NPI001",
            speciality = Speciality.NEUROLOGY,
            practiceStartDate = "10/01/2000",
        )

        val result = request.toModel()

        assertEquals(result.firstName, request.firstName)
        assertEquals(result.practiceStartDate, LocalDate.of(2000, 1, 10))
    }

    @Test
    fun `should convert doctor response`() {
        val doctor = Doctor(
            id = 1,
            firstName = "Marie",
            lastName = "Curie",
            npiNumber = "NPI001",
            speciality = Speciality.NEUROLOGY,
            practiceStartDate = LocalDate.of(2000, 1, 10),
        )

        val result = doctor.toResponse()

        assertEquals(doctor.lastName, result.lastName)
        assertEquals(result.practiceStartDate, "10/01/2000")
    }
}