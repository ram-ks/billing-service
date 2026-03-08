package com.linxhealth.controller

import com.linxhealth.controller.dto.PatientRequest
import com.linxhealth.controller.dto.PatientResponse
import com.linxhealth.controller.mapper.toModel
import com.linxhealth.controller.mapper.toResponse
import com.linxhealth.service.PatientService
import io.micronaut.http.HttpResponse
import io.micronaut.http.annotation.*

@Controller("/patients")
class PatientController(private val patientService: PatientService) {
    @Post
    fun save(@Body patientRequest: PatientRequest): HttpResponse<PatientResponse> {
        return HttpResponse.created(patientService.save(patientRequest.toModel()).toResponse())
    }

    @Get("/{id}")
    fun getById(@PathVariable id: Int): HttpResponse<PatientResponse> {
        return HttpResponse.ok(patientService.getById(id).toResponse())
    }

    @Get
    fun getAll(): HttpResponse<List<PatientResponse>> {
        return HttpResponse.ok(patientService.getAll().map { it.toResponse() })
    }
}