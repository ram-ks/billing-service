package com.linxhealth.repository

import com.linxhealth.model.Patient

interface PatientRepository {
    fun save(patient: Patient): Patient
    fun findById(id: Int): Patient?
    fun findAll(): List<Patient>
    //TODO: add support for update and delete
}