package sv.edu.udb.smilecare.models

import sv.edu.udb.smilecare.R

data class Appointment(
    val id: Int,
    val pacienteId: String,
    val doctorId: Int,
    val fecha: String,
    val horaInicio: String,
    val horaFin: String,
    val estado: String = "pendiente",
    val motivo: String = "Consulta general",
    val ubicacion: String = "Clínica Dental SmileCare, Calle Principal #123, San Salvador",
    val creadoEn: String = "",
    val doctorNombre: String = "",
    val doctorEspecialidad: String = ""
) {
    constructor() : this(0, "", 0, "", "", "", "pendiente", "Consulta general",
        "Clínica Dental SmileCare, Calle Principal #123, San Salvador", "", "", "")

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

    fun getEstadoColor(): Int {
        return when (estado) {
            "pendiente" -> R.color.warning
            "confirmada" -> R.color.success
            "cancelada" -> R.color.error
            "atendida" -> R.color.primary
            else -> R.color.text_secondary
        }
    }

    fun getEstadoTexto(): String {
        return when (estado) {
            "pendiente" -> "Pendiente"
            "confirmada" -> "Confirmada"
            "cancelada" -> "Cancelada"
            "atendida" -> "Atendida"
            else -> estado
        }
    }
}