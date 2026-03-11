package com.linxhealth.repository

import com.linxhealth.model.Patient

interface PatientRepository {
    fun save(patient: Patient): Patient
    fun findById(patientId: Int): Patient?
    fun findAll(): List<Patient>
}