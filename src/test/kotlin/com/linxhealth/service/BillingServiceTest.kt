package com.linxhealth.service

import com.linxhealth.common.ConsultationFeeKey
import com.linxhealth.common.FeeResolver
import com.linxhealth.exception.NotFoundException
import com.linxhealth.exception.ValidationException
import com.linxhealth.model.Appointment
import com.linxhealth.model.AppointmentStatus
import com.linxhealth.model.Doctor
import com.linxhealth.model.Insurance
import com.linxhealth.model.Patient
import com.linxhealth.model.Speciality
import com.linxhealth.repository.AppointmentRepository
import com.linxhealth.repository.DoctorRepository
import com.linxhealth.repository.PatientRepository
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.mockito.Mockito.mock
import org.mockito.kotlin.anyOrNull
import org.mockito.kotlin.whenever
import java.time.LocalDate

class BillingServiceTest {
    private lateinit var appointmentRepository: AppointmentRepository
    private lateinit var patientRepository: PatientRepository
    private lateinit var doctorRepository: DoctorRepository
    private lateinit var feeResolver: FeeResolver<ConsultationFeeKey>
    private lateinit var billingService: BillingService

    private fun doctor() = Doctor(
        id = 1, firstName = "Sheldon", lastName = "Cooper",
        npiNumber = "NPI001", speciality = Speciality.CARDIOLOGY,
        practiceStartDate = LocalDate.now().minusYears(25)
    )

    private fun patient() = Patient(
        id = 1, firstName = "Howard", lastName = "Wolowitz",
        dateOfBirth = LocalDate.of(1990, 1, 15), age = 34,
        insurance = Insurance(121, "PCN001", "MEM001")
    )

    private fun appointment(
        id: Int = 1,
        status: AppointmentStatus = AppointmentStatus.COMPLETED
    ) = Appointment(id = id, patientId = 1, doctorId = 1, appointmentStatus = status)

    private fun setupCompletedAppointment(baseFee: Double = 2000.0) {
        whenever(appointmentRepository.findById(1)).thenReturn(appointment())
        whenever(patientRepository.findById(1)).thenReturn(patient())
        whenever(doctorRepository.findById(1)).thenReturn(doctor())
        whenever(appointmentRepository.findByPatientId(1)).thenReturn(emptyList())
        whenever(feeResolver.resolveFee(anyOrNull())).thenReturn(baseFee)
    }

    @BeforeEach
    fun setup() {
        appointmentRepository = mock()
        patientRepository = mock()
        doctorRepository = mock()
        feeResolver = mock()
        billingService = BillingService(appointmentRepository, patientRepository, doctorRepository, feeResolver)
    }

    @Test
    fun `getBill should throw NotFoundException when appointment does not exist`() {
        whenever(appointmentRepository.findById(99)).thenReturn(null)

        assertThrows<NotFoundException> { billingService.getBill(99) }
    }

    @Test
    fun `getBill should throw ValidationException when appointment is SCHEDULED`() {
        whenever(appointmentRepository.findById(1))
            .thenReturn(appointment(status = AppointmentStatus.SCHEDULED))

        assertThrows<ValidationException> { billingService.getBill(1) }
    }

    @Test
    fun `getBill should throw ValidationException when appointment is CANCELLED`() {
        whenever(appointmentRepository.findById(1))
            .thenReturn(appointment(status = AppointmentStatus.CANCELLED))

        assertThrows<ValidationException> { billingService.getBill(1) }
    }

    @Test
    fun `getBill should throw NotFoundException when patient does not exist`() {
        whenever(appointmentRepository.findById(1)).thenReturn(appointment())
        whenever(patientRepository.findById(1)).thenReturn(null)

        assertThrows<NotFoundException> { billingService.getBill(1) }
    }

    @Test
    fun `getBill should throw NotFoundException when doctor does not exist`() {
        whenever(appointmentRepository.findById(1)).thenReturn(appointment())
        whenever(patientRepository.findById(1)).thenReturn(patient())
        whenever(doctorRepository.findById(1)).thenReturn(null)

        assertThrows<NotFoundException> { billingService.getBill(1) }
    }

