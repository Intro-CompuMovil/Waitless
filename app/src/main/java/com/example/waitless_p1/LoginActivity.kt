package com.example.waitless_p1

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button

class LoginActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        findViewById<Button>(R.id.login).setOnClickListener{
            startActivity(Intent(this,HomeActivity::class.java))
        }
        findViewById<Button>(R.id.signup).setOnClickListener{
            startActivity(Intent(this,SignupActivity::class.java))
        }
    }
}