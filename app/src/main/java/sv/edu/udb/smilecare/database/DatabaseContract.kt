package sv.edu.udb.smilecare.database

object DatabaseContract {

    object Usuario {
        const val TABLE_NAME = "usuarios"
        const val COLUMN_ID = "id"
        const val COLUMN_FIREBASE_UID = "firebase_uid"
        const val COLUMN_NOMBRE = "nombre"
        const val COLUMN_EMAIL = "email"
        const val COLUMN_TELEFONO = "telefono"
        const val COLUMN_ROL = "rol"
        const val COLUMN_ESTADO = "estado"
        const val COLUMN_CREADO_EN = "creado_en"
    }

    object Doctor {
        const val TABLE_NAME = "doctores"
        const val COLUMN_ID = "id"
        const val COLUMN_NOMBRE = "nombre"
        const val COLUMN_ESPECIALIDAD = "especialidad"
        const val COLUMN_EMAIL = "email"
        const val COLUMN_TELEFONO = "telefono"
        const val COLUMN_DESCRIPCION = "descripcion"
        const val COLUMN_FOTO_URL = "foto_url"
        const val COLUMN_ESTADO = "estado"
        const val COLUMN_CREADO_EN = "creado_en"
    }

    object Disponibilidad {
        const val TABLE_NAME = "disponibilidades"
        const val COLUMN_ID = "id"
        const val COLUMN_DOCTOR_ID = "doctor_id"
        const val COLUMN_FECHA = "fecha"
        const val COLUMN_HORA_INICIO = "hora_inicio"
        const val COLUMN_HORA_FIN = "hora_fin"
        const val COLUMN_TIPO = "tipo"
        const val COLUMN_ESTADO = "estado"
        const val COLUMN_CREADO_EN = "creado_en"
    }

    object Cita {
        const val TABLE_NAME = "citas"
        const val COLUMN_ID = "id"
        const val COLUMN_PACIENTE_ID = "paciente_id"
        const val COLUMN_DOCTOR_ID = "doctor_id"
        const val COLUMN_FECHA = "fecha"
        const val COLUMN_HORA_INICIO = "hora_inicio"
        const val COLUMN_HORA_FIN = "hora_fin"
        const val COLUMN_ESTADO = "estado"
        const val COLUMN_MOTIVO = "motivo"
        const val COLUMN_UBICACION = "ubicacion"
        const val COLUMN_CREADO_EN = "creado_en"
    }

    object Recordatorio {
        const val TABLE_NAME = "recordatorios"
        const val COLUMN_ID = "id"
        const val COLUMN_CITA_ID = "cita_id"
        const val COLUMN_TIPO = "tipo"
        const val COLUMN_ENVIADO = "enviado"
        const val COLUMN_ENVIADO_EN = "enviado_en"
    }
}