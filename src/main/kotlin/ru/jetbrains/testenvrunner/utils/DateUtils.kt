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
    fun getCurrentDate(): String {
        return dateFormat.format(Date())
    }

    /**
     * Get Java Date from String
     * @param stringDate in "HH:mm dd/MM/yy" format
     * @return date object
     */
    fun parseDate(stringDate: String): Date {
        return dateFormat.parse(stringDate)
    }
}