package com.linxhealth.repository

import com.linxhealth.model.Doctor

interface DoctorRepository {
    fun save(doctor: Doctor): Doctor
    fun findById(doctorId: Int): Doctor?
    fun findByNpiNumber(npiNumber: String): Doctor?
    fun findAll(): List<Doctor>
}