package com.linxhealth.controller

import com.linxhealth.controller.dto.PatientRequest
import com.linxhealth.controller.dto.PatientResponse
import com.linxhealth.controller.mapper.toModel
import com.linxhealth.controller.mapper.toResponse
import com.linxhealth.service.PatientService
import io.micronaut.http.HttpResponse
import io.micronaut.http.annotation.*
//import io.swagger.v3.oas.annotations.Operation

@Controller("/patients")
class PatientController(private val patientService: PatientService) {
    @Post
//    @Operation(summary = "Register a patient")
    fun save(@Body patientRequest: PatientRequest): HttpResponse<PatientResponse> {
        return HttpResponse.created(patientService.save(patientRequest.toModel()).toResponse())
    }

//    @Operation(summary = "Get patient by patientId")
    @Get("/{id}")
    fun getById(@PathVariable id: Int): HttpResponse<PatientResponse> {
        return HttpResponse.ok(patientService.getById(id).toResponse())
    }

//    @Operation(summary = "Get all patients")
    @Get
    fun getAll(): HttpResponse<List<PatientResponse>> {
        return HttpResponse.ok(patientService.getAll().map { it.toResponse() })
    }
}