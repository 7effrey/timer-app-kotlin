package com.jeffrey.timer.extension

import org.junit.Test
import org.junit.Assert.assertEquals

class LongExtTest {
    @Test
    fun `toMinuteSecondFormat() should return correct answer`() {
        assertEquals("06:40", 400L.toMinuteSecondFormat())
        assertEquals("00:00", 0L.toMinuteSecondFormat())
        assertEquals("00:00", (-1000L).toMinuteSecondFormat())
        assertEquals("60:00", 3600L.toMinuteSecondFormat())
        assertEquals("100:00", 6000L.toMinuteSecondFormat())
    }
}