package sv.edu.udb.smilecare.activities

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import sv.edu.udb.smilecare.R
import sv.edu.udb.smilecare.database.DatabaseHelper
import sv.edu.udb.smilecare.models.Appointment

class AppointmentDetailActivity : AppCompatActivity() {

    private lateinit var dbHelper: DatabaseHelper

    private lateinit var tvDoctor: TextView
    private lateinit var tvEspecialidad: TextView
    private lateinit var tvFecha: TextView
    private lateinit var tvHorario: TextView
    private lateinit var tvEstado: TextView
    private lateinit var tvMotivo: TextView
    private lateinit var tvUbicacion: TextView
    private lateinit var btnVerMapa: Button
    private lateinit var btnVolver: Button

    private var appointment: Appointment? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_appointment_detail)

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
        supportActionBar?.title = "Detalles de Cita"

        // Inicializar vistas
        initViews()

        // Mostrar información de la cita
        mostrarInformacionCita()

        // Configurar listeners
        setupListeners()
    }

    private fun initViews() {
        tvDoctor = findViewById(R.id.tvDoctor)
        tvEspecialidad = findViewById(R.id.tvEspecialidad)
        tvFecha = findViewById(R.id.tvFecha)
        tvHorario = findViewById(R.id.tvHorario)
        tvEstado = findViewById(R.id.tvEstado)
        tvMotivo = findViewById(R.id.tvMotivo)
        tvUbicacion = findViewById(R.id.tvUbicacion)
        btnVerMapa = findViewById(R.id.btnVerMapa)
        btnVolver = findViewById(R.id.btnVolver)
    }

    private fun mostrarInformacionCita() {
        appointment?.let { cita ->
            tvDoctor.text = cita.doctorNombre
            tvEspecialidad.text = cita.doctorEspecialidad
            tvFecha.text = cita.getFechaFormateada()
            tvHorario.text = cita.getHoraFormateada()
            tvEstado.text = cita.getEstadoTexto()
            tvMotivo.text = cita.motivo
            tvUbicacion.text = cita.ubicacion

            // Configurar color del estado
            val estadoColor = resources.getColor(cita.getEstadoColor(), theme)
            tvEstado.setTextColor(estadoColor)
        }
    }

    private fun setupListeners() {
        btnVerMapa.setOnClickListener {
            abrirMapa()
        }

        btnVolver.setOnClickListener {
            onBackPressed()
        }
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