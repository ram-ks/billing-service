package com.linxhealth.controller

import com.linxhealth.controller.dto.PatientRequest
import com.linxhealth.controller.dto.PatientResponse
import com.linxhealth.controller.mapper.toModel
import com.linxhealth.controller.mapper.toResponse
import com.linxhealth.model.Patient
import io.micronaut.http.annotation.Body
import io.micronaut.http.annotation.Controller
import io.micronaut.http.annotation.Post
import io.micronaut.http.HttpResponse

@Controller("/patients")
class PatientController {
    @Post
    fun save(@Body patientRequest: PatientRequest): HttpResponse<PatientResponse> {
        val patient = patientRequest.toModel()
        return HttpResponse.created(patient.toResponse())
    }
}