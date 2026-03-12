package com.linxhealth.model

data class Bill(
    val coPayAmount: Double, // final amount user has to bear
    val fee: Double, // base fee
    val amountAfterDiscount: Double, // discount amount
    val discountPercentage: Double, // discount %
    val amountCoveredByInsurance: Double, // amount insurance company will pay
    val totalAfterTaxAndDiscount: Double, // amount after applying discount and tax
    val taxAmount: Double, // 12% GST, amount after applying tax
    val discountAmount: Double, // discount before tax is applied
)
