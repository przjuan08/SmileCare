package sv.edu.udb.smilecare.activities

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import sv.edu.udb.smilecare.R
import sv.edu.udb.smilecare.database.DatabaseHelper
import sv.edu.udb.smilecare.models.Appointment

class ConfirmAppointmentActivity : AppCompatActivity() {

    private lateinit var dbHelper: DatabaseHelper

    private lateinit var tvDoctor: TextView
    private lateinit var tvFecha: TextView
    private lateinit var tvHorario: TextView
    private lateinit var tvUbicacion: TextView
    private lateinit var btnConfirmarGuardar: Button
    private lateinit var btnCancelar: Button
    private lateinit var btnVerMapa: Button

    private var appointment: Appointment? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_confirm_appointment)

        // Obtener ID de la cita del intent
        val appointmentId = intent.getIntExtra("appointment_id", -1)

        if (appointmentId == -1) {
            finish()
            return
        }

        // Inicializar base de datos
        dbHelper = DatabaseHelper(this)

        // Cargar cita
        appointment = dbHelper.getAppointmentById(appointmentId)

        if (appointment == null) {
            finish()
            return
        }

        // Configurar toolbar
        val toolbar = findViewById<com.google.android.material.appbar.MaterialToolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = "Confirmación de Cita"

        // Inicializar vistas
        initViews()

        // Mostrar información de la cita
        mostrarInformacionCita()

        // Configurar listeners
        setupListeners()
    }

    private fun initViews() {
        tvDoctor = findViewById(R.id.tvDoctor)
        tvFecha = findViewById(R.id.tvFecha)
        tvHorario = findViewById(R.id.tvHorario)
        tvUbicacion = findViewById(R.id.tvUbicacion)
        btnConfirmarGuardar = findViewById(R.id.btnConfirmarGuardar)
        btnCancelar = findViewById(R.id.btnCancelar)
        btnVerMapa = findViewById(R.id.btnVerMapa)
    }

    private fun mostrarInformacionCita() {
        appointment?.let { cita ->
            tvDoctor.text = cita.doctorNombre
            tvFecha.text = cita.getFechaFormateada()
            tvHorario.text = cita.getHoraFormateada()
            tvUbicacion.text = cita.ubicacion
        }
    }

    private fun setupListeners() {
        btnConfirmarGuardar.setOnClickListener {
            confirmarYGuardar()
        }

        btnCancelar.setOnClickListener {
            mostrarDialogoCancelacion()
        }

        btnVerMapa.setOnClickListener {
            abrirMapa()
        }
    }

    private fun confirmarYGuardar() {
        appointment?.let { cita ->
            // Mostrar mensaje de éxito
            AlertDialog.Builder(this)
                .setTitle("¡Éxito!")
                .setMessage("Su cita ha sido registrada con éxito")
                .setPositiveButton("Aceptar") { dialog, which ->
                    // Ir a Mis Citas
                    val intent = Intent(this, MyAppointmentsActivity::class.java)
                    startActivity(intent)
                    finishAffinity() // Cerrar todas las actividades anteriores
                }
                .setCancelable(false)
                .show()
        }
    }

    private fun mostrarDialogoCancelacion() {
        AlertDialog.Builder(this)
            .setTitle("Cancelar")
            .setMessage("¿Está seguro de que desea cancelar la confirmación de la cita?")
            .setPositiveButton("Sí") { dialog, which ->
                // Cancelar cita en la base de datos
                appointment?.let { cita ->
                    dbHelper.cancelAppointment(cita.id)
                }
                // Regresar a Agendar Cita
                val intent = Intent(this, BookAppointmentActivity::class.java)
                startActivity(intent)
                finish()
            }
            .setNegativeButton("No", null)
            .show()
    }

    private fun abrirMapa() {
        val gmmIntentUri = Uri.parse("geo:0,0?q=Clínica+Dental+SmileCare,San+Salvador")
        val mapIntent = Intent(Intent.ACTION_VIEW, gmmIntentUri)
        mapIntent.setPackage("com.google.android.apps.maps")

        if (mapIntent.resolveActivity(packageManager) != null) {
            startActivity(mapIntent)
        } else {
            // Si Google Maps no está instalado, abrir en navegador
            val webIntent = Intent(
                Intent.ACTION_VIEW,
                Uri.parse("https://www.google.com/maps/search/Clínica+Dental+SmileCare,+San+Salvador")
            )
            startActivity(webIntent)
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }
}