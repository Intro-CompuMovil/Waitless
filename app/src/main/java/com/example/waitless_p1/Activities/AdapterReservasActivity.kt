package com.example.waitless_p1.Activities

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.example.waitless_p1.Data.Reserva
import com.example.waitless_p1.R
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import de.hdodenhof.circleimageview.CircleImageView
import java.io.File

class AdapterReservasActivity(
    private var reservas: List<Reserva>,
    private val context: Context
) : RecyclerView.Adapter<AdapterReservasActivity.ProfileViewHolder>() {

    inner class ProfileViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val profileImage1: CircleImageView = itemView.findViewById(R.id.imagenAtraccion)
        private val fecha: TextView = itemView.findViewById(R.id.fechaReserva)
        private val detalles: Button = itemView.findViewById(R.id.verDetallesReserva)
        private val cancelar: Button = itemView.findViewById(R.id.cancelarReserva)

        fun bind(reserva: Reserva) {
            fecha.text = reserva.fecha
            downloadProfileImage(reserva.rAtraccion, profileImage1)

            detalles.setOnClickListener {
                val intent = Intent(context, VerReservaActivity::class.java).apply {
                    putExtra("reserva", reserva)
                }
                context.startActivity(intent)
            }

            cancelar.setOnClickListener {
                val database = FirebaseDatabase.getInstance()
                val reservaRef = database.getReference("reservas/${reserva.titular}/${reserva.numero}")
                reservaRef.child("rEstado").setValue(false)
                    .addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            Toast.makeText(context, "Reserva cancelada", Toast.LENGTH_SHORT).show()
                        } else {
                            Toast.makeText(context, "Error al cancelar la reserva", Toast.LENGTH_SHORT).show()
                        }
                    }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProfileViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.adapter_reservas, parent, false)
        return ProfileViewHolder(view)
    }

    override fun onBindViewHolder(holder: ProfileViewHolder, position: Int) {
        holder.bind(reservas[position])
    }

    override fun getItemCount(): Int {
        return reservas.size
    }

    fun updateData(newReservas: List<Reserva>) {
        reservas = newReservas
        notifyDataSetChanged()
    }

    private fun downloadProfileImage(atraccionId: String, profileImage1: CircleImageView) {
        val storage = FirebaseStorage.getInstance("gs://waitless-5a296.appspot.com")
        val localFile = File.createTempFile("images", "jpg")
        val imageRef = storage.reference.child("images/attractions/$atraccionId.jpg")
        imageRef.getFile(localFile).addOnSuccessListener {
            profileImage1.setImageURI(Uri.fromFile(localFile))
            Log.i("DownloadFile", "Successfully downloaded image")
        }.addOnFailureListener { exception ->
            Log.e("DownloadFile", "Error downloading image", exception)
        }
    }
}
