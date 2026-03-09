package com.linxhealth.controller

import com.linxhealth.controller.dto.DoctorRequest
import com.linxhealth.controller.dto.DoctorResponse
import com.linxhealth.controller.mapper.toModel
import com.linxhealth.controller.mapper.toResponse
import com.linxhealth.service.DoctorService
import io.micronaut.http.HttpResponse
import io.micronaut.http.annotation.Body
import io.micronaut.http.annotation.Controller
import io.micronaut.http.annotation.Get
import io.micronaut.http.annotation.PathVariable
import io.micronaut.http.annotation.Post

@Controller("/doctors")
class DoctorController(private val doctorService: DoctorService) {
    @Post
    fun save(@Body doctorRequest: DoctorRequest): HttpResponse<DoctorResponse> {
        return HttpResponse.created(doctorService.save(doctorRequest.toModel()).toResponse())
    }

    @Get("/{id}")
    fun getById(@PathVariable id: Int): HttpResponse<DoctorResponse> {
        return HttpResponse.ok(doctorService.getById(id).toResponse())
    }

    @Get
    fun getAll(): HttpResponse<List<DoctorResponse>> {
        return HttpResponse.ok(doctorService.getAll().map { it.toResponse() })
    }
}