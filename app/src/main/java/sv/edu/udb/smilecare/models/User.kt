package sv.edu.udb.smilecare.models

data class User(
    val id: Int,
    val firebaseUid: String,
    val nombre: String,
    val email: String,
    val telefono: String?,
    val rol: String = "paciente",
    val estado: String = "activo",
    val creadoEn: String = ""
) {
    constructor() : this(0, "", "", "", null, "paciente", "activo", "")
}