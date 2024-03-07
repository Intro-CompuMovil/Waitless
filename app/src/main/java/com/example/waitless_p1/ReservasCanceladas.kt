package com.example.waitless_p1

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.ImageButton

class ReservasCanceladas : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_reservas_canceladas)

        findViewById<Button>(R.id.hacerReserva).setOnClickListener{
            startActivity(Intent(this, CrearReservaActivity::class.java))
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