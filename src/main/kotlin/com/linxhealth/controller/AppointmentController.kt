package com.linxhealth.controller

import com.linxhealth.controller.dto.AppointmentRequest
import com.linxhealth.controller.dto.AppointmentResponse
import com.linxhealth.controller.dto.BillResponse
import com.linxhealth.controller.dto.UpdateStatusRequest
import com.linxhealth.controller.mapper.toResponse
import com.linxhealth.service.AppointmentService
import com.linxhealth.service.BillingService
import io.micronaut.http.HttpResponse
import io.micronaut.http.annotation.Body
import io.micronaut.http.annotation.Controller
import io.micronaut.http.annotation.Delete
import io.micronaut.http.annotation.Get
import io.micronaut.http.annotation.Patch
import io.micronaut.http.annotation.PathVariable
import io.micronaut.http.annotation.Post

@Controller("/appointments")
class AppointmentController(
    private val appointmentService: AppointmentService,
    private val billingService: BillingService
) {
    @Post
    fun book(@Body request: AppointmentRequest): HttpResponse<AppointmentResponse> =
        HttpResponse.created(
            appointmentService.book(request.patientId, request.doctorId).toResponse()
        )

    @Patch("/{id}/status")
    fun updateStatus(
        @PathVariable id: Int,
        @Body request: UpdateStatusRequest
    ): HttpResponse<AppointmentResponse> =
        HttpResponse.ok(
            appointmentService.updateStatus(id, request.status).toResponse()
        )

    @Get("/{id}")
    fun getById(@PathVariable id: Int): HttpResponse<AppointmentResponse> =
        HttpResponse.ok(appointmentService.getByID(id).toResponse())

    @Get
    fun getAll(): HttpResponse<List<AppointmentResponse>> =
        HttpResponse.ok(appointmentService.getAll().map { it.toResponse() })

    @Delete("/{id}")
    fun delete(@PathVariable id: Int): HttpResponse<Unit> {
        appointmentService.delete(id)
        return HttpResponse.noContent()
    }

    @Get("/{id}/bill")
    fun getBill(@PathVariable id: Int): HttpResponse<BillResponse> =
        HttpResponse.ok(billingService.getBill(id).toResponse())
}