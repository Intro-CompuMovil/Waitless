package com.example.waitless_p1

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageButton

class DetallePunto : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detalle_punto)

        val exitButton:ImageButton = findViewById(R.id.exitbutton2)

        exitButton.setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }
    }
}