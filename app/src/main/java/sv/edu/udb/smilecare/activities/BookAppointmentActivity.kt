package sv.edu.udb.smilecare.activities

import android.content.Intent
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import sv.edu.udb.smilecare.R
import sv.edu.udb.smilecare.database.DatabaseHelper
import sv.edu.udb.smilecare.models.Doctor
import sv.edu.udb.smilecare.models.Availability
import java.text.SimpleDateFormat
import java.util.*

class BookAppointmentActivity : AppCompatActivity() {

    private lateinit var dbHelper: DatabaseHelper
    private lateinit var auth: FirebaseAuth

    private lateinit var spinnerDoctores: Spinner
    private lateinit var tvDoctorSeleccionado: TextView
    private lateinit var tvEspecialidadSeleccionada: TextView
    private lateinit var spinnerFechas: Spinner
    private lateinit var spinnerHorarios: Spinner
    private lateinit var btnConfirmar: Button
    private lateinit var btnCancelar: Button

    private var doctorSeleccionado: Doctor? = null
    private var fechaSeleccionada: String = ""
    private var horarioSeleccionado: Availability? = null
    private val disponibilidades = mutableListOf<Availability>()
    private val fechasDisponibles = mutableListOf<String>()
    private val horariosDisponibles = mutableListOf<Availability>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_book_appointment)

        // Inicializar Firebase Auth
        auth = FirebaseAuth.getInstance()

        // Inicializar base de datos
        dbHelper = DatabaseHelper(this)

        // DEBUG: Verificar contenido de la base de datos
        dbHelper.debugDatabaseContents()
        dbHelper.debugDisponibilidades() // ← AGREGAR ESTA LÍNEA

        // Configurar toolbar
        val toolbar = findViewById<com.google.android.material.appbar.MaterialToolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        toolbar.navigationIcon?.setBounds(0, 100, 0, 0)

        supportActionBar?.title = "Agendar Cita"

        // Inicializar vistas
        initViews()

        // Cargar doctores
        loadDoctores()

        // Configurar listeners
        setupListeners()
    }

    private fun initViews() {
        spinnerDoctores = findViewById(R.id.spinnerDoctores)
        tvDoctorSeleccionado = findViewById(R.id.tvDoctorSeleccionado)
        tvEspecialidadSeleccionada = findViewById(R.id.tvEspecialidadSeleccionada)
        spinnerFechas = findViewById(R.id.spinnerFechas)
        spinnerHorarios = findViewById(R.id.spinnerHorarios)
        btnConfirmar = findViewById(R.id.btnConfirmar)
        btnCancelar = findViewById(R.id.btnCancelar)
    }

    private fun loadDoctores() {
        val doctores = dbHelper.getAllDoctores()

        // AGREGAR "Seleccione un doctor" como primer elemento
        val nombresDoctores = mutableListOf("Seleccione un doctor")
        nombresDoctores.addAll(doctores.map { it.nombre })

        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, nombresDoctores)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerDoctores.adapter = adapter

        // Asegurar que empiece en "Seleccione un doctor"
        spinnerDoctores.setSelection(0, false)
    }

    private fun setupListeners() {
        spinnerDoctores.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: android.view.View?, position: Int, id: Long) {
                val doctores = dbHelper.getAllDoctores()
                if (position > 0) { // Posición 0 es "Seleccione un doctor"
                    doctorSeleccionado = doctores[position - 1] // Restar 1 porque el hint ocupa posición 0
                    mostrarInformacionDoctor()
                    cargarFechasDisponibles()
                } else {
                    doctorSeleccionado = null
                    limpiarSeleccion()
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                doctorSeleccionado = null
                limpiarSeleccion()
            }
        }

        spinnerFechas.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: android.view.View?, position: Int, id: Long) {
                if (position > 0 && fechasDisponibles.isNotEmpty()) { // Posición 0 es "Seleccione una fecha"
                    fechaSeleccionada = fechasDisponibles[position - 1] // Restar 1 por el hint
                    println("DEBUG: Fecha seleccionada: $fechaSeleccionada")
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
                if (position > 0 && horariosDisponibles.isNotEmpty()) { // Posición 0 es "Seleccione un horario"
                    horarioSeleccionado = horariosDisponibles[position - 1] // Restar 1 por el hint
                    println("DEBUG: Horario seleccionado: ${horarioSeleccionado?.horaInicio} - ${horarioSeleccionado?.horaFin}")
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
            confirmarCita()
        }

        btnCancelar.setOnClickListener {
            mostrarDialogoCancelacion()
        }
    }

    private fun mostrarInformacionDoctor() {
        doctorSeleccionado?.let { doctor ->
            tvDoctorSeleccionado.text = doctor.nombre
            tvEspecialidadSeleccionada.text = doctor.especialidad
        }
    }

    private fun cargarFechasDisponibles() {
        doctorSeleccionado?.let { doctor ->
            disponibilidades.clear()
            fechasDisponibles.clear()

            println("DEBUG: Cargando fechas para doctor: ${doctor.nombre} (ID: ${doctor.id})")

            val todasDisponibilidades = dbHelper.getDisponibilidadesByDoctor(doctor.id)

            println("DEBUG: Se obtuvieron ${todasDisponibilidades.size} disponibilidades")

            disponibilidades.addAll(todasDisponibilidades)

            // Obtener fechas únicas
            val fechasUnicas = todasDisponibilidades.map { it.fecha }.distinct().sorted()
            fechasDisponibles.addAll(fechasUnicas)

            println("DEBUG: Fechas únicas encontradas: $fechasUnicas")

            // Actualizar spinner de fechas - CORREGIDO
            val fechasFormateadas = fechasUnicas.map { formatearFecha(it) }
            val adapterFechas = ArrayAdapter(this, android.R.layout.simple_spinner_item,
                mutableListOf<String>().apply {
                    add("Seleccione una fecha")
                    addAll(fechasFormateadas)
                })
            adapterFechas.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            spinnerFechas.adapter = adapterFechas

            // LIMPIAR selección actual
            spinnerFechas.setSelection(0, false)
            fechaSeleccionada = ""

            limpiarHorarios() // Limpiar horarios cuando cambia el doctor

        } ?: run {
            println("DEBUG: No hay doctor seleccionado")
            limpiarFechasYHorarios()
        }
    }

    private fun cargarHorariosDisponibles() {
        doctorSeleccionado?.let { doctor ->
            if (fechaSeleccionada.isNotEmpty()) {
                horariosDisponibles.clear()

                val horariosParaFecha = disponibilidades.filter {
                    it.fecha == fechaSeleccionada
                }.sortedBy { it.horaInicio }

                horariosDisponibles.addAll(horariosParaFecha)

                println("DEBUG: Horarios para $fechaSeleccionada: ${horariosParaFecha.size}")

                // Actualizar spinner de horarios - CORREGIDO
                val horariosFormateados = horariosParaFecha.map {
                    "${it.horaInicio} - ${it.horaFin}"
                }

                val adapterHorarios = ArrayAdapter(this, android.R.layout.simple_spinner_item,
                    mutableListOf<String>().apply {
                        add("Seleccione un horario")
                        addAll(horariosFormateados)
                    })
                adapterHorarios.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                spinnerHorarios.adapter = adapterHorarios

                // LIMPIAR selección actual
                spinnerHorarios.setSelection(0, false)
                horarioSeleccionado = null

                // Mostrar mensaje si no hay horarios (aunque según logs sí hay)
                if (horariosParaFecha.isEmpty()) {
                    Toast.makeText(this, "No hay horarios disponibles para esta fecha", Toast.LENGTH_SHORT).show()
                }

            } else {
                limpiarHorarios()
            }
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

    private fun limpiarSeleccion() {
        tvDoctorSeleccionado.text = "No seleccionado"
        tvEspecialidadSeleccionada.text = "No seleccionada"
        limpiarFechasYHorarios()
    }

    private fun limpiarFechasYHorarios() {
        // Limpiar spinner de fechas
        val adapterFechas = ArrayAdapter(this, android.R.layout.simple_spinner_item,
            listOf("Seleccione una fecha"))
        adapterFechas.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerFechas.adapter = adapterFechas

        limpiarHorarios()
    }

    private fun limpiarHorarios() {
        // Limpiar spinner de horarios
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

    private fun confirmarCita() {
        val user = auth.currentUser
        if (user == null) {
            Toast.makeText(this, "Error: Usuario no autenticado", Toast.LENGTH_SHORT).show()
            return
        }

        if (doctorSeleccionado == null || fechaSeleccionada.isEmpty() || horarioSeleccionado == null) {
            Toast.makeText(this, "Por favor complete todos los campos", Toast.LENGTH_SHORT).show()
            return
        }

        // Crear objeto cita
        val appointment = sv.edu.udb.smilecare.models.Appointment(
            id = 0,
            pacienteId = user.uid,
            doctorId = doctorSeleccionado!!.id,
            fecha = fechaSeleccionada,
            horaInicio = horarioSeleccionado!!.horaInicio,
            horaFin = horarioSeleccionado!!.horaFin,
            estado = "pendiente",
            motivo = "Consulta dental",
            ubicacion = "Clínica Dental SmileCare, Calle Principal #123, San Salvador"
        )

        // Guardar en base de datos
        val appointmentId = dbHelper.createAppointment(appointment)

        if (appointmentId != -1L) {
            // Ir a pantalla de confirmación
            val intent = Intent(this, ConfirmAppointmentActivity::class.java)
            intent.putExtra("appointment_id", appointmentId.toInt())
            startActivity(intent)
            finish()
        } else {
            Toast.makeText(this, "Error al agendar la cita", Toast.LENGTH_SHORT).show()
        }
    }

    private fun mostrarDialogoCancelacion() {
        AlertDialog.Builder(this)
            .setTitle("Cancelar")
            .setMessage("¿Está seguro de que desea cancelar el agendamiento de la cita?")
            .setPositiveButton("Sí") { dialog, which ->
                // Regresar al MainActivity
                val intent = Intent(this, MainActivity::class.java)
                startActivity(intent)
                finish()
            }
            .setNegativeButton("No", null)
            .show()
    }


    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }
}