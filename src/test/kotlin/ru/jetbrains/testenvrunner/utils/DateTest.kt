package ru.jetbrains.testenvrunner.utils

import org.junit.Assert
import org.junit.Test
import java.text.SimpleDateFormat

class DateTest : Assert() {
    val dateUtil = DateUtils()
    val dateFormat = SimpleDateFormat("HH:mm dd/MM/yy")
    @Test
    fun getCurrentDateTest() {
        val curDateString = dateUtil.getCurrentDate()
        val date = dateUtil.parseDate(curDateString)
        dateFormat.format(date)


        assertEquals("The date is not the same", curDateString, dateFormat.format(date))
    }
}
