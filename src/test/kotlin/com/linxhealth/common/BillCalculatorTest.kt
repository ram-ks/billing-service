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

    @Test
    fun `complete bill for 1000 base fee with no prior visits`() {
        // fee=1000, discount=0, afterDiscount=1000, GST=120, total=1120
        // insurance=1008, copay=112
        val bill = BillCalculator.calculate(fee = 1000.0, completedAppointments = 0)

        assertEquals(1000.0, bill.fee,             0.001)
        assertEquals(0.0,    bill.discountPercentage,      0.001)
        assertEquals(1000.0, bill.amountAfterDiscount,  0.001)
        assertEquals(1120.0, bill.afterTaxAndDiscount,          0.001)
        assertEquals(1008.0, bill.amountCoveredByInsurance,      0.001)
        assertEquals(112.0,  bill.coPayAmount,          0.001)
        assertEquals(120.0,  bill.taxAmount,            0.001)
    }

    @Test
    fun `complete bill for 1500 base fee with 5 prior visits`() {
        // fee=1500, discount=5%, discountAmount=75, afterDiscount=1425
        // GST=171, total=1596, insurance=1436.4, copay=159.6
        val bill = BillCalculator.calculate(fee = 1500.0, completedAppointments = 5)

        assertEquals(1500.0, bill.fee,             0.001)
        assertEquals(5.0,    bill.discountPercentage,      0.001)
        assertEquals(1425.0, bill.amountAfterDiscount,  0.001)
        assertEquals(1596.0, bill.afterTaxAndDiscount,          0.001)
        assertEquals(1436.4, bill.amountCoveredByInsurance,      0.001)
        assertEquals(159.6,  bill.coPayAmount,          0.001)
        assertEquals(171.0,  bill.taxAmount,            0.001)
    }

    @Test
    fun `complete bill for 2000 base fee with max discount`() {
        // fee=2000, discount=10%, discountAmount=200, afterDiscount=1800
        // GST=216, total=2016, insurance=1814.4, copay=201.6
        val bill = BillCalculator.calculate(fee = 2000.0, completedAppointments = 10)

        assertEquals(2000.0, bill.fee,             0.001)
        assertEquals(10.0,   bill.discountPercentage,      0.001)
        assertEquals(1800.0, bill.amountAfterDiscount,  0.001)
        assertEquals(2016.0, bill.afterTaxAndDiscount,          0.001)
        assertEquals(1814.4, bill.amountCoveredByInsurance,      0.001)
        assertEquals(201.6,  bill.coPayAmount,          0.001)
        assertEquals(216.0,  bill.taxAmount,            0.001)
    }


}