package com.qhy040404.libraryonetap

import com.qhy040404.datetime.Datetime
import com.qhy040404.datetime.Datetime.Companion.toDateTime
import com.qhy040404.datetime.DatetimePart
import org.junit.Assert
import org.junit.Test

class DatetimeTest {
    private val before1 = "2022-01-01T08:00:00".toDateTime()
    private val before2 = "2022-01-01T08:00:00".toDateTime()
    private val now = Datetime.now()

    @Test
    fun testEqual() {
        Assert.assertTrue(before1.equals(before2))
    }

    @Test
    fun testBefore() {
        Assert.assertTrue(before1.isBefore(now))
    }

    @Test
    fun testAfter() {
        Assert.assertTrue(now.isAfter(before1))
    }

    @Test
    fun testConvert() {
        Assert.assertTrue(before1.equals(Datetime.fromInstant(before1.toInstant())))
        Assert.assertTrue(before1.equals(Datetime.fromTimestamp(before1.toTimestamp())))
        Assert.assertTrue(before1.equals(before1.toString().toDateTime()))

        val beforeAddYear = "2023-01-01T08:00:00".toDateTime()
        Assert.assertTrue(beforeAddYear.equals(before1.plus(1, DatetimePart.YEAR)))

        val beforeMinusYear = "2021-01-01T08:00:00".toDateTime()
        Assert.assertTrue(beforeMinusYear.equals(before1.minus(1, DatetimePart.YEAR)))
    }
}
