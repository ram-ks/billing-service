package com.linxhealth.common

interface FeeResolver<in K> {
    fun resolveFee(key: K): Double
}
