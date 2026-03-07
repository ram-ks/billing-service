package com.linxhealth.common

import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

class BillCalculatorTest {

    @Test
    fun `new patient with 0 prior visits should get no discount`() {
        val bill = BillCalculator.calculate(fee = 1000.0, completedAppointments = 0)
        assertEquals(0.0,    bill.discountPercentage)
        assertEquals(1000.0, bill.amountAfterDiscount)
    }

    @Test
    fun `patient with 3 prior visits should get 3 percent discount`() {
        val bill = BillCalculator.calculate(fee = 1000.0, completedAppointments = 3)
        assertEquals(3.0,   bill.discountPercentage)
        assertEquals(970.0, bill.amountAfterDiscount)
    }

    @Test
    fun `patient with 10 prior visits should get 10 percent discount`() {
        val bill = BillCalculator.calculate(fee = 1000.0, completedAppointments = 10)
        assertEquals(10.0,  bill.discountPercentage)
        assertEquals(900.0, bill.amountAfterDiscount)
    }

    @Test
    fun `discount should be capped at 10 percent for 11 prior visits`() {
        val bill = BillCalculator.calculate(fee = 1000.0, completedAppointments = 11)
        assertEquals(10.0, bill.discountPercentage)
    }

    @Test
    fun `GST should be 12 percent of amount after discount`() {
        val bill = BillCalculator.calculate(fee = 1000.0, completedAppointments = 0)
        assertEquals(1120.0, bill.afterTaxAndDiscount, 0.001)
    }

    @Test
    fun `GST should be applied on discounted amount not base fee`() {
        // fee=1000, 10% discount -> afterDiscount=900 -> GST=108
        val bill = BillCalculator.calculate(fee = 1000.0, completedAppointments = 10)
        assertEquals(900.0, bill.amountAfterDiscount, 0.001)
        assertEquals(1008.0, bill.afterTaxAndDiscount, 0.001)
    }

    @Test
    fun `insurance should cover 90 percent of total amount`() {
        val bill = BillCalculator.calculate(fee = 1000.0, completedAppointments = 0)
        assertEquals(bill.afterTaxAndDiscount * 0.90, bill.amountCoveredByInsurance, 0.001)
    }

    @Test
    fun `insurance and copay should always sum to total amount`() {
        val bill = BillCalculator.calculate(fee = 1500.0, completedAppointments = 7)
        assertEquals(bill.afterTaxAndDiscount, bill.amountCoveredByInsurance + bill.coPayAmount, 0.001)
    }



}