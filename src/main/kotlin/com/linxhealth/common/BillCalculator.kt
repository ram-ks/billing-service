package com.linxhealth.common

import com.linxhealth.model.Bill

// TODO: can we do this via Singleton?
object BillCalculator {

    fun calculate(fee: Double, completedAppointments: Int): Bill {
        // step1 : discount on amount
        val mininmumDiscount = minOf(completedAppointments.toDouble(), 10.0)
        val discount = fee * (mininmumDiscount / 100)
        val discountedAmount = fee - discount

        // step2: apply tax
        // TODO: remove magic numbers
        val withTax = discountedAmount * 0.12
        val afterTaxAndDiscount = discountedAmount + withTax

        // step3: insurance amount
        // TODO: remove magic numbers
        val amountCoveredByInsurance = afterTaxAndDiscount * 0.90
        val coPayAmount = afterTaxAndDiscount - amountCoveredByInsurance

        return Bill(
            fee = fee,
            discountPercentage = mininmumDiscount,
            amountAfterDiscount = discountedAmount,
            amountCoveredByInsurance = amountCoveredByInsurance,
            coPayAmount = coPayAmount,
            afterTaxAndDiscount = afterTaxAndDiscount,
            discountAmount = discount, // TODO: do we need this
            taxAmount = withTax,
        )
    }
}