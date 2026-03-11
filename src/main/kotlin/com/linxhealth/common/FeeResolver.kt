package com.linxhealth.common

import com.linxhealth.controller.dto.BillResponse

interface FeeResolver<in K> {
    fun resolveFee(key: K): Double
}
