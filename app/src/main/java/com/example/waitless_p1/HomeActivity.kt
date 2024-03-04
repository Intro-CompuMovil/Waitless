package com.example.waitless_p1

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button

class HomeActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        findViewById<Button>(R.id.iconoReserva).setOnClickListener{
            startActivity(Intent(this,ReservasActivity::class.java))
        }
        findViewById<Button>(R.id.iconoTicket).setOnClickListener{
            startActivity(Intent(this,TicketsActivity::class.java))
        }
        findViewById<Button>(R.id.iconoHome).setOnClickListener{
            startActivity(Intent(this,HomeActivity::class.java))
        }
        findViewById<Button>(R.id.iconoQR).setOnClickListener{
            startActivity(Intent(this,ScanqrActivity::class.java))
        }
        findViewById<Button>(R.id.iconoMapa).setOnClickListener{
            startActivity(Intent(this,MapaActivity::class.java))
        }
        findViewById<Button>(R.id.rutaParque).setOnClickListener{
            startActivity(Intent(this,RutaActivity::class.java))
        }
        findViewById<Button>(R.id.botonReserva).setOnClickListener{
            startActivity(Intent(this,Crear_ReservaActivity::class.java))
        }

    }
}