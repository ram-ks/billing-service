package com.linxhealth.controller.mapper

import com.linxhealth.common.Constants.DOB_FORMATTER
import com.linxhealth.controller.dto.DoctorRequest
import com.linxhealth.controller.dto.DoctorResponse
import com.linxhealth.model.Doctor
import java.time.LocalDate

fun DoctorRequest.toModel(): Doctor =
    Doctor(
        firstName = firstName,
        lastName = lastName,
        npiNumber = npiNumber,
        speciality = speciality,
        practiceStartDate = LocalDate.parse(practiceStartDate, DOB_FORMATTER),
    )

fun Doctor.toResponse(): DoctorResponse =
    DoctorResponse(
        id = id!!,
        firstName = firstName,
        lastName = lastName,
        npiNumber = npiNumber,
        speciality = speciality,
        practiceStartDate = practiceStartDate.format(DOB_FORMATTER)
    )