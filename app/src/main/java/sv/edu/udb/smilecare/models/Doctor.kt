package sv.edu.udb.smilecare.models

data class Doctor(
    val id: Int,
    val nombre: String,
    val especialidad: String,
    val email: String,
    val telefono: String,
    val descripcion: String,
    val fotoUrl: String,
    val estado: String = "activo",
    val creadoEn: String = ""
) {
    constructor() : this(0, "", "", "", "", "", "", "activo", "")
}