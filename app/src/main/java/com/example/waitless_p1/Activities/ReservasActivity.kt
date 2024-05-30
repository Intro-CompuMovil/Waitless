package com.example.waitless_p1.Activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.ImageButton
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.waitless_p1.Data.Reserva
import com.example.waitless_p1.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ktx.getValue

class ReservasActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var reservasAdapter: AdapterReservasActivity
    private var reservasList: List<Reserva> = emptyList()
    private lateinit var auth: FirebaseAuth
    private lateinit var database: FirebaseDatabase
    private lateinit var myRef: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_reservas)

        auth = FirebaseAuth.getInstance()
        database = FirebaseDatabase.getInstance()

        recyclerView = findViewById(R.id.recyclerViewReservas)
        recyclerView.layoutManager = LinearLayoutManager(this)

        // Inicializa el adaptador con una lista vacía y el contexto
        reservasAdapter = AdapterReservasActivity(reservasList, this)
        recyclerView.adapter = reservasAdapter

        setupButtons()
        fetchReservations()
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

    private fun fetchReservations() {
        val currentUser = auth.currentUser
        if (currentUser != null) {
            myRef = database.getReference("reservas/${currentUser.uid}")
            myRef.get().addOnSuccessListener { dataSnapshot ->
                val reservasList = dataSnapshot.children.mapNotNull { it.getValue<Reserva>() }
                    .filter { it.rEstado }
                Log.d("ReservasActivity", "Filtered Reservations: ${reservasList.size}")
                reservasAdapter.updateData(reservasList)
            }.addOnFailureListener { exception ->
                Log.e("ReservasActivity", "Error fetching reservations", exception)
            }
        }
    }
}
