package com.linxhealth.service

import com.linxhealth.exception.ConflictException
import com.linxhealth.exception.NotFoundException
import com.linxhealth.model.Doctor
import com.linxhealth.model.Speciality
import com.linxhealth.repository.DoctorRepository
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.mockito.Mockito.mock
import org.mockito.kotlin.anyOrNull
import org.mockito.kotlin.whenever
import java.time.LocalDate

class DoctorServiceTest {
    private lateinit var doctorRepository: DoctorRepository;
    private lateinit var doctorService: DoctorService;

    private fun getDoctor() = Doctor(
        id = null,
        firstName = "Werner",
        lastName = "Heisenberg",
        npiNumber = "NPI001",
        speciality = Speciality.CARDIOLOGY,
        practiceStartDate = LocalDate.of(2010, 6, 1)
    )

    @BeforeEach()
    fun setUp() {
        doctorRepository = mock()
        doctorService = DoctorService(doctorRepository)
    }

    @Test
    fun `should save and return doctor`() {
        val doctor = getDoctor()
        val saved = doctor.copy(id = 1)
        whenever(doctorRepository.save(anyOrNull())).thenReturn(saved)

        val result = doctorService.save(doctor)

        assertEquals(1, result.id)
        assertEquals("Werner", result.firstName)
    }

    @Test
    fun `register should throw ConflictException when NPI already exists`() {
        val doctor = getDoctor()
        whenever(doctorRepository.findByNpiNumber(doctor.npiNumber)).thenReturn(doctor.copy(id = 1))

        assertThrows<ConflictException> {
            doctorService.save(doctor)
        }
    }

    @Test
    fun `register conflict message should contain NPI number`() {
        val doctor = getDoctor()
        whenever(doctorRepository.findByNpiNumber(doctor.npiNumber)).thenReturn(doctor.copy(id = 1))

        val exception = assertThrows<ConflictException> {
            doctorService.save(doctor)
        }

        assertTrue(exception.message!!.contains("Doctor already exists"))
    }

    @Test
    fun `getById should return doctor when found`() {
        val doctor = getDoctor().copy(id = 1)
        whenever(doctorRepository.findById(1)).thenReturn(doctor)

        val result = doctorService.getById(1)

        assertEquals(1, result.id)
    }

    @Test
    fun `getById should throw NotFoundException when doctor does not exist`() {
        whenever(doctorRepository.findById(99)).thenReturn(null)

        val exception = assertThrows<NotFoundException> {
            doctorService.getById(99)
        }

        assertTrue(exception.message!!.contains("99"))
    }

    @Test
    fun `getAll should return all doctors`() {
        whenever(doctorRepository.findAll()).thenReturn(listOf(getDoctor().copy(id = 1), getDoctor().copy(id = 2)))

        val result = doctorService.getAll()

        assertEquals(2, result.size)
    }

    @Test
    fun `getAll should return empty list when no doctors registered`() {
        whenever(doctorRepository.findAll()).thenReturn(emptyList())

        assertTrue(doctorService.getAll().isEmpty())
    }
}