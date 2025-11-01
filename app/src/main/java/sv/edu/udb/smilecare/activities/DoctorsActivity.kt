package sv.edu.udb.smilecare.activities

import android.content.Intent
import android.os.Bundle
import android.widget.SearchView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import sv.edu.udb.smilecare.R
import sv.edu.udb.smilecare.adapters.DoctorsAdapter
import sv.edu.udb.smilecare.database.DatabaseHelper
import sv.edu.udb.smilecare.models.Doctor

class DoctorsActivity : AppCompatActivity() {

    private lateinit var dbHelper: DatabaseHelper
    private lateinit var rvDoctors: RecyclerView
    private lateinit var searchView: SearchView
    private lateinit var adapter: DoctorsAdapter
    private var allDoctors: List<Doctor> = emptyList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_doctors)

        // Configurar toolbar
        val toolbar = findViewById<com.google.android.material.appbar.MaterialToolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = "Consultar Doctores"

        // Inicializar base de datos
        dbHelper = DatabaseHelper(this)
        // Inicializar vistas
        initViews()

        // Cargar doctores
        loadDoctors()
    }

    private fun initViews() {
        rvDoctors = findViewById(R.id.rvDoctors)
        searchView = findViewById(R.id.searchView)

        // Configurar RecyclerView
        rvDoctors.layoutManager = LinearLayoutManager(this)

        // Configurar SearchView
        setupSearchView()
    }

    private fun setupSearchView() {
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                filterDoctors(newText ?: "")
                return true
            }
        })
    }

    private fun loadDoctors() {
        allDoctors = dbHelper.getAllDoctores()
        adapter = DoctorsAdapter(allDoctors) { doctor ->
            goToDoctorAvailability(doctor)
        }
        rvDoctors.adapter = adapter
    }

    private fun filterDoctors(query: String) {
        val filteredDoctors = if (query.isEmpty()) {
            allDoctors
        } else {
            allDoctors.filter { doctor ->
                doctor.nombre.contains(query, true) ||
                        doctor.especialidad.contains(query, true)
            }
        }
        adapter.updateDoctors(filteredDoctors)
    }

    private fun goToDoctorAvailability(doctor: Doctor) {
        val intent = Intent(this, DoctorAvailabilityActivity::class.java)
        intent.putExtra("doctorId", doctor.id) // Cambiar a pasar solo el ID
        startActivity(intent)
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }
}