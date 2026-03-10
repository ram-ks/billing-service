package com.linxhealth.controller

import com.linxhealth.controller.dto.AppointmentRequest
import com.linxhealth.controller.dto.UpdateStatusRequest
import com.linxhealth.exception.NotFoundException
import com.linxhealth.exception.ValidationException
import com.linxhealth.model.Appointment
import com.linxhealth.model.AppointmentStatus
import com.linxhealth.service.AppointmentService
import io.micronaut.http.HttpStatus
import io.micronaut.test.annotation.MockBean
import io.micronaut.test.extensions.junit5.annotation.MicronautTest
import jakarta.inject.Inject
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.mockito.Mockito.mock
import org.mockito.kotlin.whenever

@MicronautTest
class AppointmentControllerTest {
    @Inject
    lateinit var appointmentController: AppointmentController

    @Inject
    lateinit var appointmentService: AppointmentService

    @MockBean(AppointmentService::class)
    fun appointmentService(): AppointmentService = mock()

    private fun bookRequest() = AppointmentRequest(patientId = 1, doctorId = 1)

    private fun scheduledAppointment() = Appointment(
        id = 1, patientId = 1, doctorId = 1,
        appointmentStatus = AppointmentStatus.SCHEDULED
    )

    private fun completedAppointment() = Appointment(
        id = 1, patientId = 1, doctorId = 1,
        appointmentStatus = AppointmentStatus.COMPLETED,
    )

    @Test
    fun `book should return 201 with SCHEDULED appointment`() {
        whenever(appointmentService.book(1, 1)).thenReturn(scheduledAppointment())

        val response = appointmentController.book(bookRequest())

        assertEquals(HttpStatus.CREATED, response.status)
        assertEquals(1, response.body()?.id)
        assertEquals(AppointmentStatus.SCHEDULED, response.body()?.status)
    }

    @Test
    fun `book should propagate NotFoundException when patient does not exist`() {
        whenever(appointmentService.book(99, 1))
            .thenThrow(NotFoundException("Patient not found with id: 99"))

        assertThrows<NotFoundException> {
            appointmentController.book(AppointmentRequest(patientId = 99, doctorId = 1))
        }
    }

    @Test
    fun `book should propagate NotFoundException when doctor does not exist`() {
        whenever(appointmentService.book(1, 99))
            .thenThrow(NotFoundException("Doctor not found with id: 99"))

        assertThrows<NotFoundException> {
            appointmentController.book(AppointmentRequest(patientId = 1, doctorId = 99))
        }
    }

    @Test
    fun `updateStatus should return 200 with COMPLETED status`() {
        whenever(appointmentService.updateStatus(1, AppointmentStatus.COMPLETED))
            .thenReturn(completedAppointment())

        val response = appointmentController.updateStatus(1, UpdateStatusRequest(AppointmentStatus.COMPLETED))

        assertEquals(HttpStatus.OK, response.status)
        assertEquals(AppointmentStatus.COMPLETED, response.body()?.status)
    }

    @Test
    fun `updateStatus should return 200 with CANCELLED`() {
        val cancelled = scheduledAppointment().copy(appointmentStatus = AppointmentStatus.CANCELLED)
        whenever(appointmentService.updateStatus(1, AppointmentStatus.CANCELLED))
            .thenReturn(cancelled)

        val response = appointmentController.updateStatus(1, UpdateStatusRequest(AppointmentStatus.CANCELLED))

        assertEquals(HttpStatus.OK, response.status)
    }

    @Test
    fun `updateStatus should propagate NotFoundException when appointment does not exist`() {
        whenever(appointmentService.updateStatus(99, AppointmentStatus.COMPLETED))
            .thenThrow(NotFoundException("Appointment not found with id: 99"))

        assertThrows<NotFoundException> {
            appointmentController.updateStatus(99, UpdateStatusRequest(AppointmentStatus.COMPLETED))
        }
    }

    @Test
    fun `updateStatus should propagate ValidationException for cancelled appointment`() {
        whenever(appointmentService.updateStatus(1, AppointmentStatus.COMPLETED))
            .thenThrow(ValidationException("Cannot update a cancelled appointment"))

        assertThrows<ValidationException> {
            appointmentController.updateStatus(1, UpdateStatusRequest(AppointmentStatus.COMPLETED))
        }
    }

    @Test
    fun `getById should return 200 with appointment`() {
        whenever(appointmentService.getByID(1)).thenReturn(scheduledAppointment())

        val response = appointmentController.getById(1)

        assertEquals(HttpStatus.OK, response.status)
        assertEquals(1, response.body()?.id)
    }

    @Test
    fun `getById should propagate NotFoundException when appointment does not exist`() {
        whenever(appointmentService.getByID(99))
            .thenThrow(NotFoundException("Appointment not found with id: 99"))

        assertThrows<NotFoundException> { appointmentController.getById(99) }
    }

    @Test
    fun `getAll should return 200 with list of appointments`() {
        whenever(appointmentService.getAll()).thenReturn(
            listOf(scheduledAppointment(), scheduledAppointment().copy(id = 2))
        )

        val response = appointmentController.getAll()

        assertEquals(HttpStatus.OK, response.status)
        assertEquals(2, response.body()?.size)
    }

    @Test
    fun `getAll should return 200 with empty list when no appointments exist`() {
        whenever(appointmentService.getAll()).thenReturn(emptyList())

        val response = appointmentController.getAll()

        assertEquals(HttpStatus.OK, response.status)
        assertEquals(0, response.body()?.size)
    }

    @Test
    fun `delete should return 204 no content when appointment exists`() {
        whenever(appointmentService.delete(1)).thenAnswer { }

        val response = appointmentController.delete(1)

        assertEquals(HttpStatus.NO_CONTENT, response.status)
    }

    @Test
    fun `delete should propagate NotFoundException when appointment does not exist`() {
        whenever(appointmentService.delete(99))
            .thenThrow(NotFoundException("Appointment not found with id: 99"))

        assertThrows<NotFoundException> { appointmentController.delete(99) }
    }
}
