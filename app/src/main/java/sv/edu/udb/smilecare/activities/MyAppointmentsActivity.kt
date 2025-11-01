package sv.edu.udb.smilecare.activities

import android.content.Intent
import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import sv.edu.udb.smilecare.R
import sv.edu.udb.smilecare.adapters.AppointmentsAdapter
import sv.edu.udb.smilecare.database.DatabaseHelper
import sv.edu.udb.smilecare.models.Appointment

class MyAppointmentsActivity : AppCompatActivity() {

    private lateinit var dbHelper: DatabaseHelper
    private lateinit var auth: FirebaseAuth

    private lateinit var rvAppointments: RecyclerView
    private lateinit var tvEmptyState: TextView

    private val appointments = mutableListOf<Appointment>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_my_appointments)

        // Inicializar Firebase Auth
        auth = FirebaseAuth.getInstance()

        // Inicializar base de datos
        dbHelper = DatabaseHelper(this)

        // Configurar toolbar
        val toolbar = findViewById<com.google.android.material.appbar.MaterialToolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = "Mis Citas"

        // Inicializar vistas
        initViews()

        // Cargar citas del usuario
        loadUserAppointments()
    }

    private fun initViews() {
        rvAppointments = findViewById(R.id.rvAppointments)
        tvEmptyState = findViewById(R.id.tvEmptyState)

        // Configurar RecyclerView
        rvAppointments.layoutManager = LinearLayoutManager(this)
    }

    private fun loadUserAppointments() {
        val user = auth.currentUser
        if (user != null) {
            appointments.clear()
            appointments.addAll(dbHelper.getAppointmentsByUser(user.uid))

            if (appointments.isEmpty()) {
                showEmptyState()
            } else {
                showAppointmentsList()
                setupAdapter()
            }
        } else {
            showEmptyState()
        }
    }

    private fun showEmptyState() {
        rvAppointments.visibility = android.view.View.GONE
        tvEmptyState.visibility = android.view.View.VISIBLE
    }

    private fun showAppointmentsList() {
        rvAppointments.visibility = android.view.View.VISIBLE
        tvEmptyState.visibility = android.view.View.GONE
    }

    private fun setupAdapter() {
        val adapter = AppointmentsAdapter(
            appointments = appointments,
            onVerDetallesClickListener = { appointment ->
                goToAppointmentDetails(appointment)
            },
            onCancelarClickListener = { appointment ->
                showCancelConfirmationDialog(appointment)
            },
            onReagendarClickListener = { appointment ->
                goToRescheduleAppointment(appointment)
            }
        )
        rvAppointments.adapter = adapter
    }

    private fun goToAppointmentDetails(appointment: Appointment) {
        val intent = Intent(this, AppointmentDetailActivity::class.java)
        intent.putExtra("appointment_id", appointment.id)
        startActivity(intent)
    }

    private fun showCancelConfirmationDialog(appointment: Appointment) {
        AlertDialog.Builder(this)
            .setTitle("Cancelar Cita")
            .setMessage("¿Está seguro de que desea cancelar esta cita?")
            .setPositiveButton("Sí") { dialog, which ->
                cancelAppointment(appointment)
            }
            .setNegativeButton("No", null)
            .show()
    }

    private fun cancelAppointment(appointment: Appointment) {
        val success = dbHelper.cancelAppointment(appointment.id)

        if (success) {
            // Actualizar lista
            loadUserAppointments()
            // Mostrar mensaje de éxito
            android.widget.Toast.makeText(this, "Cita cancelada exitosamente", android.widget.Toast.LENGTH_SHORT).show()
        } else {
            android.widget.Toast.makeText(this, "Error al cancelar la cita", android.widget.Toast.LENGTH_SHORT).show()
        }
    }

    private fun goToRescheduleAppointment(appointment: Appointment) {
        val intent = Intent(this, RescheduleAppointmentActivity::class.java)
        intent.putExtra("appointment_id", appointment.id)
        startActivity(intent)
    }

    override fun onResume() {
        super.onResume()
        // Recargar citas cuando la actividad se reanude
        loadUserAppointments()
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }
}