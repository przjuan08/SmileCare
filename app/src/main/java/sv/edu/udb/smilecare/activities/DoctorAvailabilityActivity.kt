package sv.edu.udb.smilecare.activities

import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import sv.edu.udb.smilecare.R
import sv.edu.udb.smilecare.adapters.DoctorAvailabilityAdapter
import sv.edu.udb.smilecare.database.DatabaseHelper
import sv.edu.udb.smilecare.models.Doctor
import sv.edu.udb.smilecare.models.Availability

class DoctorAvailabilityActivity : AppCompatActivity() {

    private lateinit var dbHelper: DatabaseHelper
    private lateinit var tvDoctorName: TextView
    private lateinit var tvSpecialty: TextView
    private lateinit var rvAvailability: RecyclerView
    private lateinit var btnBack: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_doctor_availability)

        // Inicializar base de datos
        dbHelper = DatabaseHelper(this)

        // Inicializar vistas
        initViews()

        // Obtener doctor ID del intent
        val doctorId = intent.getIntExtra("doctorId", -1)

        if (doctorId == -1) {
            // Si no hay doctorId, mostrar error y cerrar
            finish()
            return
        }

        // Obtener doctor de la base de datos
        val doctor = dbHelper.getDoctorById(doctorId)
        if (doctor != null) {
            setupDoctorInfo(doctor)
            loadAvailability(doctor.id)
        } else {
            finish()
        }
    }

    private fun initViews() {
        tvDoctorName = findViewById(R.id.tvDoctorName)
        tvSpecialty = findViewById(R.id.tvSpecialty)
        rvAvailability = findViewById(R.id.rvCalendar) // Mantener el mismo ID del XML original
        btnBack = findViewById(R.id.btnBack)

        btnBack.setOnClickListener { onBackPressed() }
    }

    private fun setupDoctorInfo(doctor: Doctor) {
        tvDoctorName.text = doctor.nombre
        tvSpecialty.text = doctor.especialidad
    }

    private fun loadAvailability(doctorId: Int) {
        val disponibilidades = dbHelper.getDisponibilidadesByDoctor(doctorId)

        // DEBUG: Ver qué estamos obteniendo
        println("DEBUG: Disponibilidades encontradas para doctor $doctorId: ${disponibilidades.size}")

        // Agrupar por fecha
        val availabilityByDate = disponibilidades.groupBy { it.fecha }

        // Convertir a lista para el adapter
        val availabilityList = availabilityByDate.entries.map { (fecha, disponibilidades) ->
            DoctorAvailability(fecha, disponibilidades)
        }.sortedBy { it.fecha }

        val adapter = DoctorAvailabilityAdapter(availabilityList)
        rvAvailability.layoutManager = LinearLayoutManager(this)
        rvAvailability.adapter = adapter
    }

    data class DoctorAvailability(
        val fecha: String,
        val disponibilidades: List<Availability>
    )
}