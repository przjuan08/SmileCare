package sv.edu.udb.smilecare.utils

import android.text.TextUtils
import android.util.Patterns
import java.util.regex.Pattern

object ValidationUtils {

    fun isValidEmail(email: String): Boolean {
        return !TextUtils.isEmpty(email) && Patterns.EMAIL_ADDRESS.matcher(email).matches()
    }

    fun isValidPassword(password: String): Boolean {
        // Mínimo 6 caracteres, al menos una letra y un número
        val passwordPattern = "^(?=.*[A-Za-z])(?=.*\\d).{6,}\$"
        return Pattern.matches(passwordPattern, password)
    }

    fun isValidPhone(phone: String): Boolean {
        // Formato de teléfono salvadoreño: 2###-#### o 7###-####
        val phonePattern = "^[27]\\d{3}-\\d{4}\$"
        return Pattern.matches(phonePattern, phone)
    }

    fun isValidName(name: String): Boolean {
        // Solo letras, espacios y acentos, mínimo 2 caracteres
        val namePattern = "^[a-zA-ZáéíóúÁÉÍÓÚñÑ\\s]{2,50}\$"
        return Pattern.matches(namePattern, name)
    }

    fun isValidDate(date: String): Boolean {
        // Formato YYYY-MM-DD
        val datePattern = "^\\d{4}-\\d{2}-\\d{2}\$"
        if (!Pattern.matches(datePattern, date)) return false

        return try {
            val parts = date.split("-")
            val year = parts[0].toInt()
            val month = parts[1].toInt()
            val day = parts[2].toInt()

            month in 1..12 && day in 1..31 && year >= 2024
        } catch (e: Exception) {
            false
        }
    }

    fun isValidTime(time: String): Boolean {
        // Formato HH:MM en 24 horas
        val timePattern = "^([01]?[0-9]|2[0-3]):[0-5][0-9]\$"
        return Pattern.matches(timePattern, time)
    }

    fun getPasswordStrength(password: String): PasswordStrength {
        return when {
            password.length < 6 -> PasswordStrength.WEAK
            password.length < 8 -> PasswordStrength.MEDIUM
            Pattern.matches("^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d).+\$", password) -> PasswordStrength.STRONG
            else -> PasswordStrength.MEDIUM
        }
    }

    enum class PasswordStrength {
        WEAK, MEDIUM, STRONG
    }

    fun formatPhoneNumber(phone: String): String {
        return if (phone.length == 8 && phone.all { it.isDigit() }) {
            "${phone.substring(0, 4)}-${phone.substring(4)}"
        } else {
            phone
        }
    }

    fun sanitizeInput(input: String): String {
        return input.trim().replace(Regex("\\s+"), " ")
    }

    fun isFutureDateTime(date: String, time: String): Boolean {
        return try {
            val dateTimeStr = "$date $time"
            val format = java.text.SimpleDateFormat("yyyy-MM-dd HH:mm", java.util.Locale.getDefault())
            val appointmentDateTime = format.parse(dateTimeStr)
            val currentDateTime = java.util.Date()

            appointmentDateTime.after(currentDateTime)
        } catch (e: Exception) {
            false
        }
    }
}