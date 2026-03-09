package com.linxhealth.controller

import com.linxhealth.controller.dto.DoctorRequest
import com.linxhealth.exception.ConflictException
import com.linxhealth.exception.NotFoundException
import com.linxhealth.model.Doctor
import com.linxhealth.model.Speciality
import com.linxhealth.service.DoctorService
import io.micronaut.http.HttpStatus
import io.micronaut.test.annotation.MockBean
import io.micronaut.test.extensions.junit5.annotation.MicronautTest
import jakarta.inject.Inject
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.Test
import org.mockito.Mockito.mock
import org.mockito.kotlin.anyOrNull
import org.mockito.kotlin.whenever
import java.time.LocalDate

@MicronautTest
class DoctorControllerTest {
    @Inject
    lateinit var doctorController: DoctorController

    @Inject
    lateinit var doctorService: DoctorService

    @MockBean(DoctorService::class)
    fun doctorService(): DoctorService = mock()

    private fun validRequestBody() = DoctorRequest(
        firstName = "Albert",
        lastName = "Einstein",
        npiNumber = "NPI001",
        practiceStartDate = "03/12/2001",
        speciality = Speciality.NEUROLOGY,
    )

    private fun getDoctor() = Doctor(
        id = 1,
        firstName = "Albert",
        lastName = "Einstein",
        npiNumber = "NPI001",
        speciality = Speciality.NEUROLOGY,
        practiceStartDate = LocalDate.of(2001, 12, 31),
    )

    @Test
    fun `save should return 201 with saved doctor`() {
        whenever(doctorService.save(anyOrNull())).thenReturn(getDoctor())

        val response = doctorController.save(validRequestBody())

        assertEquals(HttpStatus.CREATED, response.status)
        assertEquals(1, response.body()?.id)
        assertEquals("Albert", response.body()?.firstName)
        assertEquals("NPI001", response.body()?.npiNumber)
        assertEquals(Speciality.NEUROLOGY, response.body()?.speciality)
        assertEquals("31/12/2001", response.body()?.practiceStartDate)
    }

    @Test
    fun `save should propagate ConflictException when NPI already exists`() {
        whenever(doctorService.save(anyOrNull()))
            .thenThrow(ConflictException("Doctor with NPI NPI001 already exists"))

        assertThrows(ConflictException::class.java) {
            doctorController.save(validRequestBody())
        }
    }

    @Test
    fun `getById should return 200 with doctor when found`() {
        whenever(doctorService.getById(1)).thenReturn(getDoctor())

        val response = doctorController.getById(1)

        assertEquals(HttpStatus.OK, response.status)
        assertEquals(1, response.body()?.id)
        assertEquals("Albert", response.body()?.firstName)
    }

    @Test
    fun `getById should propagate NotFoundException when doctor not found`() {
        whenever(doctorService.getById(99))
            .thenThrow(NotFoundException("Doctor not found with id: 99"))

        assertThrows(NotFoundException::class.java) {
            doctorController.getById(99)
        }
    }

    @Test
    fun `getAll should return 200 with list of doctors`() {
        whenever(doctorService.getAll())
            .thenReturn(listOf(getDoctor(), getDoctor().copy(id = 2, npiNumber = "NPI002")))

        val response = doctorController.getAll()

        assertEquals(HttpStatus.OK, response.status)
        assertEquals(2, response.body()?.size)
    }

    @Test
    fun `getAll should return 200 with empty list when no doctors exist`() {
        whenever(doctorService.getAll()).thenReturn(emptyList())

        val response = doctorController.getAll()

        assertEquals(HttpStatus.OK, response.status)
        assertEquals(0, response.body()?.size)
    }
}