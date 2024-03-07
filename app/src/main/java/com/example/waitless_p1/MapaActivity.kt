package com.example.waitless_p1

import android.content.Context
import android.content.Intent
import android.content.pm.ActivityInfo
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Display
import android.view.Surface
import android.view.WindowManager
import android.widget.ImageButton
import android.widget.ImageView

class MapaActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_mapa)

        val pointOnMap:ImageButton = findViewById(R.id.punto4)

        pointOnMap.setOnClickListener {
            startActivity(Intent(this, DetallePunto::class.java))
        }

        //PARA ROTACIÓN
        // Mantener la pantalla en modo horizontal
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE

        // Obtener la rotación actual de la pantalla y rotar la imagen del mapa
        val display: Display = (getSystemService(WINDOW_SERVICE) as WindowManager).defaultDisplay
        val rotation = display.rotation
        rotateMapImage(rotation)
    }

    private fun rotateMapImage(rotation: Int) {
        val imageView = findViewById<ImageView>(R.id.mapImageView)
        when (rotation) {
            Surface.ROTATION_0 -> imageView.rotation = 90f
            Surface.ROTATION_90 -> imageView.rotation = 0f
            Surface.ROTATION_180 -> imageView.rotation = 270f
            Surface.ROTATION_270 -> imageView.rotation = 180f
        }
    }
}