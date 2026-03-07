package com.linxhealth.model

data class Bill(
    val coPayAmount: Double,
    val fee: Double,
    val amountAfterDiscount: Double,
    val discountPercentage: Double,
    val amountCoveredByInsurance: Double,
    val afterTaxAndDiscount: Double,
)
