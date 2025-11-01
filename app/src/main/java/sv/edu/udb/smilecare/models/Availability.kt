package sv.edu.udb.smilecare.models

import java.util.*

data class Availability(
    val id: Int,
    val doctorId: Int,
    val fecha: String,
    val horaInicio: String,
    val horaFin: String,
    val tipo: String = "normal",
    val estado: String = "disponible",
    val creadoEn: String = ""
) {
    constructor() : this(0, 0, "", "", "", "normal", "disponible", "")

    fun getFechaFormateada(): String {
        return try {
            val parts = fecha.split("-")
            "${parts[2]}/${parts[1]}/${parts[0]}"
        } catch (e: Exception) {
            fecha
        }
    }

    fun getHoraFormateada(): String {
        return "$horaInicio - $horaFin"
    }
}