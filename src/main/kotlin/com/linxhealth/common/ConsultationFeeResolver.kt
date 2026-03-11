package com.linxhealth.common

import com.linxhealth.model.Speciality
import jakarta.inject.Singleton

@Singleton
class ConsultationFeeResolver: FeeResolver<ConsultationFeeKey> {
    private data class FeeEntry(val speciality: Speciality, val tier: ExperienceTier)

    private val fees = mapOf(
        FeeEntry(Speciality.GENERAL,     ExperienceTier.JUNIOR) to 500.0,
        FeeEntry(Speciality.GENERAL,     ExperienceTier.MID)    to 800.0,
        FeeEntry(Speciality.GENERAL,     ExperienceTier.SENIOR) to 1200.0,

        FeeEntry(Speciality.ORTHOPEDICS, ExperienceTier.JUNIOR) to 800.0,
        FeeEntry(Speciality.ORTHOPEDICS, ExperienceTier.MID)    to 1000.0,
        FeeEntry(Speciality.ORTHOPEDICS, ExperienceTier.SENIOR) to 1500.0,

        FeeEntry(Speciality.CARDIOLOGY,  ExperienceTier.JUNIOR) to 1000.0,
        FeeEntry(Speciality.CARDIOLOGY,  ExperienceTier.MID)    to 1500.0,
        FeeEntry(Speciality.CARDIOLOGY,  ExperienceTier.SENIOR) to 2000.0,
    )

    override fun resolveFee(key: ConsultationFeeKey): Double {
        val tier = ExperienceTier.from(key.yearsOfExperience)
        return fees[FeeEntry(key.speciality, tier)]
        ?: throw IllegalArgumentException("No fee for ${key.speciality} found")
    }
}