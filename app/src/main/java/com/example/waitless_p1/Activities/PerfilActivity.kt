package com.example.waitless_p1.Activities

import android.app.Activity
import android.content.ContentValues
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.widget.Button
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.waitless_p1.Data.Datos
import com.example.waitless_p1.Data.Usuario
import com.example.waitless_p1.R
import com.example.waitless_p1.databinding.ActivityPerfilBinding
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

class PerfilActivity : AppCompatActivity() {


    //Camera and gallery
    private lateinit var cameraExecutor: ExecutorService
    private var selectedImageUri: Uri? = null
    private lateinit var takePictureLauncher: ActivityResultLauncher<Uri>
    //Binding
    private lateinit var binding: ActivityPerfilBinding
    //Authentication
    private lateinit var auth: FirebaseAuth
    //Realtime Database
    private val database = FirebaseDatabase.getInstance()
    private lateinit var myRef: DatabaseReference
    val PATH_USERS="users/"

    override fun onStart() {
        super.onStart()
        auth = FirebaseAuth.getInstance()
        val currentUser = auth.currentUser
        updateUI(currentUser)
        loadUser()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPerfilBinding.inflate(layoutInflater)
        setContentView(binding.root)

        cameraExecutor = Executors.newSingleThreadExecutor()


        binding.buttonSelectImage.setOnClickListener {
            // Crear un AlertDialog.Builder y establecer el mensaje
            val builder = AlertDialog.Builder(this)
            builder.setTitle("Elige una opción")
            builder.setMessage("¿Quieres abrir la galería o tomar una foto?")

            // Agregar los botones
            builder.setPositiveButton("Abrir galería") { _, _ ->
                openGallery()
            }
            builder.setNegativeButton("Tomar foto") { _, _ ->
                checkCameraPermission()
            }

            // Crear y mostrar el AlertDialog
            val dialog = builder.create()
            dialog.show()
        }


        // Preparar el lanzador para el resultado de tomar foto.
        takePictureLauncher = registerForActivityResult(ActivityResultContracts.TakePicture()) { success ->
            if (success) {
                selectedImageUri?.let {
                    binding.imageViewContact.setImageURI(it)
                }
            }
        }

        binding.cerrarSesion.setOnClickListener {
            auth.signOut()
            updateUI(null)
        }

    }

    private fun updateUI(currentUser: FirebaseUser?) {
        if (currentUser == null) {
            // Navigate to the login activity
            val intent = Intent(this, LoginActivity::class.java)
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            startActivity(intent)
            finish()
        }
    }

    //PERMISOS CÁMARA
    private fun checkCameraPermission() {
        when {
            ContextCompat.checkSelfPermission(
                this, android.Manifest.permission.CAMERA
            ) == PackageManager.PERMISSION_GRANTED -> {
                //Lanzamos la camara
                startCamera()
            }
            ActivityCompat.shouldShowRequestPermissionRationale(
                this, android.Manifest.permission.CAMERA) -> {
                requestPermissions(
                    arrayOf(android.Manifest.permission.CAMERA),
                    Datos.MY_PERMISSION_REQUEST_CAMARA)
            }
            else -> {
                requestPermissions(
                    arrayOf(android.Manifest.permission.CAMERA),
                    Datos.MY_PERMISSION_REQUEST_CAMARA
                )
            }
        }
    }


    private fun startCamera(){
        val permissionCheck = ContextCompat.checkSelfPermission(this, android.Manifest.permission.CAMERA)
        if (permissionCheck == PackageManager.PERMISSION_GRANTED) {
            val values = ContentValues()
            values.put(MediaStore.Images.Media.TITLE, "Foto nueva")
            values.put(MediaStore.Images.Media.DESCRIPTION, "Foto de perfil")
            selectedImageUri = contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values)

            selectedImageUri?.let { uri ->
                takePictureLauncher.launch(uri)
            }

        } else {
            Toast.makeText(this, "No hay permiso", Toast.LENGTH_SHORT).show()
            requestPermissions(arrayOf(android.Manifest.permission.CAMERA),
                Datos.MY_PERMISSION_REQUEST_CAMARA)
        }
    }

    // Método para abrir la galería y seleccionar una imagen
    fun openGallery() {
        val intent = Intent(Intent.ACTION_PICK)
        intent.type = "image/*"
        startActivityForResult(intent, Datos.GALLERY_REQUEST_CODE)
    }


    //PERMISOS CÁMARA
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        // Verificar si los permisos fueron concedidos
        when (requestCode) {
            Datos.MY_PERMISSION_REQUEST_CAMARA -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // Si los permisos fueron concedidos, iniciar la cámara
                    startCamera()
                } else {
                    // Si los permisos fueron denegados, mostrar un mensaje
                    Toast.makeText(this, "Permiso de cámara denegado", Toast.LENGTH_SHORT).show()
                }
                return
            }
            else -> {
                // Ignore all other requests.
            }
        }
    }

    //Despues de seleccionar la imagen de la galería
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == Datos.GALLERY_REQUEST_CODE && resultCode == RESULT_OK && data != null) {
            // Obtiene la imagen seleccionada por el usuario
            selectedImageUri = data.data
            // Muestra la imagen seleccionada en el ImageView
            binding.imageViewContact.setImageURI(selectedImageUri)

            Toast.makeText(this, "Imagen seleccionada: " + selectedImageUri?.lastPathSegment, Toast.LENGTH_SHORT).show()
        }
    }

    fun loadUser() {
        myRef = database.getReference(PATH_USERS)
        val userReference = myRef.child(auth.currentUser!!.uid)
        userReference.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val user = dataSnapshot.getValue(Usuario::class.java)
                if(user != null){
                    binding.telefono.setText(user.telefono)
                    binding.nombre.setText(user.nombre)
                    binding.apellido.setText(user.apellido)
                }
            }
            override fun onCancelled(databaseError: DatabaseError) {
                Log.w("LOAD_USER", "error en la consulta", databaseError.toException())
            }
        })
    }

    override fun onDestroy() {
        super.onDestroy()
        cameraExecutor.shutdown()
    }
}