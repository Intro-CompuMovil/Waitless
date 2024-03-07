package com.example.waitless_p1

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.ImageButton
import android.widget.RelativeLayout
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import org.json.JSONArray
import org.json.JSONObject
import java.io.IOException
import java.io.InputStream

class HomeActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        findViewById<Button>(R.id.rutaParque).setOnClickListener{
            startActivity(Intent(this,PermisoRutaActivity::class.java))
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
        findViewById<Button>(R.id.botonReserva).setOnClickListener{
            startActivity(Intent(this,CrearReservaActivity::class.java))
        }
        findViewById<ImageButton>(R.id.iconoPerfil).setOnClickListener{
            startActivity(Intent(this,LoginActivity::class.java))
        }
        findViewById<ImageButton>(R.id.tema).setOnClickListener{
            Toast.makeText(this, "Sensor cambio tema", Toast.LENGTH_LONG).show()
        }
    }

}
