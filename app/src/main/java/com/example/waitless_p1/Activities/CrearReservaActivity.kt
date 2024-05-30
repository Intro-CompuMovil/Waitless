package com.example.waitless_p1.Activities

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.example.waitless_p1.Data.Reserva
import com.example.waitless_p1.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import java.text.SimpleDateFormat
import java.util.*

class CrearReservaActivity : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth
    private lateinit var database: FirebaseDatabase
    private lateinit var myRef: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_crear_reserva)

        auth = FirebaseAuth.getInstance()
        database = FirebaseDatabase.getInstance()

        val parque = intent.getStringExtra("parque")
        val atraccion = intent.getStringExtra("atraccion")
        val idAtraccion = intent.getIntExtra("idAtraccion", 0)
        Log.d("CrearReservaActivity", "Parque: $parque, Atracción: $atraccion, idAtracción: $idAtraccion")

        if (parque != null && atraccion != null) {
            findViewById<TextView>(R.id.escoParque).text = "Parque: $parque"
            findViewById<TextView>(R.id.tipoAtraccion).text = "Atracción: $atraccion"
        } else {
            Toast.makeText(this, "Datos no recibidos correctamente", Toast.LENGTH_LONG).show()
        }

        findViewById<Button>(R.id.reservar).setOnClickListener {
            createReservation(parque, atraccion, idAtraccion)
        }

        setupButtons()
    }

    private fun setupButtons() {
        findViewById<ImageButton>(R.id.iconoReserva).setOnClickListener {
            startActivity(Intent(this, ReservasActivity::class.java))
        }
        findViewById<ImageButton>(R.id.iconoTicket).setOnClickListener {
            startActivity(Intent(this, TicketsActivity::class.java))
        }
        findViewById<ImageButton>(R.id.iconoHome).setOnClickListener {
            startActivity(Intent(this, HomeActivity::class.java))
        }
        findViewById<ImageButton>(R.id.iconoQR).setOnClickListener {
            startActivity(Intent(this, PermisoCamaraActivity::class.java))
        }
        findViewById<ImageButton>(R.id.iconoMapa).setOnClickListener {
            startActivity(Intent(this, MapaActivity::class.java))
        }
        findViewById<ImageButton>(R.id.iconoPerfil).setOnClickListener {
            startActivity(Intent(this, PerfilActivity::class.java))
        }
    }

    private fun createReservation(parque: String?, atraccion: String?, idAtraccion: Int?) {
        val user = auth.currentUser
        if (user != null && parque != null && atraccion != null && idAtraccion != null) {
            val spinner = findViewById<Spinner>(R.id.numPuestos)
            val asientos = spinner.selectedItem.toString().toInt()
            val fecha = findViewById<CalendarView>(R.id.calendarView).date
            val formattedDate = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date(fecha))
            val hora = SimpleDateFormat("HH:mm:ss", Locale.getDefault()).format(Date())

            val reserva = Reserva(
                numero = generateReservationNumber(),
                rParque = parque,
                rAtraccion = atraccion,
                rAId = idAtraccion,
                rEstado = true,
                hora = hora,
                fecha = formattedDate,
                asientos = asientos,
                titular = user.uid
            )


            val reservasPath = "reservas/${user.uid}/${reserva.numero}"
            myRef = database.getReference(reservasPath)
            myRef.setValue(reserva)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        Log.d("FirebaseCreateReservation", "createReservation:onComplete:" + task.isSuccessful)
                        Toast.makeText(this, "Reserva creada exitosamente", Toast.LENGTH_LONG).show()
                        startActivity(Intent(this, ReservasActivity::class.java))
                    } else {
                        Toast.makeText(this, "Error al crear la reserva: " + task.exception.toString(), Toast.LENGTH_SHORT).show()
                        task.exception?.message?.let { Log.e("FirebaseCreateReservation", it) }
                    }
                }
        } else {
            Toast.makeText(this, "No se pudo crear la reserva", Toast.LENGTH_LONG).show()
        }
    }

    private fun generateReservationNumber(): Int {
        return (1000..9999).random()
    }
}
