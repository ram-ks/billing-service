package com.linxhealth.service

import com.linxhealth.exception.ConflictException
import com.linxhealth.exception.NotFoundException
import com.linxhealth.model.Doctor
import com.linxhealth.repository.DoctorRepository
import jakarta.inject.Singleton

@Singleton
class DoctorService(private val doctorRepository: DoctorRepository) {
    fun save(doctor: Doctor): Doctor {
        if (doctorRepository.findByNpiNumber(doctor.npiNumber) != null) {
            throw ConflictException("Doctor already exists")
        }
        return doctorRepository.save(doctor)
    }

    fun getAll(): List<Doctor> {
        return doctorRepository.findAll()
    }

    fun getById(id: Int): Doctor {
        return doctorRepository.findById(id) ?: throw NotFoundException("Unable to find doctor by id: $id")
    }
}