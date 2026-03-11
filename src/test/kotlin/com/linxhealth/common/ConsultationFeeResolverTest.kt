package com.linxhealth.common

import com.linxhealth.model.Speciality
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

class ConsultationFeeResolverTest {
    private val resolver = ConsultationFeeResolver()

    @Test
    fun `resolve should throw for unsupported specialty`() {
        assertThrows<IllegalArgumentException> {
            resolver.resolveFee(ConsultationFeeKey(Speciality.DERMATOLOGY, 10))
        }
    }

    @Test
    fun `resolve should return 1000 for CARDIOLOGY JUNIOR tier`() {
        assertEquals(1000.0, resolver.resolveFee(ConsultationFeeKey(Speciality.CARDIOLOGY, 15)))
    }

    @Test
    fun `resolve should return 1500 for CARDIOLOGY MID tier`() {
        assertEquals(1500.0, resolver.resolveFee(ConsultationFeeKey(Speciality.CARDIOLOGY, 20)))
        assertEquals(1500.0, resolver.resolveFee(ConsultationFeeKey(Speciality.CARDIOLOGY, 29)))
    }

    @Test
    fun `resolve should return 2000 for CARDIOLOGY SENIOR tier`() {
        assertEquals(2000.0, resolver.resolveFee(ConsultationFeeKey(Speciality.CARDIOLOGY, 30)))
        assertEquals(2000.0, resolver.resolveFee(ConsultationFeeKey(Speciality.CARDIOLOGY, 45)))
    }

    @Test
    fun `resolve should return 800 for ORTHOPEDICS JUNIOR tier`() {
        assertEquals(800.0, resolver.resolveFee(ConsultationFeeKey(Speciality.ORTHOPEDICS, 10)))
    }

    @Test
    fun `resolve should return 1000 for ORTHOPEDICS MID tier`() {
        assertEquals(1000.0, resolver.resolveFee(ConsultationFeeKey(Speciality.ORTHOPEDICS, 25)))
    }

    @Test
    fun `resolve should return 1500 for ORTHOPEDICS SENIOR tier`() {
        assertEquals(1500.0, resolver.resolveFee(ConsultationFeeKey(Speciality.ORTHOPEDICS, 35)))
    }

    @Test
    fun `resolve should return 500 for GENERAL JUNIOR tier`() {
        assertEquals(500.0, resolver.resolveFee(ConsultationFeeKey(Speciality.GENERAL, 10)))
    }

    @Test
    fun `resolve should return 800 for GENERAL MID tier`() {
        assertEquals(800.0, resolver.resolveFee(ConsultationFeeKey(Speciality.GENERAL, 25)))
    }

    @Test
    fun `resolve should return 1200 for GENERAL SENIOR tier`() {
        assertEquals(1200.0, resolver.resolveFee(ConsultationFeeKey(Speciality.GENERAL, 35)))
    }
}