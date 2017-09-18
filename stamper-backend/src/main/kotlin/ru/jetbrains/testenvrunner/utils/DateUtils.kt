package ru.jetbrains.testenvrunner.utils

import org.springframework.stereotype.Component
import java.text.SimpleDateFormat
import java.util.*

@Component
class DateUtils {
    val dateFormat = SimpleDateFormat("HH:mm dd/MM/yy")
    /**
     * Get current date as String
     * @return Current date in "HH:mm dd/MM/yy" format
     */
    fun getCurrentDateAsString(): String {
        return dateFormat.format(Date())
    }

    /**
     * Get current date
     * @return Current date in "HH:mm dd/MM/yy" format
     */
    fun getCurrentDate(): Date {
        return Date()
    }

    /**
     * Get Java Date from String
     * @param stringDate in "HH:mm dd/MM/yy" format
     * @return date object
     */
    fun parseDate(stringDate: String): Date {
        return dateFormat.parse(stringDate)
    }

    fun addDaysToDate(date: Date, days: Int): Date {
        val c = Calendar.getInstance()
        c.time = date
        c.add(Calendar.DATE, days) //same with c.add(Calendar.DAY_OF_MONTH, 1);
        return c.time ?: throw Exception("the exception in Java calendar")
    }
}