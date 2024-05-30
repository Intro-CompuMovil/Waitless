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
import com.example.waitless_p1.Data.Usuario
import com.example.waitless_p1.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class HomeActivity : AppCompatActivity() {

    private lateinit var recyclerViewContainer: LinearLayout
    private lateinit var atraccionesPorParque: List<List<Atraccion>>
    private val recyclerViews = mutableListOf<RecyclerView>()
    private val adapters = mutableListOf<AdapterActivity>()
    private var item: String? = null
    private lateinit var auth: FirebaseAuth
    private val database = FirebaseDatabase.getInstance()
    private lateinit var myRef: DatabaseReference
    private var atracciones = mutableListOf<Atraccion>()

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

        val parques = findViewById<Spinner>(R.id.parques)
        val botonMapa = findViewById<Button>(R.id.rutaParque)
        botonMapa.setOnClickListener {
            val item = parques.selectedItem.toString()
            val intent = Intent(this, RutaActivity::class.java)
            intent.putExtra("parqueSeleccionado", item)
            startActivity(intent)
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

                if(currentUser != null){
                    myRef = database.getReference("atracciones")
                    myRef.addValueEventListener(object : ValueEventListener {
                        override fun onDataChange(dataSnapshot: DataSnapshot) {
                            atracciones.clear()
                            for(child in dataSnapshot.children){
                                val atraccion = child.getValue(Atraccion::class.java)
                                if(atraccion != null && atraccion.estado){
                                    atracciones.add(atraccion)
                                }
                            }
                            actualizarRecyclerViews(atracciones)
                        }
                        override fun onCancelled(databaseError: DatabaseError) {
                            Log.w("LOAD_USER", "error en la consulta", databaseError.toException())
                        }
                    })
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

        // Crear un único RecyclerView
        val recyclerView = RecyclerView(this)
        recyclerView.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        val adapter = AdapterActivity(atracciones, { abrirDetalles(it) }, this)
        recyclerView.adapter = adapter

        // Agregar RecyclerView y adaptador a las listas correspondientes
        recyclerViews.add(recyclerView)
        adapters.add(adapter)

        // Agregar RecyclerView al contenedor lineal
        recyclerViewContainer.addView(recyclerView)
    }

    private fun abrirDetalles(atraccion: Atraccion) {
        val intent = Intent(this, CrearReservaActivity::class.java)
        intent.putExtra("parque", atraccion.parque)
        intent.putExtra("atraccion", atraccion.aNombre)
        intent.putExtra("idAtraccion", atraccion.aId)
        Log.d("HomeActivity", "Parque: ${atraccion.parque}, Atracción: ${atraccion.aNombre}, Id: ${atraccion.aId}")

        startActivity(intent)
    }

}
