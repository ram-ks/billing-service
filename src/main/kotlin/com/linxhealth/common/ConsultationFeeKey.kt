package com.linxhealth.common

import com.linxhealth.model.Speciality

data class ConsultationFeeKey(
    val speciality: Speciality,
    val yearsOfExperience: Int,
)
