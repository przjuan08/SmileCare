package sv.edu.udb.smilecare.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import sv.edu.udb.smilecare.R
import sv.edu.udb.smilecare.activities.DoctorAvailabilityActivity

class DoctorAvailabilityAdapter(
    private val availabilityList: List<DoctorAvailabilityActivity.DoctorAvailability>
) : RecyclerView.Adapter<DoctorAvailabilityAdapter.AvailabilityViewHolder>() {

    class AvailabilityViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvDate: TextView = itemView.findViewById(R.id.tvDate)
        val tvTimes: TextView = itemView.findViewById(R.id.tvTimes)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AvailabilityViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_doctor_availability, parent, false)
        return AvailabilityViewHolder(view)
    }

    override fun onBindViewHolder(holder: AvailabilityViewHolder, position: Int) {
        val availability = availabilityList[position]

        // Formatear fecha
        val formattedDate = formatDate(availability.fecha)
        holder.tvDate.text = formattedDate

        // Formatear horarios
        val times = availability.disponibilidades.joinToString("\n") {
            "• ${it.horaInicio} - ${it.horaFin}"
        }
        holder.tvTimes.text = if (times.isNotEmpty()) times else "No hay horarios disponibles"
    }

    override fun getItemCount(): Int = availabilityList.size

    private fun formatDate(dateStr: String): String {
        return try {
            val parts = dateStr.split("-")
            "${parts[2]}/${parts[1]}/${parts[0]}"
        } catch (e: Exception) {
            dateStr
        }
    }
}