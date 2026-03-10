package com.linxhealth.repository.impl

import com.linxhealth.model.Patient
import com.linxhealth.repository.PatientRepository
import jakarta.inject.Singleton

@Singleton
class PatientRepositoryImpl: PatientRepository {
    private val patientStore = mutableMapOf<Int, Patient>()
    private var idCounter = 1

    override fun save(patient: Patient): Patient {
        val id = patient.id ?: idCounter++
        val newPatient = patient.copy(id = id)
        patientStore[id] = newPatient
        return newPatient
    }

    override fun findById(patientId: Int): Patient? =
        patientStore[patientId]

    override fun findAll(): List<Patient> =
        patientStore.values.toList()
}