package com.example.waitless_p1.Activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import com.example.waitless_p1.R

class CrearReservaActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_crear_reserva)

        // Obtener datos del Intent
        val parque = intent.getStringExtra("parque")
        val atraccion = intent.getStringExtra("atraccion")

        // Log para depuración
        Log.d("CrearReservaActivity", "Parque: $parque, Atracción: $atraccion")

        // Verificar que los datos no sean nulos y luego actualizar TextViews
        if (parque != null && atraccion != null) {
            findViewById<TextView>(R.id.escoParque).text = "Parque: $parque"
            findViewById<TextView>(R.id.tipoAtraccion).text = "Atracción: $atraccion"
        } else {
            // Log para depuración
            Toast.makeText(this, "Datos no recibidos correctamente", Toast.LENGTH_LONG).show()
        }

        // Configurar listeners para los botones
        findViewById<Button>(R.id.reservar).setOnClickListener{
            startActivity(Intent(this, ReservasActivity::class.java))
        }

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


    }

}
