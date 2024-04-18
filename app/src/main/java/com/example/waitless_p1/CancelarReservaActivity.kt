package com.example.waitless_p1

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.ImageButton
import android.widget.Toast

class CancelarReservaActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_cancelar_reserva)

        findViewById<Button>(R.id.cancReserva).setOnClickListener{
            val intent = Intent (this, ReservasCanceladas::class.java)
            startActivity(intent)
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
            startActivity(Intent(this,PermisoCamaraActivity::class.java))
        }
        findViewById<ImageButton>(R.id.iconoMapa).setOnClickListener{
            startActivity(Intent(this,MapaActivity::class.java))
        }
        findViewById<ImageButton>(R.id.iconoPerfil).setOnClickListener{
            startActivity(Intent(this,PerfilActivity::class.java))
        }
        findViewById<ImageButton>(R.id.tema).setOnClickListener{
            Toast.makeText(this, "Sensor tema", Toast.LENGTH_LONG).show()
        }
    }
}
