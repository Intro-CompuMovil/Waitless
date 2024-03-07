package com.example.waitless_p1

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.ImageButton
import android.widget.TextView
import kotlin.random.Random

class CrearReservaActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_crear_reserva)

        findViewById<Button>(R.id.reservar).setOnClickListener{
            startActivity(Intent(this, ReservasActivity::class.java))
        }

        findViewById<ImageButton>(R.id.iconoReserva).setOnClickListener{
            startActivity(Intent(this,ReservasActivity::class.java))
        }
        findViewById<ImageButton>(R.id.iconoTicket).setOnClickListener{
            startActivity(Intent(this,TicketsActivity::class.java))
        }
        findViewById<ImageButton>(R.id.iconoHome).setOnClickListener{
            startActivity(Intent(this,HomeActivity::class.java))
        }
        findViewById<ImageButton>(R.id.iconoQR).setOnClickListener{
            startActivity(Intent(this,ScanqrActivity::class.java))
        }
        findViewById<ImageButton>(R.id.iconoMapa).setOnClickListener{
            startActivity(Intent(this,MapaActivity::class.java))
        }
    }

}