package com.linxhealth.repository.impl

import com.linxhealth.model.Doctor
import com.linxhealth.model.Speciality
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertNotNull
import org.junit.jupiter.api.assertNull
import java.time.LocalDate
import kotlin.test.assertEquals

class DoctorRepositoryImplTest {
    private lateinit var doctorRepository: DoctorRepositoryImpl

    private fun getDoctor(npiNumber: String = "NPI001") = Doctor(
        firstName = "Max",
        lastName = "Planck",
        npiNumber = npiNumber,
        speciality = Speciality.NEUROLOGY,
        practiceStartDate = LocalDate.of(2001, 1, 1),
    )

    @BeforeEach
    fun setUp() {
        doctorRepository = DoctorRepositoryImpl()
    }

    @Test
    fun `should persist Doctor`() {
        val result = doctorRepository.save(getDoctor("NPI002"))

        assertNotNull(result)
        assertEquals(1, result.id)
    }

    @Test
    fun `should auto increment ID when persisting doctor`() {
        doctorRepository.save(getDoctor())
        val result = doctorRepository.save(getDoctor())

        assertEquals(2, result.id)
    }

    @Test
    fun `should not return result if doctor does not exist`() {
        val result = doctorRepository.findById(99)
        assertNull(result)
    }

    @Test
    fun `should return doctor by NPI number`() {
        doctorRepository.save(getDoctor())
        val result = doctorRepository.findByNpiNumber("NPI001")

        assertNotNull(result)
        assertEquals(1, result.id)
        assertEquals("NPI001", result.npiNumber)
    }

    @Test
    fun `should return null for unknown NPI number`() {
        doctorRepository.save(getDoctor())
        val result = doctorRepository.findByNpiNumber("NPI003")

        assertNull(result)
    }

    @Test
    fun `findAll should return all saved doctors`() {
        doctorRepository.save(getDoctor("NPI001"))
        doctorRepository.save(getDoctor("NPI002"))

        assertEquals(2, doctorRepository.findAll().size)
    }

    @Test
    fun `findAll should return empty list when no doctors saved`() {
        assertTrue(doctorRepository.findAll().isEmpty())
    }
}