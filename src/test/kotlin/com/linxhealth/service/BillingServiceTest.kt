package com.linxhealth.service

import com.linxhealth.common.BillCalculator
import com.linxhealth.common.ConsultationFeeKey
import com.linxhealth.common.FeeResolver
import com.linxhealth.exception.NotFoundException
import com.linxhealth.exception.ValidationException
import com.linxhealth.model.Appointment
import com.linxhealth.model.AppointmentStatus
import com.linxhealth.model.Bill
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
import org.mockito.Mockito.verify
import org.mockito.kotlin.any
import org.mockito.kotlin.anyOrNull
import org.mockito.kotlin.eq
import org.mockito.kotlin.whenever
import java.time.LocalDate

class BillingServiceTest {
    private lateinit var appointmentRepository: AppointmentRepository
    private lateinit var patientRepository: PatientRepository
    private lateinit var doctorRepository: DoctorRepository
    private lateinit var feeResolver: FeeResolver<ConsultationFeeKey>
    private lateinit var billCalculator: BillCalculator

    private lateinit var billingService: BillingService

    private fun stubDoctor() = Doctor(
        id = 1, firstName = "Sheldon", lastName = "Cooper",
        npiNumber = "NPI001", speciality = Speciality.CARDIOLOGY,
        practiceStartDate = LocalDate.now().minusYears(25)
    )

    private fun stubPatient() = Patient(
        id = 1, firstName = "Howard", lastName = "Wolowitz",
        dateOfBirth = LocalDate.of(1990, 1, 15), age = 34,
        insurance = Insurance(121, "PCN001", "MEM001")
    )

    private fun stubAppointment(
        id: Int = 1,
        status: AppointmentStatus = AppointmentStatus.COMPLETED
    ) = Appointment(id = id, patientId = 1, doctorId = 1, appointmentStatus = status)

    private fun stubBill(
        fee: Double = 2000.0,
        discountPercentage: Double = 0.0,
        discountAmount: Double = 0.0,
        amountAfterDiscount: Double = 2000.0,
        taxAmount: Double = 240.0,
        afterTaxAndDiscount: Double = 2240.0,
        amountCoveredByInsurance: Double = 2016.0,
        coPayAmount: Double = 224.0
    ) = Bill(
        fee = fee,
        discountPercentage = discountPercentage,
        discountAmount = discountAmount,
        amountAfterDiscount = amountAfterDiscount,
        taxAmount = taxAmount,
        afterTaxAndDiscount = afterTaxAndDiscount,
        amountCoveredByInsurance = amountCoveredByInsurance,
        coPayAmount = coPayAmount
    )

    private fun setupCompletedAppointment(baseFee: Double = 2000.0, priorAppointments: List<Appointment> = emptyList()) {
        whenever(appointmentRepository.findById(1)).thenReturn(stubAppointment())
        whenever(patientRepository.findById(1)).thenReturn(stubPatient())
        whenever(doctorRepository.findById(1)).thenReturn(stubDoctor())
        whenever(appointmentRepository.findByPatientId(1)).thenReturn(priorAppointments)
        whenever(feeResolver.resolveFee(anyOrNull())).thenReturn(baseFee)
        whenever(billCalculator.calculate(any(), any())).thenReturn(stubBill(fee = baseFee))
    }

    @BeforeEach
    fun setup() {
        appointmentRepository = mock()
        patientRepository = mock()
        doctorRepository = mock()
        feeResolver = mock()
        billCalculator = mock()
        billingService = BillingService(appointmentRepository, patientRepository, doctorRepository, feeResolver, billCalculator)
    }

    @Test
    fun `getBill should throw NotFoundException when appointment does not exist`() {
        whenever(appointmentRepository.findById(99)).thenReturn(null)

        assertThrows<NotFoundException> { billingService.getBill(99) }
    }

    @Test
    fun `getBill should throw ValidationException when appointment is SCHEDULED`() {
        whenever(appointmentRepository.findById(1))
            .thenReturn(stubAppointment(status = AppointmentStatus.SCHEDULED))

        assertThrows<ValidationException> { billingService.getBill(1) }
    }

    @Test
    fun `getBill should throw ValidationException when appointment is CANCELLED`() {
        whenever(appointmentRepository.findById(1))
            .thenReturn(stubAppointment(status = AppointmentStatus.CANCELLED))

        assertThrows<ValidationException> { billingService.getBill(1) }
    }

    @Test
    fun `getBill should throw NotFoundException when patient does not exist`() {
        whenever(appointmentRepository.findById(1)).thenReturn(stubAppointment())
        whenever(patientRepository.findById(1)).thenReturn(null)

        assertThrows<NotFoundException> { billingService.getBill(1) }
    }

    @Test
    fun `getBill should throw NotFoundException when doctor does not exist`() {
        whenever(appointmentRepository.findById(1)).thenReturn(stubAppointment())
        whenever(patientRepository.findById(1)).thenReturn(stubPatient())
        whenever(doctorRepository.findById(1)).thenReturn(null)

        assertThrows<NotFoundException> { billingService.getBill(1) }
    }

    @Test
    fun `getBill should pass fee`() {
        setupCompletedAppointment()
        billingService.getBill(1)

        verify(billCalculator).calculate(eq(2000.0), any())
    }

    @Test
    fun `getBill should apply 0 percent discount for first appointment`() {
        setupCompletedAppointment()

        val bill = billingService.getBill(1)

        assertEquals(0.0, bill.discountPercentage)
        assertEquals(2000.0, bill.amountAfterDiscount)
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

    @Test
    fun `getBill should not count current appointment as prior visit`() {
        setupCompletedAppointment(
            priorAppointments = listOf(stubAppointment(id = 1)) // current appointment only
        )

        billingService.getBill(1)

        verify(billCalculator).calculate(any(), eq(0))
    }


    @Test
    fun `getBill should only count COMPLETE appointments for prior visits`() {
        setupCompletedAppointment(
            priorAppointments = listOf(
                stubAppointment(id = 2, status = AppointmentStatus.COMPLETED),
                stubAppointment(id = 3, status = AppointmentStatus.COMPLETED),
                stubAppointment(id = 4, status = AppointmentStatus.CANCELLED),
                stubAppointment(id = 5, status = AppointmentStatus.SCHEDULED)
            )
        )

        billingService.getBill(1)

        verify(billCalculator).calculate(any(), eq(2))
    }

    @Test
    fun `getBill should return bill produced by BillCalculator`() {
        setupCompletedAppointment()
        val expected = stubBill()
        whenever(billCalculator.calculate(any(), any())).thenReturn(expected)

        val result = billingService.getBill(1)

        assertEquals(expected, result)
    }
}