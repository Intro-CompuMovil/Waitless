package com.example.waitless_p1.Activities

import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.ImageButton
import com.example.waitless_p1.Data.Atraccion
import com.example.waitless_p1.Data.Usuario
import com.example.waitless_p1.R
import com.example.waitless_p1.databinding.ActivityDetallePuntoBinding
import com.google.firebase.Firebase
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.storage.storage
import de.hdodenhof.circleimageview.CircleImageView
import java.io.File

class DetallePuntoActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDetallePuntoBinding
    //Realtime Database
    private val database = FirebaseDatabase.getInstance()
    private lateinit var myRef: DatabaseReference
    val PATH_ATTRACTIONS="atracciones/"
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetallePuntoBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val attractionId = intent.getIntExtra("idAtraccion",0)
        Log.i("ATTRACTION_ID", "$attractionId")
        loadAttraction(attractionId)

        val exitButton:ImageButton = findViewById(R.id.exitbutton2)

        exitButton.setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }
    }

    private fun loadAttraction(attractionId : Int) {
        myRef = database.getReference(PATH_ATTRACTIONS)
        val userReference = myRef.child(attractionId.toString())
        userReference.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                Log.i("ATTRACTION", "Entro al onDataChange")
                val attraction = dataSnapshot.getValue(Atraccion::class.java)
                if(attraction != null){
                    binding.tituloAtraccion.setText(attraction.aNombre)
                    binding.descripcionAtraccionInfo.setText(attraction.descripcion)
                    binding.tipoAtraccionInfo.setText(attraction.tipo)
                    binding.estadoAtraccionInfo.setText(if(attraction.estado) "Abierto" else "Cerrado")
                    binding.capacidadAtraccionInfo.setText(attraction.capacidad.toString() + " personas")
                    binding.estaturaMinAtraccionInfo.setText(if(attraction.estaturaMinima != 0) attraction.estaturaMinima.toString() + " cm" else "N/A")
                    binding.estaturaMaxAtraccionInfo.setText(if(attraction.estaturaMaxima != 0) attraction.estaturaMaxima.toString() + " cm" else "N/A")
                    binding.nivelIntensidadAtraccionInfo.setText(attraction.nivelIntensidad)
                    downloadAttractionImage(attraction.aId)
                }
            }
            override fun onCancelled(databaseError: DatabaseError) {
                Log.w("LOAD_USER", "error en la consulta", databaseError.toException())
            }
        })
    }

    private fun downloadAttractionImage(atracttionId : Int){
        //Storage
        var storage = Firebase.storage("gs://waitless-5a296.appspot.com")
        val localFile = File.createTempFile("images", "jpg")
        val imageRef = storage.reference.child("images/attractions/${atracttionId}.jpg")
        imageRef.getFile(localFile).addOnSuccessListener { taskSnapshot ->
            binding.imagenAtraccion.setImageURI(Uri.fromFile(localFile))
            Log.i("DownloadFile", "Successfully downloaded image")
        }.addOnFailureListener { exception ->
            Log.e("DownloadFile", "Error downloading image", exception)
        }
    }

}