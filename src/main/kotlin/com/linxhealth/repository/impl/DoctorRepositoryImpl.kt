package com.linxhealth.repository.impl

import com.linxhealth.model.Doctor
import com.linxhealth.repository.DoctorRepository
import jakarta.inject.Singleton
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.atomic.AtomicInteger

@Singleton
class DoctorRepositoryImpl: DoctorRepository {
    private val store: ConcurrentHashMap<Int, Doctor> = ConcurrentHashMap()
    private val idCounter: AtomicInteger = AtomicInteger(0)

    override fun save(doctor: Doctor): Doctor {
        val id = doctor.id ?: idCounter.incrementAndGet()
        val savedDoctor = doctor.copy(id = id)
        store[id] = savedDoctor
        return savedDoctor
    }

    override fun findByNpiNumber(npiNumber: String): Doctor? {
        return store.values.find { it.npiNumber == npiNumber }
    }

    override fun findAll(): List<Doctor> {
        return store.values.toList()
    }

    override fun findById(doctorId: Int): Doctor? {
        return store[doctorId]
    }
}