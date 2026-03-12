package com.linxhealth.common

import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

class BillCalculatorTest {
    private lateinit var billingCalculator: BillCalculator

    @BeforeEach
    fun setUp() {
        billingCalculator = BillCalculator()
    }

    @Test
    fun `new patient with 0 prior visits should get no discount`() {
        val bill = billingCalculator.calculate(fee = 1000.0, completedAppointments = 0)
        assertEquals(0.0,    bill.discountPercentage)
        assertEquals(1000.0, bill.amountAfterDiscount)
    }

    @Test
    fun `patient with 3 prior visits should get 3 percent discount`() {
        val bill = billingCalculator.calculate(fee = 1000.0, completedAppointments = 3)
        assertEquals(3.0,   bill.discountPercentage)
        assertEquals(970.0, bill.amountAfterDiscount)
    }

    @Test
    fun `patient with 10 prior visits should get 10 percent discount`() {
        val bill = billingCalculator.calculate(fee = 1000.0, completedAppointments = 10)
        assertEquals(10.0,  bill.discountPercentage)
        assertEquals(900.0, bill.amountAfterDiscount)
    }

    @Test
    fun `discount should be capped at 10 percent for 11 prior visits`() {
        val bill = billingCalculator.calculate(fee = 1000.0, completedAppointments = 11)
        assertEquals(10.0, bill.discountPercentage)
    }

    @Test
    fun `GST should be 12 percent of amount after discount`() {
        val bill = billingCalculator.calculate(fee = 1000.0, completedAppointments = 0)
        assertEquals(1120.0, bill.totalAfterTaxAndDiscount)
    }

    @Test
    fun `GST should be applied on discounted amount not base fee`() {
        val bill = billingCalculator.calculate(fee = 1000.0, completedAppointments = 10)
        assertEquals(900.0, bill.amountAfterDiscount)
        assertEquals(1008.0, bill.totalAfterTaxAndDiscount)
    }

    @Test
    fun `insurance should cover 90 percent of total amount`() {
        val bill = billingCalculator.calculate(fee = 1000.0, completedAppointments = 0)
        assertEquals(bill.totalAfterTaxAndDiscount * 0.90, bill.amountCoveredByInsurance)
    }

    @Test
    fun `insurance and copay should always sum to total amount`() {
        val bill = billingCalculator.calculate(fee = 1500.0, completedAppointments = 7)
        assertEquals(bill.totalAfterTaxAndDiscount, bill.amountCoveredByInsurance + bill.coPayAmount)
    }

    @Test
    fun `complete bill for 1000 base fee with no prior visits`() {
        val bill = billingCalculator.calculate(fee = 1000.0, completedAppointments = 0)

        assertEquals(1000.0, bill.fee)
        assertEquals(0.0,    bill.discountPercentage)
        assertEquals(1000.0, bill.amountAfterDiscount)
        assertEquals(1120.0, bill.totalAfterTaxAndDiscount)
        assertEquals(1008.0, bill.amountCoveredByInsurance)
        assertEquals(112.0,  bill.coPayAmount)
        assertEquals(120.0,  bill.taxAmount)
    }

    @Test
    fun `complete bill for 1500 base fee with 5 prior visits`() {
        val bill = billingCalculator.calculate(fee = 1500.0, completedAppointments = 5)

        assertEquals(1500.0, bill.fee)
        assertEquals(5.0,    bill.discountPercentage)
        assertEquals(1425.0, bill.amountAfterDiscount)
        assertEquals(1596.0, bill.totalAfterTaxAndDiscount)
        assertEquals(1436.4, bill.amountCoveredByInsurance)
        assertEquals(159.6,  bill.coPayAmount)
        assertEquals(171.0,  bill.taxAmount)
    }

    @Test
    fun `complete bill for 2000 base fee with max discount`() {
        val bill = billingCalculator.calculate(fee = 2000.0, completedAppointments = 12)

        assertEquals(2000.0, bill.fee)
        assertEquals(10.0,   bill.discountPercentage)
        assertEquals(1800.0, bill.amountAfterDiscount)
        assertEquals(2016.0, bill.totalAfterTaxAndDiscount)
        assertEquals(1814.4, bill.amountCoveredByInsurance)
        assertEquals(201.6,  bill.coPayAmount)
        assertEquals(216.0,  bill.taxAmount)
    }


}