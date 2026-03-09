package com.linxhealth.controller.mapper

import com.linxhealth.controller.dto.DoctorRequest
import com.linxhealth.controller.dto.DoctorResponse
import com.linxhealth.model.Doctor
import java.time.LocalDate
import java.time.format.DateTimeFormatter

private val DATE_FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy")

fun DoctorRequest.toModel(): Doctor =
    Doctor(
        firstName = firstName,
        lastName = lastName,
        npiNumber = npiNumber,
        speciality = speciality,
        practiceStartDate = LocalDate.parse(practiceStartDate, DATE_FORMATTER)
    )

fun Doctor.toResponse(): DoctorResponse =
    DoctorResponse(
        id = id!!,
        firstName = firstName,
        lastName = lastName,
        npiNumber = npiNumber,
        speciality = speciality,
        practiceStartDate = practiceStartDate.format(DATE_FORMATTER)
    )