    @Test
    fun `getBill should return correct base fee from FeeTable`() {
        setupCompletedAppointment()

        val bill = billingService.getBill(1)

        assertEquals(2000.0, bill.fee)
    }

    @Test
    fun `getBill should apply 0 percent discount for first appointment`() {
        setupCompletedAppointment()

        val bill = billingService.getBill(1)

        assertEquals(0.0, bill.discountPercentage)
        assertEquals(2000.0, bill.amountAfterDiscount)
    }

    @Test
    fun `getBill should apply discount based on prior completed appointments`() {
        whenever(appointmentRepository.findById(1)).thenReturn(appointment(id = 1))
        whenever(patientRepository.findById(1)).thenReturn(patient())
        whenever(doctorRepository.findById(1)).thenReturn(doctor())
        whenever(appointmentRepository.findByPatientId(1)).thenReturn(
            listOf(
                appointment(id = 2, status = AppointmentStatus.COMPLETED),
                appointment(id = 3, status = AppointmentStatus.COMPLETED),
                appointment(id = 4, status = AppointmentStatus.COMPLETED)
            )
        )

        val bill = billingService.getBill(1)

        assertEquals(3.0, bill.discountPercentage)
    }

    @Test
    fun `getBill should not count current appointment as prior visit`() {
        val current = appointment(id = 1, status = AppointmentStatus.COMPLETED)
        whenever(appointmentRepository.findById(1)).thenReturn(current)
        whenever(patientRepository.findById(1)).thenReturn(patient())
        whenever(doctorRepository.findById(1)).thenReturn(doctor())
        whenever(appointmentRepository.findByPatientId(1)).thenReturn(listOf(current))

        val bill = billingService.getBill(1)

        assertEquals(1.0, bill.discountPercentage)
    }

    @Test
    fun `getBill should only count COMPLETE appointments for discount`() {
        whenever(appointmentRepository.findById(1)).thenReturn(appointment(id = 1))
        whenever(patientRepository.findById(1)).thenReturn(patient())
        whenever(doctorRepository.findById(1)).thenReturn(doctor())
        whenever(appointmentRepository.findByPatientId(1)).thenReturn(
            listOf(
                appointment(id = 2, status = AppointmentStatus.COMPLETED),
                appointment(id = 3, status = AppointmentStatus.COMPLETED),
                appointment(id = 4, status = AppointmentStatus.CANCELLED),
                appointment(id = 5, status = AppointmentStatus.SCHEDULED)
            )
        )

        val bill = billingService.getBill(1)

        assertEquals(2.0, bill.discountPercentage)
    }

    @Test
    fun `getBill should cap discount at 10 percent`() {
        whenever(appointmentRepository.findById(1)).thenReturn(appointment(id = 1))
        whenever(patientRepository.findById(1)).thenReturn(patient())
        whenever(doctorRepository.findById(1)).thenReturn(doctor())
        whenever(appointmentRepository.findByPatientId(1)).thenReturn(
            (2..15).map { appointment(id = it, status = AppointmentStatus.COMPLETED) }
        )

        val bill = billingService.getBill(1)

        assertEquals(10.0, bill.discountPercentage)
    }

    @Test
    fun `getBill should apply TAX and split insurance and co-pay correctly`() {
        setupCompletedAppointment()

        val bill = billingService.getBill(1)

        assertEquals(240.0, bill.taxAmount)
        assertEquals(2240.0, bill.afterTaxAndDiscount)
        assertEquals(2016.0, bill.amountCoveredByInsurance)
        assertEquals(224.0, bill.coPayAmount)
    }

    @Test
    fun `getBill should use feeResolver to get base fee`() {
        setupCompletedAppointment(baseFee = 2000.0)

        val bill = billingService.getBill(1)

        assertEquals(2000.0, bill.fee)
    }

    @Test
    fun `getBill should use feeResolver result regardless of specialty`() {
        setupCompletedAppointment(baseFee = 1500.0)

        val bill = billingService.getBill(1)

        assertEquals(1500.0, bill.fee)
    }
}