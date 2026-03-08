package com.linxhealth.controller.mapper

import com.linxhealth.controller.dto.InsuranceRequest
import com.linxhealth.controller.dto.InsuranceResponse
import com.linxhealth.controller.dto.PatientRequest
import com.linxhealth.controller.dto.PatientResponse
import com.linxhealth.model.Insurance
import com.linxhealth.model.Patient
import java.time.LocalDate
import java.time.format.DateTimeFormatter

private val DOB_FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy")

fun PatientRequest.toModel(): Patient {
    return Patient(
        firstName = firstName,
        lastName = lastName,
        dateOfBirth = LocalDate.parse(dob, DOB_FORMATTER),
        age = age,
        insurance = insurance.toModel(),
    )
}

fun Patient.toResponse(): PatientResponse {
    return PatientResponse(
        id = "2323",
        firstName = firstName,
        lastName = lastName,
        dob = dateOfBirth.format(DOB_FORMATTER),
        age = age,
        insurance = insurance.toResponse(),
    )
}

fun InsuranceRequest.toModel(): Insurance {
    return Insurance(
        binNumber = binNumber,
        pcnNumber = pcnNumber,
        memberId = memberId
    )
}

fun Insurance.toResponse(): InsuranceResponse {
    return InsuranceResponse(
        binNumber = binNumber,
        pcnNumber = pcnNumber,
        memberId = memberId
    )
}
