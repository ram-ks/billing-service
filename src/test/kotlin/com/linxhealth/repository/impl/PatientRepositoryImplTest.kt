package com.linxhealth.repository.impl

import com.linxhealth.model.Insurance
import com.linxhealth.model.Patient
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertNotNull
import java.time.LocalDate
import kotlin.test.assertEquals
import kotlin.test.assertNull

class PatientRepositoryImplTest {
    private lateinit var patientRepositoryImpl: PatientRepositoryImpl

    @BeforeEach
    fun setUp() {
        patientRepositoryImpl = PatientRepositoryImpl()
    }

    private fun getPatient(firstName: String = "Ram") = Patient(
        id = null,
        firstName = firstName,
        lastName = "S",
        dateOfBirth = LocalDate.of(1992, 4, 10),
        age = 34,
        insurance = Insurance(121, "PCN001", "MEM001")
    )

    @Test
    fun `save should persist and return patient`() {
        val patient = getPatient()

        val result = patientRepositoryImpl.save(patient)
        assertEquals(1, result.id)
    }

    @Test
    fun `findById should return patient after saving`() {
        val patient = getPatient()
        val savedPatient = patientRepositoryImpl.save(patient)

        val result = patientRepositoryImpl.findById(savedPatient.id!!)

        assertNotNull(result)
        assertEquals(expected = 1, actual = result.id)
        assertEquals("Ram", actual = result.firstName)
    }

    @Test
    fun `findById should return null when patient does not exist`() {
        val result = patientRepositoryImpl.findById(1212)

        assertNull(result)
    }

    @Test
    fun `findAll should return all saved patients`() {
        patientRepositoryImpl.save(getPatient("Sesko"))
        patientRepositoryImpl.save(getPatient("Zlatan"))

        val all = patientRepositoryImpl.findAll()

        assertEquals(2, all.size)
    }

    @Test
    fun `findAll should return empty list when no patients saved`() {
        val all = patientRepositoryImpl.findAll()

        assertTrue(all.isEmpty())
    }

    @Test
    fun `save should overwrite existing patient with same id`() {
        val patient = getPatient()
        val savedPatient = patientRepositoryImpl.save(patient)

        val updated = savedPatient.copy(firstName = "Jane")
        patientRepositoryImpl.save(updated)

        val found = patientRepositoryImpl.findById(savedPatient.id!!)
        assertEquals("Jane", found?.firstName)
        assertEquals(1, patientRepositoryImpl.findAll().size)
    }

}