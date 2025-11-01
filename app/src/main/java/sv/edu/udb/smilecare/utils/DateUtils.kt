package sv.edu.udb.smilecare.utils

import java.text.SimpleDateFormat
import java.util.*

object DateUtils {

    private val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
    private val timeFormat = SimpleDateFormat("HH:mm", Locale.getDefault())
    private val displayDateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
    private val displayDateTimeFormat = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())

    fun formatDateForDisplay(date: String): String {
        return try {
            val parsedDate = dateFormat.parse(date)
            displayDateFormat.format(parsedDate)
        } catch (e: Exception) {
            date
        }
    }

    fun formatDateTimeForDisplay(date: String, time: String): String {
        return try {
            val dateTime = "$date $time"
            val inputFormat = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault())
            val parsedDate = inputFormat.parse(dateTime)
            displayDateTimeFormat.format(parsedDate)
        } catch (e: Exception) {
            "$date $time"
        }
    }

    fun getCurrentDate(): String {
        return dateFormat.format(Date())
    }

    fun getCurrentDateTime(): String {
        return SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(Date())
    }

    fun isDateInFuture(date: String): Boolean {
        return try {
            val inputDate = dateFormat.parse(date)
            val today = dateFormat.parse(getCurrentDate())
            inputDate.after(today) || inputDate == today
        } catch (e: Exception) {
            false
        }
    }

    fun getDaysBetweenDates(startDate: String, endDate: String): Int {
        return try {
            val start = dateFormat.parse(startDate)
            val end = dateFormat.parse(endDate)
            val difference = end.time - start.time
            (difference / (1000 * 60 * 60 * 24)).toInt()
        } catch (e: Exception) {
            0
        }
    }

    fun addDaysToDate(date: String, days: Int): String {
        return try {
            val calendar = Calendar.getInstance()
            calendar.time = dateFormat.parse(date)!!
            calendar.add(Calendar.DAY_OF_YEAR, days)
            dateFormat.format(calendar.time)
        } catch (e: Exception) {
            date
        }
    }

    fun getDayOfWeek(date: String): String {
        return try {
            val calendar = Calendar.getInstance()
            calendar.time = dateFormat.parse(date)!!
            val dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK)
            when (dayOfWeek) {
                Calendar.SUNDAY -> "Domingo"
                Calendar.MONDAY -> "Lunes"
                Calendar.TUESDAY -> "Martes"
                Calendar.WEDNESDAY -> "Miércoles"
                Calendar.THURSDAY -> "Jueves"
                Calendar.FRIDAY -> "Viernes"
                Calendar.SATURDAY -> "Sábado"
                else -> ""
            }
        } catch (e: Exception) {
            ""
        }
    }

    fun isValidTimeRange(startTime: String, endTime: String): Boolean {
        return try {
            val start = timeFormat.parse(startTime)
            val end = timeFormat.parse(endTime)
            start.before(end)
        } catch (e: Exception) {
            false
        }
    }

    fun getTimeDifferenceInMinutes(startTime: String, endTime: String): Int {
        return try {
            val start = timeFormat.parse(startTime)
            val end = timeFormat.parse(endTime)
            val difference = end.time - start.time
            (difference / (1000 * 60)).toInt()
        } catch (e: Exception) {
            0
        }
    }
}