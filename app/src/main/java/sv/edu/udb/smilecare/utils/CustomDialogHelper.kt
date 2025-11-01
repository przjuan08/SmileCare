package sv.edu.udb.smilecare.utils

import android.app.AlertDialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.LayoutInflater
import android.view.Window
import android.widget.Button
import android.widget.TextView
import sv.edu.udb.smilecare.R

class CustomDialogHelper(private val context: Context) {

    fun showSuccessDialog(
        title: String,
        message: String,
        positiveButtonText: String = "Aceptar",
        onPositiveClick: (() -> Unit)? = null
    ) {
        val dialogBuilder = AlertDialog.Builder(context)
        val inflater = LayoutInflater.from(context)
        val dialogView = inflater.inflate(R.layout.dialog_success, null)

        dialogBuilder.setView(dialogView)
        val dialog = dialogBuilder.create()
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)

        val tvTitle = dialogView.findViewById<TextView>(R.id.tvTitle)
        val tvMessage = dialogView.findViewById<TextView>(R.id.tvMessage)
        val btnPositive = dialogView.findViewById<Button>(R.id.btnPositive)

        tvTitle.text = title
        tvMessage.text = message
        btnPositive.text = positiveButtonText

        btnPositive.setOnClickListener {
            onPositiveClick?.invoke()
            dialog.dismiss()
        }

        dialog.setCancelable(false)
        dialog.show()
    }

    fun showErrorDialog(
        title: String,
        message: String,
        positiveButtonText: String = "Aceptar",
        onPositiveClick: (() -> Unit)? = null
    ) {
        val dialogBuilder = AlertDialog.Builder(context)
        val inflater = LayoutInflater.from(context)
        val dialogView = inflater.inflate(R.layout.dialog_error, null)

        dialogBuilder.setView(dialogView)
        val dialog = dialogBuilder.create()
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)

        val tvTitle = dialogView.findViewById<TextView>(R.id.tvTitle)
        val tvMessage = dialogView.findViewById<TextView>(R.id.tvMessage)
        val btnPositive = dialogView.findViewById<Button>(R.id.btnPositive)

        tvTitle.text = title
        tvMessage.text = message
        btnPositive.text = positiveButtonText

        btnPositive.setOnClickListener {
            onPositiveClick?.invoke()
            dialog.dismiss()
        }

        dialog.setCancelable(false)
        dialog.show()
    }

    fun showConfirmationDialog(
        title: String,
        message: String,
        positiveButtonText: String = "Sí",
        negativeButtonText: String = "No",
        onPositiveClick: (() -> Unit)? = null,
        onNegativeClick: (() -> Unit)? = null
    ) {
        val dialogBuilder = AlertDialog.Builder(context)
        val inflater = LayoutInflater.from(context)
        val dialogView = inflater.inflate(R.layout.dialog_confirmation, null)

        dialogBuilder.setView(dialogView)
        val dialog = dialogBuilder.create()
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)

        val tvTitle = dialogView.findViewById<TextView>(R.id.tvTitle)
        val tvMessage = dialogView.findViewById<TextView>(R.id.tvMessage)
        val btnPositive = dialogView.findViewById<Button>(R.id.btnPositive)
        val btnNegative = dialogView.findViewById<Button>(R.id.btnNegative)

        tvTitle.text = title
        tvMessage.text = message
        btnPositive.text = positiveButtonText
        btnNegative.text = negativeButtonText

        btnPositive.setOnClickListener {
            onPositiveClick?.invoke()
            dialog.dismiss()
        }

        btnNegative.setOnClickListener {
            onNegativeClick?.invoke()
            dialog.dismiss()
        }

        dialog.setCancelable(true)
        dialog.show()
    }

    fun showLoadingDialog(message: String = "Cargando..."): AlertDialog {
        val dialogBuilder = AlertDialog.Builder(context)
        val inflater = LayoutInflater.from(context)
        val dialogView = inflater.inflate(R.layout.dialog_loading, null)

        dialogBuilder.setView(dialogView)
        val dialog = dialogBuilder.create()
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)

        val tvMessage = dialogView.findViewById<TextView>(R.id.tvMessage)
        tvMessage.text = message

        dialog.setCancelable(false)
        dialog.show()

        return dialog
    }
}