package com.linxhealth.common

import com.linxhealth.common.Constants.GST_RATE
import com.linxhealth.common.Constants.HUNDRED
import com.linxhealth.common.Constants.INSURANCE_COVERAGE
import com.linxhealth.common.Constants.MAX_DISCOUNT
import com.linxhealth.model.Bill
import java.math.BigDecimal
import java.math.RoundingMode

object BillCalculator {
    fun calculate(fee: Double, completedAppointments: Int): Bill {
        val base = BigDecimal(fee)

        // step1 : discount on amount
        val minimumDiscount = minOf(BigDecimal(completedAppointments), MAX_DISCOUNT)
        val discount = (base * minimumDiscount / HUNDRED).roundMoney()
        val discountedAmount = (base - discount).roundMoney()

        // step2: apply tax
        val withTax = (discountedAmount * GST_RATE).roundMoney()
        val afterTaxAndDiscount = (discountedAmount + withTax).roundMoney()

        // step3: insurance amount
        val amountCoveredByInsurance = (afterTaxAndDiscount * INSURANCE_COVERAGE).roundMoney()
        val coPayAmount = (afterTaxAndDiscount - amountCoveredByInsurance).roundMoney()

        return Bill(
            fee = fee,
            discountPercentage = minimumDiscount.toDouble(),
            amountAfterDiscount = discountedAmount.toDouble(),
            amountCoveredByInsurance = amountCoveredByInsurance.toDouble(),
            coPayAmount = coPayAmount.toDouble(),
            afterTaxAndDiscount = afterTaxAndDiscount.toDouble(),
            discountAmount = discount.toDouble(),
            taxAmount = withTax.toDouble(),
        )
    }

    private fun BigDecimal.roundMoney() = this.setScale(2, RoundingMode.HALF_UP)
}