package com.example.waitless_p1.Activities

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.waitless_p1.Data.Reserva
import com.example.waitless_p1.R
import com.google.firebase.Firebase
import com.google.firebase.storage.storage
import de.hdodenhof.circleimageview.CircleImageView
import java.io.File

class VerReservaActivity : AppCompatActivity() {

    private lateinit var imagenParque: ImageView
    private lateinit var iconoMapa: ImageButton
    private lateinit var iconoQR: ImageButton
    private lateinit var iconoHome: ImageButton
    private lateinit var iconoTicket: ImageButton
    private lateinit var iconoReserva: ImageButton
    private lateinit var iconoPerfil: ImageButton

    private lateinit var nombreAtra: TextView
    private lateinit var hora2: TextView
    private lateinit var reservados: TextView
    private lateinit var estado: TextView
    private lateinit var titular: TextView
    private lateinit var numero: TextView
    private lateinit var imagenReservaHora: ImageView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_ver_reserva)

        // Enlazar vistas
        imagenParque = findViewById(R.id.imagenParque)
        iconoMapa = findViewById(R.id.iconoMapa)
        iconoQR = findViewById(R.id.iconoQR)
        iconoHome = findViewById(R.id.iconoHome)
        iconoTicket = findViewById(R.id.iconoTicket)
        iconoReserva = findViewById(R.id.iconoReserva)
        iconoPerfil = findViewById(R.id.iconoPerfil)

        nombreAtra = findViewById(R.id.nombreAtra)
        hora2 = findViewById(R.id.hora2)
        reservados = findViewById(R.id.reservados)
        estado = findViewById(R.id.estado)
        numero = findViewById(R.id.numero)
        imagenReservaHora = findViewById(R.id.imagenReservaHora)

        // Obtener la reserva del intent
        val reserva = intent.getSerializableExtra("reserva") as? Reserva
        reserva?.let { mostrarDetallesReserva(it) }

        // Configurar los botones de navegación
        configurarBotones()
    }

    private fun mostrarDetallesReserva(reserva: Reserva) {
        nombreAtra.text = reserva.rAtraccion
        hora2.text = reserva.hora
        reservados.text = reserva.asientos.toString()
        estado.text = if (reserva.rEstado) "Activa" else "Cancelada"
        numero.text = reserva.numero.toString()

        var image = findViewById<ImageView>(R.id.imagenReservaHora)
        downloadProfileImage(reserva.rAId, image)

        // Aquí puedes actualizar la imagen de la reserva si tienes una URL o referencia a la imagen
        // Por ejemplo, si tienes una URL de Firebase Storage:
        // val storageReference = FirebaseStorage.getInstance().reference.child("path/to/image")
        // Glide.with(this).load(storageReference).into(imagenReservaHora)
    }

    private fun configurarBotones() {
        iconoMapa.setOnClickListener {
            startActivity(Intent(this, MapaActivity::class.java))
        }
        iconoQR.setOnClickListener {
            startActivity(Intent(this, PermisoCamaraActivity::class.java))
        }
        iconoHome.setOnClickListener {
            startActivity(Intent(this, HomeActivity::class.java))
        }
        iconoTicket.setOnClickListener {
            startActivity(Intent(this, TicketsActivity::class.java))
        }
        iconoReserva.setOnClickListener {
            startActivity(Intent(this, ReservasActivity::class.java))
        }
        iconoPerfil.setOnClickListener {
            startActivity(Intent(this, PerfilActivity::class.java))
        }
    }

    private fun downloadProfileImage(atracttionId : Int,  profileImage1: ImageView){
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
