package com.example.waitless_p1.Activities

import android.content.ContentValues
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.widget.Button
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.waitless_p1.Data.Datos
import com.example.waitless_p1.Data.Datos.Companion.GALLERY_REQUEST_CODE
import com.example.waitless_p1.Data.Usuario
import com.example.waitless_p1.R
import com.example.waitless_p1.databinding.ActivitySignupBinding
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.auth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.storage
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

class SignupActivity : AppCompatActivity() {

    //Auth
    private lateinit var auth: FirebaseAuth
    //Binding
    private lateinit var binding: ActivitySignupBinding
    //Storage
    private lateinit var storage: FirebaseStorage
    //Camera and gallery
    private var selectedImageUri: Uri? = null
    private lateinit var takePictureLauncher: ActivityResultLauncher<Uri>
    private lateinit var cameraExecutor: ExecutorService
    //Realtime Database
    private val database = FirebaseDatabase.getInstance()
    private lateinit var myRef: DatabaseReference
    val PATH_USERS="users/"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignupBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = Firebase.auth
        storage = Firebase.storage("gs://waitless-5a296.appspot.com")

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
                            uploadFile()
                            updateUI(user)
                        }
                    } else {
                        Toast.makeText(this, "createUserWithEmail:Failure: " + task.exception.toString(),
                            Toast.LENGTH_SHORT).show()
                        task.exception?.message?.let { Log.e("FirebaseRegister", it) }
                    }
                }
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

    //ABRIR CÁMARA
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

    private fun updateUI(user: FirebaseUser) {
        val intent = Intent(this, HomeActivity::class.java)
        intent.putExtra("user", user.email)
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        startActivity(intent)
        finish()
    }

    private fun uploadFile() {
        if (selectedImageUri != null) {
            val imageRef = storage.reference.child("images/profile/${auth.currentUser?.uid}/${selectedImageUri?.lastPathSegment}")
            imageRef.putFile(selectedImageUri!!)
                .addOnSuccessListener { taskSnapshot ->
                    Log.i("FBApp", "Successfully uploaded image")
                }
                .addOnFailureListener { exception ->
                    Log.e("FBApp", "Failed to upload image", exception)
                }
        } else {
            Log.e("FBApp", "No image selected")
        }
    }

    // Método para abrir la galería y seleccionar una imagen
    fun openGallery() {
        val intent = Intent(Intent.ACTION_PICK)
        intent.type = "image/*"
        startActivityForResult(intent, GALLERY_REQUEST_CODE)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == GALLERY_REQUEST_CODE && resultCode == RESULT_OK && data != null) {
            // Obtiene la imagen seleccionada por el usuario
            selectedImageUri = data.data
            // Muestra la imagen seleccionada en el ImageView
            binding.imageViewContact.setImageURI(selectedImageUri)

            Toast.makeText(this, "Imagen seleccionada: " + selectedImageUri?.lastPathSegment, Toast.LENGTH_SHORT).show()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        cameraExecutor.shutdown()
    }
}