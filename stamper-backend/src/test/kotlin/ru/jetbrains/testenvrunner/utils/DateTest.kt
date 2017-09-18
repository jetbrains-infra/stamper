package ru.jetbrains.testenvrunner.utils

import org.junit.Assert
import org.junit.Test
import java.text.SimpleDateFormat

class DateTest : Assert() {
    private val dateUtil = DateUtils()
    private val dateFormat = SimpleDateFormat("HH:mm dd/MM/yy")
    @Test
    fun getCurrentDateTest() {
        val curDateString = dateUtil.getCurrentDateAsString()
        val date = dateUtil.parseDate(curDateString)
        dateFormat.format(date)

        assertEquals("The date is not the same", curDateString, dateFormat.format(date))
    }
}
