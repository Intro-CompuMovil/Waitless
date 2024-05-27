package com.example.waitless_p1.Activities

import android.widget.LinearLayout
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.Button
import android.widget.ImageButton
import android.widget.Spinner
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.waitless_p1.Data.Atraccion
import com.example.waitless_p1.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

class HomeActivity : AppCompatActivity() {

    private lateinit var recyclerViewContainer: LinearLayout
    private lateinit var atraccionesPorParque: List<List<Atraccion>>
    private val recyclerViews = mutableListOf<RecyclerView>()
    private val adapters = mutableListOf<AdapterActivity>()
    private var item: String? = null
    private lateinit var auth: FirebaseAuth
    private val database = FirebaseDatabase.getInstance()
    private lateinit var myRef: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        // Setup UI components with listeners
        setupUIComponents()

        // Initialize FirebaseAuth
        auth = FirebaseAuth.getInstance()

        // Initialize RecyclerViewContainer
        recyclerViewContainer = findViewById(R.id.recyclerViewContainer)

        // Setup Spinner item selection
        setupSpinner()
    }

    private fun setupUIComponents() {
        findViewById<Button>(R.id.rutaParque).setOnClickListener {
            startActivity(Intent(this, PermisoRutaActivity::class.java))
        }
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
        findViewById<ImageButton>(R.id.tema).setOnClickListener {
            Toast.makeText(this, "Sensor cambio tema", Toast.LENGTH_LONG).show()
        }
    }

    private fun setupSpinner() {
        val parquesSpinner = findViewById<Spinner>(R.id.parques)
        parquesSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parentView: AdapterView<*>,
                selectedItemView: View,
                position: Int,
                id: Long
            ) {
                item = parentView.getItemAtPosition(position).toString()
                val currentUser = auth.currentUser
                if (currentUser != null) {
                    myRef = database.getReference("atracciones")
                    myRef.get().addOnSuccessListener { dataSnapshot ->
                        val filteredAttractions = dataSnapshot.children.mapNotNull { it.getValue(Atraccion::class.java) }
                            .filter { it.parque == item && it.estado }

                        Log.d("HomeActivity", "Filtered Attractions: ${filteredAttractions.size}")

                        // Update the RecyclerViews and adapters with the filtered attractions
                        actualizarRecyclerViews(filteredAttractions)
                    }
                }
            }

            override fun onNothingSelected(parentView: AdapterView<*>?) {
                // Handle when nothing is selected
            }
        }
    }

    private fun actualizarRecyclerViews(atracciones: List<Atraccion>) {
        // Limpiar vistas anteriores
        recyclerViewContainer.removeAllViews()
        recyclerViews.clear()
        adapters.clear()

        // Agrupar atracciones por cada 4 elementos (puedes ajustar este número según tus necesidades)
        atraccionesPorParque = atracciones.chunked(4)

        // Crear y configurar RecyclerViews y adaptadores dinámicamente
        for (atraccionesChunk in atraccionesPorParque) {
            val recyclerView = RecyclerView(this)
            recyclerView.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
            val adapter = AdapterActivity(atraccionesChunk, { abrirDetalles(it) }, this)
            recyclerView.adapter = adapter

            // Agregar RecyclerView y adaptador a las listas correspondientes
            recyclerViews.add(recyclerView)
            adapters.add(adapter)

            // Agregar RecyclerView al contenedor lineal
            recyclerViewContainer.addView(recyclerView)
        }
    }

    private fun abrirDetalles(atraccion: Atraccion) {
        val intent = Intent(this, CrearReservaActivity::class.java)
        intent.putExtra("parque", atraccion.parque)
        intent.putExtra("atraccion", atraccion.aNombre)
        Log.d("HomeActivity", "Parque: ${atraccion.parque}, Atracción: ${atraccion.aNombre}")

        startActivity(intent)
    }

}
