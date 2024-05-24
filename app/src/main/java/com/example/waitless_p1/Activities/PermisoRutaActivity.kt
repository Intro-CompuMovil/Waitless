package com.example.waitless_p1.Activities

import android.content.Intent
import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageButton
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.waitless_p1.Data.Datos.Companion.MY_PERMISSION_REQUEST_LOCATION
import com.example.waitless_p1.R

class PermisoRutaActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_permiso_ruta)

        // Verificar si los permisos de la localización ya se han concedido
        when {
            ContextCompat.checkSelfPermission(
                this, android.Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED -> {
                // Si el permiso de localización ya está concedido, iniciar la actividad RutaActivity
                startActivity(Intent(this, RutaActivity::class.java))
                finish() // Finalizar la actividad actual para que no se pueda volver atrás
            }
            ActivityCompat.shouldShowRequestPermissionRationale(
                this, android.Manifest.permission.ACCESS_FINE_LOCATION
            ) -> {
                // Si se debe explicar al usuario por qué se necesitan los permisos, solicitarlos
                requestPermissions(arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION), MY_PERMISSION_REQUEST_LOCATION)
            }
            else -> {
                // Si no se tienen los permisos, solicitarlos directamente
                requestPermissions(arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION), MY_PERMISSION_REQUEST_LOCATION)
            }
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
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        // Verificar si el permiso de localización fue concedido
        when (requestCode) {
            MY_PERMISSION_REQUEST_LOCATION -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // Si el permiso fue concedido, iniciar la actividad RutaActivity
                    startActivity(Intent(this, RutaActivity::class.java))
                } else {
                    // Si el permiso fue denegado, mostrar un mensaje o tomar alguna acción adecuada
                    startActivity(Intent(this, HomeActivity::class.java))
                    Toast.makeText(this, "Permiso de localización denegado", Toast.LENGTH_SHORT).show()
                }
                // Finalizar la actividad actual para que no se pueda volver atrás
                finish()
            }
        }
    }
}