package com.example.waitless_p1


import android.content.Intent
import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.widget.Button
import android.widget.ImageButton
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.waitless_p1.Datos.Companion.MY_PERMISSION_REQUEST_CAMARA
import com.google.zxing.integration.android.IntentIntegrator
import java.io.File
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

class ScanqrActivity : AppCompatActivity() {
    private val CAMERA_PERMISSION_REQUEST_CODE = 123
    private lateinit var cameraExecutor: ExecutorService

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_scanqr)

        cameraExecutor = Executors.newSingleThreadExecutor()

        findViewById<ImageButton>(R.id.iconoReserva).setOnClickListener {
            startActivity(Intent(this, ReservasActivity::class.java))
        }
        findViewById<ImageButton>(R.id.iconoTicket).setOnClickListener {
            startActivity(Intent(this, TicketsActivity::class.java))
        }
        findViewById<ImageButton>(R.id.iconoHome).setOnClickListener {
            startActivity(Intent(this, HomeActivity::class.java))
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
        findViewById<Button>(R.id.botonOK).setOnClickListener {
            startQRScanner()
        }

        findViewById<ImageButton>(R.id.iconoQR).setOnClickListener {
            // Verificar los permisos de la cámara antes de iniciar la actividad de escaneo QR
            if (checkCameraPermission()) {
                // Si ya se tienen los permisos, iniciar la cámara
                startCamera()
            } else {
                // Si no se tienen los permisos, solicitarlos
                requestCameraPermission()
            }
        }
    }

    private fun startQRScanner() {
        IntentIntegrator(this).initiateScan()
    }

    private fun checkCameraPermission(): Boolean {
        return ContextCompat.checkSelfPermission(this, android.Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED
    }

    private fun requestCameraPermission() {
        ActivityCompat.requestPermissions(this, arrayOf(android.Manifest.permission.CAMERA), CAMERA_PERMISSION_REQUEST_CODE)
    }

    private fun startCamera(){
        val permissionCheck = ContextCompat.checkSelfPermission(this, android.Manifest.permission.CAMERA)
        val takePictureIntent =  Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        if (permissionCheck == PackageManager.PERMISSION_GRANTED) {
            if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
                startActivityForResult(takePictureIntent, Datos.REQUEST_IMAGE_CAPTURE);
            } else {
                Toast.makeText(this, "No hay camara", Toast.LENGTH_SHORT).show();
            }

        } else {
            Toast.makeText(this, "No hay permiso", Toast.LENGTH_SHORT).show()
            requestPermissions(arrayOf(android.Manifest.permission.CAMERA),
                Datos.MY_PERMISSION_REQUEST_CAMARA)
        }
    }


    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        // Verificar si los permisos fueron concedidos
        when (requestCode) {
            MY_PERMISSION_REQUEST_CAMARA -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // Si los permisos fueron concedidos, iniciar la cámara
                    startQRScanner()
                } else {
                    // Si los permisos fueron denegados, mostrar un mensaje
                    Toast.makeText(this, "Permiso de cámara denegado", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        cameraExecutor.shutdown()
    }
}
