package sv.edu.udb.smilecare.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import de.hdodenhof.circleimageview.CircleImageView
import sv.edu.udb.smilecare.R
import sv.edu.udb.smilecare.models.Doctor

class DoctorsAdapter(
    private var doctors: List<Doctor>,
    private val onVerDisponibilidadClickListener: (Doctor) -> Unit
) : RecyclerView.Adapter<DoctorsAdapter.DoctorViewHolder>() {

    class DoctorViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val ivDoctorPhoto: CircleImageView = itemView.findViewById(R.id.ivDoctorPhoto)
        val tvDoctorName: TextView = itemView.findViewById(R.id.tvDoctorName)
        val tvSpecialty: TextView = itemView.findViewById(R.id.tvSpecialty)
        val btnVerDisponibilidad: Button = itemView.findViewById(R.id.btnVerDisponibilidad)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DoctorViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_doctor, parent, false)
        return DoctorViewHolder(view)
    }

    override fun onBindViewHolder(holder: DoctorViewHolder, position: Int) {
        val doctor = doctors[position]

        // Cargar imagen del doctor
        Glide.with(holder.itemView.context)
            .load(doctor.fotoUrl)
            .placeholder(R.drawable.ic_doctor_placeholder)
            .error(R.drawable.ic_doctor_placeholder)
            .into(holder.ivDoctorPhoto)

        holder.tvDoctorName.text = doctor.nombre
        holder.tvSpecialty.text = doctor.especialidad

        holder.btnVerDisponibilidad.setOnClickListener {
            onVerDisponibilidadClickListener(doctor)
        }
    }

    override fun getItemCount(): Int = doctors.size


    fun updateDoctors(newDoctors: List<Doctor>) {
        this.doctors = newDoctors.toMutableList()
        notifyDataSetChanged()
    }
}