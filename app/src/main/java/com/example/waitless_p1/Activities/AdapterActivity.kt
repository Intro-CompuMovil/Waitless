package com.example.waitless_p1.Activities

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.waitless_p1.Data.Atraccion
import com.example.waitless_p1.R
import com.google.firebase.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.storage
import de.hdodenhof.circleimageview.CircleImageView
import java.io.File

class AdapterActivity(
    private var atracciones: List<Atraccion>,
    private val onClick: (Atraccion) -> Unit,
    private val context: Context
) : RecyclerView.Adapter<AdapterActivity.ProfileViewHolder>() {

    inner class ProfileViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val profileImage1: CircleImageView = itemView.findViewById(R.id.profile_image1)
        private val titulo1: TextView = itemView.findViewById(R.id.tituloAtraccion)
        private val button1: TextView = itemView.findViewById(R.id.buttonReserva)

        fun bind(atraccion: Atraccion) {
            // Set the image resource or use a library like Glide or Picasso for URL images
            // profileImage1.setImageResource(atraccion.imgUrl)
            titulo1.text = atraccion.aNombre

            downloadProfileImage(atraccion.aId, profileImage1)

            itemView.setOnClickListener { onClick(atraccion) }

            button1.setOnClickListener {
                val intent = Intent(context, CrearReservaActivity::class.java)
                intent.putExtra("parque", atraccion.parque)
                intent.putExtra("atraccion", atraccion.aNombre)
                Log.d("AdapterActivity", "Parque: ${atraccion.parque}, Atracción: ${atraccion.aNombre}")
                context.startActivity(intent)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProfileViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.adapter_atracciones, parent, false)
        return ProfileViewHolder(view)
    }

    override fun onBindViewHolder(holder: ProfileViewHolder, position: Int) {
        holder.bind(atracciones[position])
    }

    override fun getItemCount(): Int {
        return atracciones.size
    }

    fun updateData(newAtracciones: List<Atraccion>) {
        atracciones = newAtracciones
        notifyDataSetChanged()
    }

    private fun downloadProfileImage(atracttionId : Int,  profileImage1: CircleImageView){
        //Storage
        var storage = Firebase.storage("gs://waitless-5a296.appspot.com")
        val localFile = File.createTempFile("images", "jpg")
        val imageRef = storage.reference.child("images/attractions/${atracttionId}.jpg")
        imageRef.getFile(localFile).addOnSuccessListener { taskSnapshot ->
            profileImage1.setImageURI(Uri.fromFile(localFile))
            Log.i("DownloadFile", "Successfully downloaded image")
        }.addOnFailureListener { exception ->
            Log.e("DownloadFile", "Error downloading image", exception)
        }
    }
}
