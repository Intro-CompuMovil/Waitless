package com.example.waitless_p1.Activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.Button
import android.widget.ImageButton
import android.widget.Spinner
import android.widget.Toast
import com.example.waitless_p1.Data.Atraccion
import com.example.waitless_p1.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

class HomeActivity : AppCompatActivity() {

    var item: String? = null
    private lateinit var auth: FirebaseAuth
    private val database = FirebaseDatabase.getInstance()
    private lateinit var myRef: DatabaseReference
    val PATH_USERS="users/"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        findViewById<Button>(R.id.rutaParque).setOnClickListener{
            startActivity(Intent(this, PermisoRutaActivity::class.java))
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
        findViewById<Button>(R.id.botonReserva).setOnClickListener{
            startActivity(Intent(this, CrearReservaActivity::class.java))
        }
        findViewById<ImageButton>(R.id.iconoPerfil).setOnClickListener{
            startActivity(Intent(this, PerfilActivity::class.java))
        }
        findViewById<ImageButton>(R.id.tema).setOnClickListener{
            Toast.makeText(this, "Sensor cambio tema", Toast.LENGTH_LONG).show()
        }

        val parques = findViewById<Spinner>(R.id.parques)
        parques.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parentView: AdapterView<*>,
                selectedItemView: View,
                position: Int,
                id: Long
            ) {
                item = parentView.getItemAtPosition(position).toString()
                val currentUser = auth.currentUser
                if (currentUser != null) {
                    myRef = database.getReference(PATH_USERS+currentUser.uid)
                }
                myRef.get().addOnSuccessListener { dataSnapshot ->
                    // Obtiene el usuario actual de la base de datos
                    val atraccion = dataSnapshot.getValue(Atraccion::class.java)
                    atraccion?.let {
                        // Cambia el estado del usuario
                        it.estado = !it.estado
                        // Actualiza el estado del usuario en la base de datos
                        myRef.setValue(it)
                    }
                }
                // Aquí puedes agregar el código que deseas ejecutar cuando se selecciona un elemento del spinner
            }

            override fun onNothingSelected(parentView: AdapterView<*>?) {
                // Aquí puedes agregar el código que deseas ejecutar cuando no se selecciona ningún elemento del spinner
            }
        }
        findViewById<Button>(R.id.rutaParque).setOnClickListener{
            startActivity(Intent(this, PermisoRutaActivity::class.java).putExtra("nombre", item.toString()))
        }

    }

}
