package com.example.waitless_p1.Activities

import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.Toast
import com.example.waitless_p1.Data.Usuario
import com.example.waitless_p1.R
import com.example.waitless_p1.databinding.ActivitySignupBinding
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.auth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

class SignupActivity : AppCompatActivity() {

    //Auth
    private lateinit var auth: FirebaseAuth
    private lateinit var binding: ActivitySignupBinding
    //Storage
    private var selectedImageUri: Uri? = null
    //Realtime Database
    private val database = FirebaseDatabase.getInstance()
    private lateinit var myRef: DatabaseReference
    val PATH_USERS="users/"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignupBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = Firebase.auth

        binding.signUp.setOnClickListener {
            val email = binding.correo.text.toString()
            val password = binding.password.text.toString()

            auth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this) { task ->
                    if (task.isSuccessful) {
                        Log.d("FirebaseRegister", "createUserWithEmail:onComplete:" + task.isSuccessful)
                        val user = auth.currentUser
                        if (user != null) {
                            // Update user info
                            //val upcrb = UserProfileChangeRequest.Builder()
                            //upcrb.setDisplayName(binding.editTextName.text.toString() + " " + binding.editTextLastName.text.toString())
                            //upcrb.setPhotoUri(Uri.parse("path/to/pic")) //fake uri, use Firebase Storage
                            //user.updateProfile(upcrb.build())
                            var usuario = Usuario()
                            usuario.uid = user.uid
                            usuario.profileImgUrl = selectedImageUri?.lastPathSegment ?: ""
                            usuario.nombre = binding.nombre.text.toString()
                            usuario.apellido = binding.apellido.text.toString()
                            usuario.telefono = binding.telefono.text.toString()
                            myRef = database.getReference(PATH_USERS+user.uid)
                            myRef.setValue(usuario)
                            updateUI(user)
                            //uploadFile()
                        }
                    } else {
                        Toast.makeText(this, "createUserWithEmail:Failure: " + task.exception.toString(),
                            Toast.LENGTH_SHORT).show()
                        task.exception?.message?.let { Log.e("FirebaseRegister", it) }
                    }
                }
        }
    }

    private fun updateUI(user: FirebaseUser) {
        val intent = Intent(this, HomeActivity::class.java)
        intent.putExtra("user", user.email)
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        startActivity(intent)
    }
}