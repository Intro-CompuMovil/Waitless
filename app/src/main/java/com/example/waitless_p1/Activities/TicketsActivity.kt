package com.example.waitless_p1.Activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.ImageButton
import android.widget.ListView
import android.widget.Spinner
import android.widget.Toast
import com.example.waitless_p1.R

class TicketsActivity : AppCompatActivity() {
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
        findViewById<ImageButton>(R.id.tema).setOnClickListener{
            Toast.makeText(this, "Sensor tema", Toast.LENGTH_LONG).show()
        }

        val spinner = findViewById<Spinner>(R.id.tipoEntrada)
        val listView = findViewById<ListView>(R.id.Atracciones)

        val atraccionesAdapter = ArrayAdapter<String> (this, android.R.layout.simple_list_item_1)
        listView.adapter = atraccionesAdapter

        spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                val entradaSeleccionada = resources.getStringArray(R.array.tiposEntradas)[position]
                val atracciones = atraccionesPorEntrada [entradaSeleccionada]
                atracciones?.let {
                    atraccionesAdapter.clear()
                    atraccionesAdapter.addAll(it)
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                atraccionesAdapter.clear()
            }
        }

        //Para pantalla sensor NFC
        val nfc_button = findViewById<Button>(R.id.escNFC)
        nfc_button.setOnClickListener {
            startActivity(Intent(this, NFCActivity::class.java))
        }

    }

    private val atraccionesPorEntrada = mapOf(
        "Entrada Basica" to listOf("Rueda De La Fortuna", "Montaña Rusa", "Casa De Los Espejos"),
        "Entrada VIP" to listOf("Apocalipsis", "Castillo Del Terror", "Tornado", "Rueda De La Fortuna", "Montaña Rusa"),
        "Entrada PRO" to listOf("Apocalipsis", "Karts", "Castillo Del Terror", "Tornado", "Rueda De La Fortuna", "Montaña Rusa", "Simuladores Virtuales"),
        "Entrada Familiar" to listOf("Rueda De La Fortuna", "Casa De Los Espejos", "Juego Infantiles", "Toboganes")
    )

}
