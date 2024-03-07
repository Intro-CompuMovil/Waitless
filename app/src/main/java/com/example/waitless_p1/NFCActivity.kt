package com.example.waitless_p1

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.ImageButton

class NFCActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_nfcactivity)

        val exitbutton = findViewById<ImageButton>(R.id.exitbutton)
        exitbutton.setOnClickListener{
            onBackPressedDispatcher.onBackPressed()
        }
    }
}