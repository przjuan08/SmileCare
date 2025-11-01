package sv.edu.udb.smilecare.database

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import sv.edu.udb.smilecare.models.Appointment
import sv.edu.udb.smilecare.models.Availability
import sv.edu.udb.smilecare.models.Doctor
import sv.edu.udb.smilecare.models.User
import java.io.*

class DatabaseHelper(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    companion object {
        private const val DATABASE_NAME = "SmileCare.db"
        private const val DATABASE_VERSION = 1

        // Tablas
        const val TABLE_DOCTORES = "doctores"
        const val TABLE_DISPONIBILIDADES = "disponibilidades"
        const val TABLE_USUARIOS = "usuarios"
        const val TABLE_CITAS = "citas"
        const val TABLE_RECORDATORIOS = "recordatorios"

        // Columnas comunes
        const val COLUMN_ID = "id"
        const val COLUMN_CREADO_EN = "creado_en"
        const val COLUMN_ESTADO = "estado"

        // Columnas doctores
        const val COLUMN_NOMBRE = "nombre"
        const val COLUMN_ESPECIALIDAD = "especialidad"
        const val COLUMN_EMAIL = "email"
        const val COLUMN_TELEFONO = "telefono"
        const val COLUMN_DESCRIPCION = "descripcion"
        const val COLUMN_FOTO_URL = "foto_url"

        // Columnas disponibilidades
        const val COLUMN_DOCTOR_ID = "doctor_id"
        const val COLUMN_FECHA = "fecha"
        const val COLUMN_HORA_INICIO = "hora_inicio"
        const val COLUMN_HORA_FIN = "hora_fin"
        const val COLUMN_TIPO = "tipo"

        // Columnas usuarios
        const val COLUMN_FIREBASE_UID = "firebase_uid"
        const val COLUMN_ROL = "rol"

        // Columnas citas
        const val COLUMN_PACIENTE_ID = "paciente_id"
        const val COLUMN_MOTIVO = "motivo"
        const val COLUMN_UBICACION = "ubicacion"
    }

    private val context: Context = context.applicationContext

    init {
        // Verificar y copiar la base de datos si es necesario
        if (!isDatabaseExists()) {
            copyDatabaseFromAssets()
        }
    }

    override fun onCreate(db: SQLiteDatabase) {

        println("DEBUG: DatabaseHelper onCreate llamado - usando DB pre-poblada")
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {

        try {
            copyDatabaseFromAssets()
        } catch (e: Exception) {
            println("DEBUG: Error al actualizar base de datos: ${e.message}")
        }
    }

    private fun isDatabaseExists(): Boolean {
        val dbFile = context.getDatabasePath(DATABASE_NAME)
        return dbFile.exists()
    }

    private fun copyDatabaseFromAssets() {
        try {
            // Obtener input stream desde assets
            val inputStream = context.assets.open(DATABASE_NAME)

            // Crear directorio de bases de datos si no existe
            val dbFile = context.getDatabasePath(DATABASE_NAME)
            dbFile.parentFile?.mkdirs()

            // Crear output stream hacia la ubicación de la base de datos
            val outputStream = FileOutputStream(dbFile)

            // Copiar el archivo
            val buffer = ByteArray(1024)
            var length: Int
            while (inputStream.read(buffer).also { length = it } > 0) {
                outputStream.write(buffer, 0, length)
            }

            // Cerrar streams
            outputStream.flush()
            outputStream.close()
            inputStream.close()

            println("DEBUG: Base de datos copiada desde assets exitosamente")

        } catch (e: IOException) {
            throw RuntimeException("Error copiando base de datos desde assets", e)
        }
    }

    // Método para verificar el contenido de la base de datos (para debugging)
    fun debugDatabaseContents() {
        val db = readableDatabase

        try {
            // Contar doctores
            val cursorDoctores = db.rawQuery("SELECT COUNT(*) FROM $TABLE_DOCTORES", null)
            cursorDoctores.moveToFirst()
            val countDoctores = cursorDoctores.getInt(0)
            cursorDoctores.close()

            // Contar disponibilidades
            val cursorDisp = db.rawQuery("SELECT COUNT(*) FROM $TABLE_DISPONIBILIDADES", null)
            cursorDisp.moveToFirst()
            val countDisp = cursorDisp.getInt(0)
            cursorDisp.close()

            // Listar doctores
            val cursorList = db.rawQuery("SELECT * FROM $TABLE_DOCTORES", null)
            println("DEBUG: === CONTENIDO DE LA BASE DE DATOS ===")
            println("DEBUG: Total doctores: $countDoctores")
            println("DEBUG: Total disponibilidades: $countDisp")

            while (cursorList.moveToNext()) {
                val id = cursorList.getInt(cursorList.getColumnIndexOrThrow(COLUMN_ID))
                val nombre = cursorList.getString(cursorList.getColumnIndexOrThrow(COLUMN_NOMBRE))
                val especialidad = cursorList.getString(cursorList.getColumnIndexOrThrow(COLUMN_ESPECIALIDAD))
                println("DEBUG: Doctor $id: $nombre - $especialidad")
            }
            cursorList.close()

            println("DEBUG: === FIN DEL CONTENIDO ===")

        } catch (e: Exception) {
            println("DEBUG: Error al leer base de datos: ${e.message}")
        }
    }

    // DOCTORES ----------------------------------------------------------------

    fun getAllDoctores(): List<Doctor> {
        val doctores = mutableListOf<Doctor>()
        val db = readableDatabase
        val cursor: Cursor = db.query(
            TABLE_DOCTORES,
            null,
            "$COLUMN_ESTADO = ?",
            arrayOf("activo"),
            null, null,
            COLUMN_NOMBRE
        )

        while (cursor.moveToNext()) {
            val doctor = Doctor(
                id = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_ID)),
                nombre = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_NOMBRE)),
                especialidad = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_ESPECIALIDAD)),
                email = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_EMAIL)),
                telefono = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_TELEFONO)),
                descripcion = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_DESCRIPCION)),
                fotoUrl = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_FOTO_URL)),
                estado = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_ESTADO)),
                creadoEn = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_CREADO_EN))
            )
            doctores.add(doctor)
        }
        cursor.close()
        return doctores
    }

    fun getDoctorById(id: Int): Doctor? {
        val db = readableDatabase
        val cursor: Cursor = db.query(
            TABLE_DOCTORES,
            null,
            "$COLUMN_ID = ?",
            arrayOf(id.toString()),
            null, null, null
        )

        return if (cursor.moveToFirst()) {
            val doctor = Doctor(
                id = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_ID)),
                nombre = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_NOMBRE)),
                especialidad = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_ESPECIALIDAD)),
                email = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_EMAIL)),
                telefono = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_TELEFONO)),
                descripcion = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_DESCRIPCION)),
                fotoUrl = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_FOTO_URL)),
                estado = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_ESTADO)),
                creadoEn = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_CREADO_EN))
            )
            cursor.close()
            doctor
        } else {
            cursor.close()
            null
        }
    }

    // DISPONIBILIDADES --------------------------------------------------------

    fun getDisponibilidadesByDoctor(doctorId: Int): List<Availability>  {
        val disponibilidades = mutableListOf<Availability>()
        val db = readableDatabase
        val cursor: Cursor = db.query(
            TABLE_DISPONIBILIDADES,
            null,
            "$COLUMN_DOCTOR_ID = ? AND $COLUMN_ESTADO = ?",
            arrayOf(doctorId.toString(), "disponible"),
            null, null,
            "$COLUMN_FECHA, $COLUMN_HORA_INICIO"
        )

        while (cursor.moveToNext()) {
            val disponibilidad = Availability(
                id = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_ID)),
                doctorId = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_DOCTOR_ID)),
                fecha = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_FECHA)),
                horaInicio = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_HORA_INICIO)),
                horaFin = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_HORA_FIN)),
                tipo = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_TIPO)),
                estado = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_ESTADO)),
                creadoEn = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_CREADO_EN))
            )
            disponibilidades.add(disponibilidad)
        }
        cursor.close()
        return disponibilidades
    }

    fun getDisponibilidadesByDoctorAndDate(doctorId: Int, fecha: String): List<Availability> {
        val disponibilidades = mutableListOf<Availability>()
        val db = readableDatabase
        val cursor: Cursor = db.query(
            TABLE_DISPONIBILIDADES,
            null,
            "$COLUMN_DOCTOR_ID = ? AND $COLUMN_FECHA = ? AND $COLUMN_ESTADO = ?",
            arrayOf(doctorId.toString(), fecha, "disponible"),
            null, null,
            COLUMN_HORA_INICIO
        )

        while (cursor.moveToNext()) {
            val disponibilidad = Availability(
                id = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_ID)),
                doctorId = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_DOCTOR_ID)),
                fecha = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_FECHA)),
                horaInicio = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_HORA_INICIO)),
                horaFin = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_HORA_FIN)),
                tipo = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_TIPO)),
                estado = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_ESTADO)),
                creadoEn = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_CREADO_EN))
            )
            disponibilidades.add(disponibilidad)
        }
        cursor.close()
        return disponibilidades
    }

    // CITAS ----------------------------------------------------------------

    fun createAppointment(appointment: Appointment): Long {
        val db = writableDatabase
        val values = ContentValues().apply {
            put(COLUMN_PACIENTE_ID, appointment.pacienteId)
            put(COLUMN_DOCTOR_ID, appointment.doctorId)
            put(COLUMN_FECHA, appointment.fecha)
            put(COLUMN_HORA_INICIO, appointment.horaInicio)
            put(COLUMN_HORA_FIN, appointment.horaFin)
            put(COLUMN_ESTADO, appointment.estado)
            put(COLUMN_MOTIVO, appointment.motivo)
            put(COLUMN_UBICACION, appointment.ubicacion)
            put(COLUMN_CREADO_EN, "datetime('now')")
        }

        val appointmentId = db.insert(TABLE_CITAS, null, values)

        // Actualizar la disponibilidad a "ocupado"
        if (appointmentId != -1L) {
            updateDisponibilidadEstado(appointment.doctorId, appointment.fecha, appointment.horaInicio, "ocupado")
        }

        return appointmentId
    }

    fun getAppointmentsByUser(userId: String): List<Appointment> {
        val appointments = mutableListOf<Appointment>()
        val db = readableDatabase

        val query = """
            SELECT c.*, d.nombre as doctor_nombre, d.especialidad as doctor_especialidad 
            FROM $TABLE_CITAS c 
            INNER JOIN $TABLE_DOCTORES d ON c.doctor_id = d.id 
            WHERE c.paciente_id = ? 
            ORDER BY c.fecha DESC, c.hora_inicio DESC
        """

        val cursor = db.rawQuery(query, arrayOf(userId))

        while (cursor.moveToNext()) {
            val appointment = Appointment(
                id = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_ID)),
                pacienteId = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_PACIENTE_ID)),
                doctorId = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_DOCTOR_ID)),
                fecha = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_FECHA)),
                horaInicio = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_HORA_INICIO)),
                horaFin = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_HORA_FIN)),
                estado = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_ESTADO)),
                motivo = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_MOTIVO)),
                ubicacion = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_UBICACION)),
                creadoEn = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_CREADO_EN)),
                doctorNombre = cursor.getString(cursor.getColumnIndexOrThrow("doctor_nombre")),
                doctorEspecialidad = cursor.getString(cursor.getColumnIndexOrThrow("doctor_especialidad"))
            )
            appointments.add(appointment)
        }
        cursor.close()
        return appointments
    }

    fun getAppointmentById(id: Int): Appointment? {
        val db = readableDatabase

        val query = """
            SELECT c.*, d.nombre as doctor_nombre, d.especialidad as doctor_especialidad 
            FROM $TABLE_CITAS c 
            INNER JOIN $TABLE_DOCTORES d ON c.doctor_id = d.id 
            WHERE c.id = ?
        """

        val cursor = db.rawQuery(query, arrayOf(id.toString()))

        return if (cursor.moveToFirst()) {
            val appointment = Appointment(
                id = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_ID)),
                pacienteId = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_PACIENTE_ID)),
                doctorId = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_DOCTOR_ID)),
                fecha = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_FECHA)),
                horaInicio = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_HORA_INICIO)),
                horaFin = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_HORA_FIN)),
                estado = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_ESTADO)),
                motivo = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_MOTIVO)),
                ubicacion = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_UBICACION)),
                creadoEn = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_CREADO_EN)),
                doctorNombre = cursor.getString(cursor.getColumnIndexOrThrow("doctor_nombre")),
                doctorEspecialidad = cursor.getString(cursor.getColumnIndexOrThrow("doctor_especialidad"))
            )
            cursor.close()
            appointment
        } else {
            cursor.close()
            null
        }
    }

    fun cancelAppointment(appointmentId: Int): Boolean {
        val db = writableDatabase
        val values = ContentValues().apply {
            put(COLUMN_ESTADO, "cancelada")
        }

        // Primero obtener la cita para liberar la disponibilidad
        val appointment = getAppointmentById(appointmentId)

        val rowsAffected = db.update(
            TABLE_CITAS,
            values,
            "$COLUMN_ID = ?",
            arrayOf(appointmentId.toString())
        )

        // Liberar la disponibilidad
        if (rowsAffected > 0 && appointment != null) {
            updateDisponibilidadEstado(appointment.doctorId, appointment.fecha, appointment.horaInicio, "disponible")
        }

        return rowsAffected > 0
    }

    fun rescheduleAppointment(appointmentId: Int, nuevaFecha: String, nuevaHoraInicio: String, nuevaHoraFin: String): Boolean {
        val db = writableDatabase

        // Primero obtener la cita original
        val appointment = getAppointmentById(appointmentId) ?: return false

        // Liberar la disponibilidad original
        updateDisponibilidadEstado(appointment.doctorId, appointment.fecha, appointment.horaInicio, "disponible")

        // Ocupar la nueva disponibilidad
        updateDisponibilidadEstado(appointment.doctorId, nuevaFecha, nuevaHoraInicio, "ocupado")

        // Actualizar la cita
        val values = ContentValues().apply {
            put(COLUMN_FECHA, nuevaFecha)
            put(COLUMN_HORA_INICIO, nuevaHoraInicio)
            put(COLUMN_HORA_FIN, nuevaHoraFin)
        }

        val rowsAffected = db.update(
            TABLE_CITAS,
            values,
            "$COLUMN_ID = ?",
            arrayOf(appointmentId.toString())
        )

        return rowsAffected > 0
    }


    fun debugDisponibilidades() {
        val db = readableDatabase

        try {
            println("DEBUG: === VERIFICANDO DISPONIBILIDADES ===")

            // Verificar todas las disponibilidades
            val cursorAll = db.rawQuery("SELECT * FROM $TABLE_DISPONIBILIDADES", null)
            println("DEBUG: Total disponibilidades en DB: ${cursorAll.count}")

            while (cursorAll.moveToNext()) {
                val id = cursorAll.getInt(cursorAll.getColumnIndexOrThrow(COLUMN_ID))
                val doctorId = cursorAll.getInt(cursorAll.getColumnIndexOrThrow(COLUMN_DOCTOR_ID))
                val fecha = cursorAll.getString(cursorAll.getColumnIndexOrThrow(COLUMN_FECHA))
                val horaInicio = cursorAll.getString(cursorAll.getColumnIndexOrThrow(COLUMN_HORA_INICIO))
                val estado = cursorAll.getString(cursorAll.getColumnIndexOrThrow(COLUMN_ESTADO))
                println("DEBUG: Disp $id - Doctor:$doctorId Fecha:$fecha Hora:$horaInicio Estado:$estado")
            }
            cursorAll.close()

            // Verificar por cada doctor
            val doctores = getAllDoctores()
            for (doctor in doctores) {
                val disponibilidades = getDisponibilidadesByDoctor(doctor.id)
                println("DEBUG: Doctor ${doctor.nombre} (ID:${doctor.id}) tiene ${disponibilidades.size} disponibilidades")

                disponibilidades.forEach { disp ->
                    println("DEBUG:   - ${disp.fecha} ${disp.horaInicio}-${disp.horaFin}")
                }
            }

            println("DEBUG: === FIN VERIFICACIÓN DISPONIBILIDADES ===")

        } catch (e: Exception) {
            println("DEBUG: Error al verificar disponibilidades: ${e.message}")
        }
    }




    private fun updateDisponibilidadEstado(doctorId: Int, fecha: String, horaInicio: String, nuevoEstado: String) {
        val db = writableDatabase
        val values = ContentValues().apply {
            put(COLUMN_ESTADO, nuevoEstado)
        }

        db.update(
            TABLE_DISPONIBILIDADES,
            values,
            "$COLUMN_DOCTOR_ID = ? AND $COLUMN_FECHA = ? AND $COLUMN_HORA_INICIO = ?",
            arrayOf(doctorId.toString(), fecha, horaInicio)
        )
    }

    // USUARIOS -------------------------------------------------------------

    fun createOrUpdateUser(user: User): Long {
        val db = writableDatabase

        // Verificar si el usuario ya existe
        val existingUser = getUserByFirebaseUid(user.firebaseUid)

        return if (existingUser == null) {
            // Crear nuevo usuario
            val values = ContentValues().apply {
                put(COLUMN_FIREBASE_UID, user.firebaseUid)
                put(COLUMN_NOMBRE, user.nombre)
                put(COLUMN_EMAIL, user.email)
                put(COLUMN_TELEFONO, user.telefono ?: "")
                put(COLUMN_ROL, user.rol)
                put(COLUMN_ESTADO, user.estado)
                put(COLUMN_CREADO_EN, "datetime('now')")
            }
            db.insert(TABLE_USUARIOS, null, values)
        } else {
            // Actualizar usuario existente
            val values = ContentValues().apply {
                put(COLUMN_NOMBRE, user.nombre)
                put(COLUMN_EMAIL, user.email)
                put(COLUMN_TELEFONO, user.telefono ?: "")
            }
            db.update(
                TABLE_USUARIOS,
                values,
                "$COLUMN_FIREBASE_UID = ?",
                arrayOf(user.firebaseUid)
            )
            existingUser.id.toLong()
        }
    }

    fun getUserByFirebaseUid(firebaseUid: String): User? {
        val db = readableDatabase
        val cursor: Cursor = db.query(
            TABLE_USUARIOS,
            null,
            "$COLUMN_FIREBASE_UID = ?",
            arrayOf(firebaseUid),
            null, null, null
        )

        return if (cursor.moveToFirst()) {
            val user = User(
                id = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_ID)),
                firebaseUid = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_FIREBASE_UID)),
                nombre = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_NOMBRE)),
                email = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_EMAIL)),
                telefono = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_TELEFONO)),
                rol = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_ROL)),
                estado = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_ESTADO)),
                creadoEn = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_CREADO_EN))
            )
            cursor.close()
            user
        } else {
            cursor.close()
            null
        }
    }

    // MÉTODOS DE LIMPIEZA Y MANTENIMIENTO -----------------------------------

    fun clearAllData() {
        val db = writableDatabase
        val tables = listOf(
            TABLE_RECORDATORIOS,
            TABLE_CITAS,
            TABLE_DISPONIBILIDADES,
            TABLE_DOCTORES,
            TABLE_USUARIOS
        )

        tables.forEach { table ->
            db.delete(table, null, null)
        }
    }
}