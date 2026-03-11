package com.linxhealth.common

import com.linxhealth.common.Constants.CARDIOLOGY_JUNIOR
import com.linxhealth.common.Constants.CARDIOLOGY_MID
import com.linxhealth.common.Constants.CARDIOLOGY_SENIOR
import com.linxhealth.common.Constants.GENERAL_JUNIOR
import com.linxhealth.common.Constants.GENERAL_MID
import com.linxhealth.common.Constants.GENERAL_SENIOR
import com.linxhealth.common.Constants.ORTHOPEDICS_JUNIOR
import com.linxhealth.common.Constants.ORTHOPEDICS_MID
import com.linxhealth.common.Constants.ORTHOPEDICS_SENIOR
import com.linxhealth.model.Speciality
import jakarta.inject.Singleton

@Singleton
class ConsultationFeeResolver: FeeResolver<ConsultationFeeKey> {
    private data class FeeEntry(val speciality: Speciality, val tier: ExperienceTier)

    private val fees = mapOf(
        FeeEntry(Speciality.GENERAL,     ExperienceTier.JUNIOR) to GENERAL_JUNIOR,
        FeeEntry(Speciality.GENERAL,     ExperienceTier.MID)    to GENERAL_MID,
        FeeEntry(Speciality.GENERAL,     ExperienceTier.SENIOR) to GENERAL_SENIOR,

        FeeEntry(Speciality.ORTHOPEDICS, ExperienceTier.JUNIOR) to ORTHOPEDICS_JUNIOR,
        FeeEntry(Speciality.ORTHOPEDICS, ExperienceTier.MID)    to ORTHOPEDICS_MID,
        FeeEntry(Speciality.ORTHOPEDICS, ExperienceTier.SENIOR) to ORTHOPEDICS_SENIOR,

        FeeEntry(Speciality.CARDIOLOGY,  ExperienceTier.JUNIOR) to CARDIOLOGY_JUNIOR,
        FeeEntry(Speciality.CARDIOLOGY,  ExperienceTier.MID)    to CARDIOLOGY_MID,
        FeeEntry(Speciality.CARDIOLOGY,  ExperienceTier.SENIOR) to CARDIOLOGY_SENIOR,
    )

    override fun resolveFee(key: ConsultationFeeKey): Double {
        val tier = ExperienceTier.from(key.yearsOfExperience)
        return fees[FeeEntry(key.speciality, tier)]
        ?: throw IllegalArgumentException("No fee for ${key.speciality} found")
    }
}