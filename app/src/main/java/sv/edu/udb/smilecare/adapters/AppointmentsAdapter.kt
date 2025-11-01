package sv.edu.udb.smilecare.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import sv.edu.udb.smilecare.R
import sv.edu.udb.smilecare.models.Appointment

class AppointmentsAdapter(
    private val appointments: List<Appointment>,
    private val onVerDetallesClickListener: (Appointment) -> Unit,
    private val onCancelarClickListener: (Appointment) -> Unit,
    private val onReagendarClickListener: (Appointment) -> Unit
) : RecyclerView.Adapter<AppointmentsAdapter.AppointmentViewHolder>() {

    class AppointmentViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvDoctorName: TextView = itemView.findViewById(R.id.tvDoctorName)
        val tvFecha: TextView = itemView.findViewById(R.id.tvFecha)
        val tvHorario: TextView = itemView.findViewById(R.id.tvHorario)
        val tvEstado: TextView = itemView.findViewById(R.id.tvEstado)
        val btnVerDetalles: Button = itemView.findViewById(R.id.btnVerDetalles)
        val btnCancelar: Button = itemView.findViewById(R.id.btnCancelar)
        val btnReagendar: Button = itemView.findViewById(R.id.btnReagendar)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AppointmentViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_appointment, parent, false)
        return AppointmentViewHolder(view)
    }

    override fun onBindViewHolder(holder: AppointmentViewHolder, position: Int) {
        val appointment = appointments[position]

        holder.tvDoctorName.text = appointment.doctorNombre
        holder.tvFecha.text = appointment.getFechaFormateada()
        holder.tvHorario.text = appointment.getHoraFormateada()

        // Configurar estado con color
        holder.tvEstado.text = appointment.getEstadoTexto()
        val estadoColor = ContextCompat.getColor(holder.itemView.context, appointment.getEstadoColor())
        holder.tvEstado.setTextColor(estadoColor)

        // Configurar listeners de botones
        holder.btnVerDetalles.setOnClickListener {
            onVerDetallesClickListener(appointment)
        }

        holder.btnCancelar.setOnClickListener {
            onCancelarClickListener(appointment)
        }

        holder.btnReagendar.setOnClickListener {
            onReagendarClickListener(appointment)
        }

        // Deshabilitar botones según el estado
        when (appointment.estado) {
            "cancelada", "atendida" -> {
                holder.btnCancelar.isEnabled = false
                holder.btnReagendar.isEnabled = false
                holder.btnCancelar.alpha = 0.5f
                holder.btnReagendar.alpha = 0.5f
            }
            else -> {
                holder.btnCancelar.isEnabled = true
                holder.btnReagendar.isEnabled = true
                holder.btnCancelar.alpha = 1f
                holder.btnReagendar.alpha = 1f
            }
        }
    }

    override fun getItemCount(): Int = appointments.size
}