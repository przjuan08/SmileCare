package sv.edu.udb.smilecare.activities

import android.content.Intent
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import sv.edu.udb.smilecare.R
import sv.edu.udb.smilecare.database.DatabaseHelper
import sv.edu.udb.smilecare.models.Appointment
import sv.edu.udb.smilecare.models.Availability
import java.text.SimpleDateFormat
import java.util.*

class RescheduleAppointmentActivity : AppCompatActivity() {

    private lateinit var dbHelper: DatabaseHelper

    private lateinit var tvDoctor: TextView
    private lateinit var tvCitaActual: TextView
    private lateinit var spinnerFechas: Spinner
    private lateinit var spinnerHorarios: Spinner
    private lateinit var btnConfirmar: Button
    private lateinit var btnCancelar: Button

    private var appointment: Appointment? = null
    private var fechaSeleccionada: String = ""
    private var horarioSeleccionado: Availability? = null
    private val disponibilidades = mutableListOf<Availability>()
    private val fechasDisponibles = mutableListOf<String>()
    private val horariosDisponibles = mutableListOf<Availability>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_reschedule_appointment)

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
        supportActionBar?.title = "Re-agendar Cita"

        // Inicializar vistas
        initViews()

        // Mostrar información de la cita actual
        mostrarInformacionCitaActual()

        // Cargar disponibilidades
        cargarDisponibilidades()

        // Configurar listeners
        setupListeners()
    }

    private fun initViews() {
        tvDoctor = findViewById(R.id.tvDoctor)
        tvCitaActual = findViewById(R.id.tvCitaActual)
        spinnerFechas = findViewById(R.id.spinnerFechas)
        spinnerHorarios = findViewById(R.id.spinnerHorarios)
        btnConfirmar = findViewById(R.id.btnConfirmar)
        btnCancelar = findViewById(R.id.btnCancelar)
    }

    private fun mostrarInformacionCitaActual() {
        appointment?.let { cita ->
            tvDoctor.text = cita.doctorNombre
            tvCitaActual.text = "${cita.getFechaFormateada()} - ${cita.getHoraFormateada()}"
        }
    }

    private fun cargarDisponibilidades() {
        appointment?.let { cita ->
            disponibilidades.clear()
            fechasDisponibles.clear()

            val todasDisponibilidades = dbHelper.getDisponibilidadesByDoctor(cita.doctorId)
            // Filtrar para excluir la cita actual
            disponibilidades.addAll(todasDisponibilidades.filter {
                it.fecha != cita.fecha || it.horaInicio != cita.horaInicio
            })

            // Obtener fechas únicas
            val fechasUnicas = disponibilidades.map { it.fecha }.distinct().sorted()
            fechasDisponibles.addAll(fechasUnicas)

            // Actualizar spinner de fechas
            val fechasFormateadas = fechasUnicas.map { formatearFecha(it) }
            val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item,
                listOf("Seleccione una fecha") + fechasFormateadas)
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            spinnerFechas.adapter = adapter

            deshabilitarBotonConfirmar()
        }
    }

    private fun setupListeners() {
        spinnerFechas.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: android.view.View?, position: Int, id: Long) {
                if (position > 0) {
                    fechaSeleccionada = fechasDisponibles[position - 1]
                    cargarHorariosDisponibles()
                } else {
                    fechaSeleccionada = ""
                    limpiarHorarios()
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                fechaSeleccionada = ""
                limpiarHorarios()
            }
        }

        spinnerHorarios.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: android.view.View?, position: Int, id: Long) {
                if (position > 0) {
                    horarioSeleccionado = horariosDisponibles[position - 1]
                    habilitarBotonConfirmar()
                } else {
                    horarioSeleccionado = null
                    deshabilitarBotonConfirmar()
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                horarioSeleccionado = null
                deshabilitarBotonConfirmar()
            }
        }

        btnConfirmar.setOnClickListener {
            confirmarReagendamiento()
        }

        btnCancelar.setOnClickListener {
            onBackPressed()
        }
    }

    private fun cargarHorariosDisponibles() {
        appointment?.let { cita ->
            horariosDisponibles.clear()

            val horariosParaFecha = disponibilidades.filter {
                it.fecha == fechaSeleccionada
            }.sortedBy { it.horaInicio }

            horariosDisponibles.addAll(horariosParaFecha)

            // Actualizar spinner de horarios
            val horariosFormateados = horariosParaFecha.map {
                "${it.horaInicio} - ${it.horaFin}"
            }
            val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item,
                listOf("Seleccione un horario") + horariosFormateados)
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            spinnerHorarios.adapter = adapter
        }
    }

    private fun formatearFecha(fecha: String): String {
        return try {
            val inputFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
            val outputFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
            val date = inputFormat.parse(fecha)
            outputFormat.format(date)
        } catch (e: Exception) {
            fecha
        }
    }

    private fun limpiarHorarios() {
        val adapterHorarios = ArrayAdapter(this, android.R.layout.simple_spinner_item,
            listOf("Seleccione un horario"))
        adapterHorarios.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerHorarios.adapter = adapterHorarios

        deshabilitarBotonConfirmar()
    }

    private fun habilitarBotonConfirmar() {
        btnConfirmar.isEnabled = true
        btnConfirmar.alpha = 1f
    }

    private fun deshabilitarBotonConfirmar() {
        btnConfirmar.isEnabled = false
        btnConfirmar.alpha = 0.5f
    }

    private fun confirmarReagendamiento() {
        if (appointment == null || fechaSeleccionada.isEmpty() || horarioSeleccionado == null) {
            Toast.makeText(this, "Por favor complete todos los campos", Toast.LENGTH_SHORT).show()
            return
        }

        val success = dbHelper.rescheduleAppointment(
            appointment!!.id,
            fechaSeleccionada,
            horarioSeleccionado!!.horaInicio,
            horarioSeleccionado!!.horaFin
        )

        if (success) {
            AlertDialog.Builder(this)
                .setTitle("¡Éxito!")
                .setMessage("Cita re-agendada exitosamente")
                .setPositiveButton("Aceptar") { dialog, which ->
                    // Regresar a Mis Citas
                    val intent = Intent(this, MyAppointmentsActivity::class.java)
                    startActivity(intent)
                    finish()
                }
                .setCancelable(false)
                .show()
        } else {
            Toast.makeText(this, "Error al re-agendar la cita", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }
}