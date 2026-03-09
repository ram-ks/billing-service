package com.linxhealth.service

import com.linxhealth.exception.NotFoundException
import com.linxhealth.exception.ValidationException
import com.linxhealth.model.Patient
import com.linxhealth.repository.PatientRepository
import jakarta.inject.Singleton

@Singleton
class PatientService(private val repository: PatientRepository) {
    fun save(patient: Patient): Patient {
        return try {
            repository.save(patient)
        } catch (e: IllegalArgumentException) {
            throw ValidationException(e.message ?: "Validation failed")
        }
    }

    fun getById(id: Int): Patient {
        return repository.findById(id) ?: throw NotFoundException("Patient not found with id: $id")
    }

    fun getAll(): List<Patient> =
        repository.findAll()
}