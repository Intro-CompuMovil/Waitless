package com.example.waitless_p1.Activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageButton
import com.example.waitless_p1.R

class DetallePuntoActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detalle_punto)

        val exitButton:ImageButton = findViewById(R.id.exitbutton2)

        exitButton.setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }
    }
}