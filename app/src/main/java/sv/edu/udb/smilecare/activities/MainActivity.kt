package sv.edu.udb.smilecare.activities

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import sv.edu.udb.smilecare.R
import sv.edu.udb.smilecare.auth.LoginActivity
import com.google.firebase.auth.FirebaseAuth

class MainActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth

    // Views
    private lateinit var tvWelcome: TextView
    private lateinit var btnConsultarDoctores: Button
    private lateinit var btnAgendarCita: Button
    private lateinit var btnMisCitas: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Inicializar Firebase Auth
        auth = FirebaseAuth.getInstance()

        // Verificar autenticación
        if (auth.currentUser == null) {
            goToLogin()
            return
        }

        // Configurar toolbar
        val toolbar = findViewById<com.google.android.material.appbar.MaterialToolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)

        // Inicializar vistas
        initViews()

        // Configurar welcome message
        setupWelcomeMessage()

        // Configurar listeners
        setupClickListeners()
    }

    private fun initViews() {
        tvWelcome = findViewById(R.id.tvWelcome)
        btnConsultarDoctores = findViewById(R.id.btnConsultarDoctores)
        btnAgendarCita = findViewById(R.id.btnAgendarCita)
        btnMisCitas = findViewById(R.id.btnMisCitas)
    }

    private fun setupWelcomeMessage() {
        val user = auth.currentUser
        val welcomeMessage = "¡Bienvenid@, ${user?.displayName ?: "Usuario"}!"
        tvWelcome.text = welcomeMessage
    }

    private fun setupClickListeners() {
        btnConsultarDoctores.setOnClickListener { goToConsultarDoctores() }
        btnAgendarCita.setOnClickListener { goToAgendarCita() }
        btnMisCitas.setOnClickListener { goToMisCitas() }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.main_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.menu_logout -> {
                mostrarDialogoConfirmacionLogout()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun mostrarDialogoConfirmacionLogout() {
        AlertDialog.Builder(this)
            .setTitle("Cerrar Sesión")
            .setMessage("¿Está seguro de que desea cerrar sesión?")
            .setPositiveButton("Sí") { dialog, which ->
                // Cerrar sesión
                logout()
            }
            .setNegativeButton("No", null)
            .show()
    }

    private fun goToConsultarDoctores() {
        val intent = Intent(this, DoctorsActivity::class.java)
        startActivity(intent)
    }

    private fun goToAgendarCita() {
        val intent = Intent(this, BookAppointmentActivity::class.java)
        startActivity(intent)
    }

    private fun goToMisCitas() {
        val intent = Intent(this, MyAppointmentsActivity::class.java)
        startActivity(intent)
    }

    private fun goToLogin() {
        val intent = Intent(this, LoginActivity::class.java)
        startActivity(intent)
        finish()
    }

    private fun logout() {
        auth.signOut()
        goToLogin()
    }

    override fun onResume() {
        super.onResume()
        // Actualizar mensaje de bienvenida por si cambió el nombre
        setupWelcomeMessage()
    }
}