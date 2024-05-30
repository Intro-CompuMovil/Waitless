package com.example.waitless_p1.Activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.ImageButton
import android.widget.ListView
import android.widget.Spinner
import android.widget.Toast
import com.example.waitless_p1.Data.Atraccion
import com.example.waitless_p1.Data.Entrada
import com.example.waitless_p1.Data.Usuario
import com.example.waitless_p1.R
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class TicketsActivity : AppCompatActivity() {

    private var entradas : MutableList<Entrada> = mutableListOf()
    private var nombresEntradas : MutableList<String> = mutableListOf()
    private var atraccionesPorId : MutableMap<Int, String> = mutableMapOf()

    //Realtime Database
    private val database = FirebaseDatabase.getInstance()
    private lateinit var myRef: DatabaseReference
    val PATH_ENTRADAS="entradas/"
    val PATH_ATTRACTIONS="atracciones/"
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_tickets)

        findViewById<ImageButton>(R.id.iconoReserva).setOnClickListener{
            startActivity(Intent(this, ReservasActivity::class.java))
        }
        findViewById<ImageButton>(R.id.iconoTicket).setOnClickListener{
            startActivity(Intent(this, TicketsActivity::class.java))
        }
        findViewById<ImageButton>(R.id.iconoHome).setOnClickListener{
            startActivity(Intent(this, HomeActivity::class.java))
        }
        findViewById<ImageButton>(R.id.iconoQR).setOnClickListener{
            startActivity(Intent(this, PermisoCamaraActivity::class.java))
        }
        findViewById<ImageButton>(R.id.iconoMapa).setOnClickListener{
            startActivity(Intent(this, MapaActivity::class.java))
        }
        findViewById<ImageButton>(R.id.iconoPerfil).setOnClickListener{
            startActivity(Intent(this, PerfilActivity::class.java))
        }

        loadAttractionsNames()
        //Para pantalla sensor NFC
        val nfc_button = findViewById<Button>(R.id.escNFC)
        nfc_button.setOnClickListener {
            startActivity(Intent(this, NFCActivity::class.java))
        }

    }

    private fun loadEntradas() {
        myRef = database.getReference(PATH_ENTRADAS)
        myRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                for(singleSnapshot in dataSnapshot.children){
                    val entrada = singleSnapshot.getValue(Entrada::class.java)
                    if(entrada != null){
                        entradas.add(entrada)
                        nombresEntradas.add(entrada.nombre)
                    }
                }
                val spinner = findViewById<Spinner>(R.id.tipoEntrada)
                val adapter = ArrayAdapter<String>(this@TicketsActivity, android.R.layout.simple_spinner_item, nombresEntradas)
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                spinner.adapter = adapter

                addValuesToList(spinner);
            }
            override fun onCancelled(databaseError: DatabaseError) {
                Log.w("LOAD_USER", "error en la consulta", databaseError.toException())
            }
        })
    }

    private fun loadAttractionsNames(){
        myRef = database.getReference(PATH_ATTRACTIONS)
        myRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                for(singleSnapshot in dataSnapshot.children){
                    val atraccion = singleSnapshot.getValue(Atraccion::class.java)
                    if(atraccion != null){
                        atraccionesPorId[atraccion.aId] = atraccion.aNombre
                    }
                }
                loadEntradas()
            }
            override fun onCancelled(databaseError: DatabaseError) {
                Log.w("LOAD_USER", "error en la consulta", databaseError.toException())
            }
        })
    }

    private fun addValuesToList(spinner: Spinner){
        val listView = findViewById<ListView>(R.id.Atracciones)
        val atraccionesAdapter = ArrayAdapter<String> (this@TicketsActivity, android.R.layout.simple_list_item_1)
        listView.adapter = atraccionesAdapter

        spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                val atraccionesIds = entradas[position].atracciones
                atraccionesAdapter.clear()
                for (atraccionId in atraccionesIds){
                    atraccionesAdapter.add(atraccionesPorId[atraccionId]!!)
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                atraccionesAdapter.clear()
            }
        }
    }


}
