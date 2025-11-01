package sv.edu.udb.smilecare.services

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import sv.edu.udb.smilecare.R
import sv.edu.udb.smilecare.activities.MyAppointmentsActivity
import sv.edu.udb.smilecare.database.DatabaseHelper
import sv.edu.udb.smilecare.models.Appointment
import java.util.*
import sv.edu.udb.smilecare.utils.DateUtils
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit

class NotificationService(private val context: Context) {

    private val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
    private val dbHelper = DatabaseHelper(context)
    private val scheduler = Executors.newScheduledThreadPool(1)

    companion object {
        private const val CHANNEL_ID = "smilecare_notifications"
        private const val CHANNEL_NAME = "Recordatorios de Citas"
        private const val NOTIFICATION_ID = 1
    }

    init {
        createNotificationChannel()
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                CHANNEL_NAME,
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Recordatorios para sus citas dentales"
                enableLights(true)
                enableVibration(true)
            }
            notificationManager.createNotificationChannel(channel)
        }
    }

    fun scheduleAppointmentReminders() {
        // Programar verificación diaria de recordatorios
        scheduler.scheduleAtFixedRate({
            checkUpcomingAppointments()
        }, 0, 1, TimeUnit.DAYS)
    }

    private fun checkUpcomingAppointments() {
        val currentUser = getCurrentUserId() ?: return

        val appointments = dbHelper.getAppointmentsByUser(currentUser)
        val today = DateUtils.getCurrentDate()

        appointments.forEach { appointment ->
            if (appointment.estado == "confirmada" || appointment.estado == "pendiente") {
                val daysUntilAppointment = DateUtils.getDaysBetweenDates(today, appointment.fecha)

                when (daysUntilAppointment) {
                    1 -> send24HourReminder(appointment)
                    0 -> send1HourReminder(appointment)
                }
            }
        }
    }

    private fun send24HourReminder(appointment: Appointment) {
        val title = "Recordatorio de Cita - 24 Horas"
        val message = "Tiene una cita mañana con ${appointment.doctorNombre} a las ${appointment.horaInicio}"

        sendNotification(title, message, appointment)
    }

    private fun send1HourReminder(appointment: Appointment) {
        val title = "Recordatorio de Cita - 1 Hora"
        val message = "Tiene una cita en 1 hora con ${appointment.doctorNombre}"

        sendNotification(title, message, appointment)
    }

    private fun sendNotification(title: String, message: String, appointment: Appointment) {
        val intent = Intent(context, MyAppointmentsActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }

        val pendingIntent = PendingIntent.getActivity(
            context,
            appointment.id,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val notification = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_logo)
            .setContentTitle(title)
            .setContentText(message)
            .setStyle(NotificationCompat.BigTextStyle().bigText(message))
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .build()

        notificationManager.notify(appointment.id, notification)

        // Registrar en la base de datos
        saveReminderRecord(appointment, if (title.contains("24")) "24h" else "1h")
    }

    private fun saveReminderRecord(appointment: Appointment, tipo: String) {
        val db = dbHelper.writableDatabase
        val values = android.content.ContentValues().apply {
            put("cita_id", appointment.id)
            put("tipo", tipo)
            put("enviado", 1)
            put("enviado_en", DateUtils.getCurrentDateTime())
        }

        db.insert("recordatorios", null, values)
    }

    private fun getCurrentUserId(): String? {
        // Implementar lógica para obtener el ID del usuario actual
        // Por ejemplo, desde SharedPreferences o Firebase Auth
        return null
    }

    fun cancelAllNotifications() {
        notificationManager.cancelAll()
        scheduler.shutdown()
    }

    fun showAppointmentConfirmedNotification(appointment: Appointment) {
        val title = "Cita Confirmada"
        val message = "Su cita con ${appointment.doctorNombre} ha sido confirmada para el ${appointment.getFechaFormateada()} a las ${appointment.horaInicio}"

        sendNotification(title, message, appointment)
    }

    fun showAppointmentCancelledNotification(appointment: Appointment) {
        val title = "Cita Cancelada"
        val message = "Su cita con ${appointment.doctorNombre} ha sido cancelada"

        sendNotification(title, message, appointment)
    }
}