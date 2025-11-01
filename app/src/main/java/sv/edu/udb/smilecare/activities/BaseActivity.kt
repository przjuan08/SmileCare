package sv.edu.udb.smilecare.activities

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import sv.edu.udb.smilecare.R
import sv.edu.udb.smilecare.utils.CustomDialogHelper
import sv.edu.udb.smilecare.utils.LoadingDialog
import sv.edu.udb.smilecare.utils.NetworkUtils

abstract class BaseActivity : AppCompatActivity() {

    protected lateinit var loadingDialog: LoadingDialog
    protected lateinit var customDialogHelper: CustomDialogHelper
    protected var isNetworkAvailable: Boolean = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initializeCommonComponents()
    }

    private fun initializeCommonComponents() {
        loadingDialog = LoadingDialog(this)
        customDialogHelper = CustomDialogHelper(this)
        checkNetworkConnection()
    }

    protected fun checkNetworkConnection() {
        isNetworkAvailable = NetworkUtils.isNetworkAvailable(this)
    }

    protected fun showLoading(message: String = "Cargando...") {
        loadingDialog.show()
    }

    protected fun hideLoading() {
        loadingDialog.dismiss()
    }

    protected fun showToast(message: String, duration: Int = Toast.LENGTH_SHORT) {
        Toast.makeText(this, message, duration).show()
    }

    protected fun showSuccessDialog(
        title: String,
        message: String,
        onPositiveClick: (() -> Unit)? = null
    ) {
        customDialogHelper.showSuccessDialog(title, message, onPositiveClick = onPositiveClick)
    }

    protected fun showErrorDialog(
        title: String = "Error",
        message: String,
        onPositiveClick: (() -> Unit)? = null
    ) {
        customDialogHelper.showErrorDialog(title, message, onPositiveClick = onPositiveClick)
    }

    protected fun showConfirmationDialog(
        title: String,
        message: String,
        onPositiveClick: (() -> Unit)? = null,
        onNegativeClick: (() -> Unit)? = null
    ) {
        customDialogHelper.showConfirmationDialog(
            title,
            message,
            onPositiveClick = onPositiveClick,
            onNegativeClick = onNegativeClick
        )
    }

    protected fun setupToolbar(
        title: String,
        showBackButton: Boolean = true,
        backButtonAction: (() -> Unit)? = null
    ) {
        val toolbar = findViewById<com.google.android.material.appbar.MaterialToolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.title = title
        supportActionBar?.setDisplayHomeAsUpEnabled(showBackButton)

        if (showBackButton && backButtonAction != null) {
            toolbar.setNavigationOnClickListener {
                backButtonAction()
            }
        } else if (showBackButton) {
            toolbar.setNavigationOnClickListener {
                onBackPressed()
            }
        }
    }

    protected fun validateNetworkConnection(): Boolean {
        checkNetworkConnection()
        if (!isNetworkAvailable) {
            showErrorDialog(
                "Sin conexión",
                "No hay conexión a internet. Por favor, verifique su conexión e intente nuevamente."
            )
            return false
        }
        return true
    }

    override fun onResume() {
        super.onResume()
        checkNetworkConnection()
    }
}