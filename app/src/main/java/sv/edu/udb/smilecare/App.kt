package sv.edu.udb.smilecare

import android.app.Application
import sv.edu.udb.smilecare.database.DatabaseHelper
import sv.edu.udb.smilecare.services.NotificationService
import sv.edu.udb.smilecare.utils.SharedPrefsManager

class SmileCareApp : Application() {

    companion object {
        lateinit var instance: SmileCareApp
            private set
    }

    lateinit var databaseHelper: DatabaseHelper
    lateinit var sharedPrefs: SharedPrefsManager
    lateinit var notificationService: NotificationService

    override fun onCreate() {
        super.onCreate()
        instance = this

        // Inicializar componentes
        initializeComponents()
    }

    private fun initializeComponents() {
        // Base de datos
        databaseHelper = DatabaseHelper(this)

        // Shared Preferences
        sharedPrefs = SharedPrefsManager(this)

        // Servicio de notificaciones
        notificationService = NotificationService(this)

        // Inicializar datos de muestra si es primera ejecución
        if (sharedPrefs.isFirstRun()) {
            sharedPrefs.setFirstRunCompleted()
        }
    }


    override fun onTerminate() {
        // Limpiar recursos
        notificationService.cancelAllNotifications()
        super.onTerminate()
    }
}