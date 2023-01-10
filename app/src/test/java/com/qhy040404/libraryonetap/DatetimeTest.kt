package com.qhy040404.libraryonetap

import com.qhy040404.datetime.Datetime
import com.qhy040404.datetime.Datetime.Companion.toDatetime
import com.qhy040404.datetime.DatetimePart
import org.junit.Assert
import org.junit.Test

class DatetimeTest {
    private val before1 = "2022-01-01T08:00:00".toDatetime()
    private val before2 = "2022-01-01T08:00:00".toDatetime()
    private val now = Datetime.now()

    @Test
    fun testCompareTo() {
        Assert.assertTrue(before1 < now)
        Assert.assertTrue(now > before1)
        Assert.assertTrue(before2 < now)
        Assert.assertTrue(now > before2)
        Assert.assertTrue(before2 == before1)
        Assert.assertTrue(before1 == before2)
    }

    @Test
    fun testConvert() {
        Assert.assertTrue(before1 == Datetime.fromInstant(before1.toInstant()))
        Assert.assertTrue(before1 == Datetime.fromTimestamp(before1.toTimestamp()))
        Assert.assertTrue(before1 == before1.toString().toDatetime())

        val beforeAddYear = "2023-01-01T08:00:00".toDatetime()
        Assert.assertTrue(beforeAddYear == before1.plus(1, DatetimePart.YEAR))

        val beforeMinusYear = "2021-01-01T08:00:00".toDatetime()
        Assert.assertTrue(beforeMinusYear == before1.minus(1, DatetimePart.YEAR))
    }
}
