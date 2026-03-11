package com.linxhealth.common

import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import kotlin.test.assertEquals

class ExperienceTierTest {
    @Test
    fun `from should throw for negative years`() {
        assertThrows<IllegalArgumentException> { ExperienceTier.from(-1) }
    }

    @Test
    fun `from should throw for larger years of experience`() {
        assertThrows<IllegalArgumentException> { ExperienceTier.from(72) }
    }

    @Test
    fun `should return JUNIOR for 0 years of experience tier`() {
        assertEquals(ExperienceTier.JUNIOR, ExperienceTier.from(0))
    }

    @Test
    fun `should return JUNIOR for 19 years`() {
        assertEquals(ExperienceTier.JUNIOR, ExperienceTier.from(19))
    }

    @Test
    fun `should return MID for 20 years`() {
        assertEquals(ExperienceTier.MID, ExperienceTier.from(20))
    }

    @Test
    fun `should return MID for 29 years`() {
        assertEquals(ExperienceTier.MID, ExperienceTier.from(29))
    }

    @Test
    fun `should return SENIOR for 30 years`() {
        assertEquals(ExperienceTier.SENIOR, ExperienceTier.from(30))
    }

    @Test
    fun `should return SENIOR for large values`() {
        assertEquals(ExperienceTier.SENIOR, ExperienceTier.from(70))
    }
}