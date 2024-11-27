package com.example.profinal.Activity

import android.app.ProgressDialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.SoftwareKeyboardControllerCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.profinal.R
import com.example.profinal.Servicios.RetrofitCliente
import com.example.profinal.databinding.ActivityIntroBinding
import com.example.profinal.databinding.ActivityMainBinding
import com.example.profinal.entidades.clientes
import com.example.profinal.entidades.producto
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlin.math.log

class login : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private  var  listacliente=ArrayList<clientes>()
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var authStateListener:FirebaseAuth.AuthStateListener
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding=ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        firebaseAuth=Firebase.auth
//crear cuenta
        binding.txtcrear.setOnClickListener {

            val int=Intent(this,crearcuenta::class.java)
            startActivity(int)
        }
        //logear
        binding.btnlogear.setOnClickListener {
                iniciarSesion()

        }
    }
    /*autenticar */
    private fun iniciarSesion() {
        val correo = binding.txtusuario.text.toString().trim()
        val contrasena = binding.txtpassword.text.toString().trim()

        if (correo.isEmpty()) {
            binding.txtusuario.error = "Ingrese usuario"
            return
        }

        if (contrasena.isEmpty()) {
            binding.txtpassword.error = "Ingrese contraseña"
            return
        }

        // Mostrar un indicador de iniciando sesion
        val progressDialog = ProgressDialog(this)
        progressDialog.setMessage("Iniciando sesión...")
        progressDialog.setCancelable(false)
        progressDialog.show()

        // Intentar iniciar sesión con Firebase
        firebaseAuth.signInWithEmailAndPassword(correo, contrasena).addOnCompleteListener { task ->
            progressDialog.dismiss() // Cerrar el indicador de progreso
            if (task.isSuccessful) {
                // Inicia la siguiente actividad de inmediato
                val intent = Intent(this, Home::class.java)
                startActivity(intent)
                finish()

                // Realiza la llamada a la API en segundo plano
                guardarIdCliente(correo, contrasena)
            } else {
                mostrarMensajeError("La contraseña o el usuario son incorrectos.")
            }
        }
    }
    private fun guardarIdCliente(correo: String, contrasena: String) {
        CoroutineScope(Dispatchers.IO).launch {
            val response = RetrofitCliente.webser.obtener(correo, contrasena)
            if (response.isSuccessful) {
                val idCliente = response.body()?.idCliente
                idCliente?.let {
                    // Guardar el ID en SharedPreferences
                    val sharedPref = getSharedPreferences("MiApp", Context.MODE_PRIVATE)
                    sharedPref.edit().putInt("idCliente", it).apply()
                    Log.d("===", "El ID del cliente es: $it")
                }
            } else {
                Log.e("===", "Error al obtener el cliente: ${response.message()}")
            }
        }
    }
    private fun mostrarMensajeError(mensaje: String) {
        AlertDialog.Builder(this)
            .setTitle("MENSAJE")
            .setMessage(mensaje)
            .setPositiveButton("Aceptar") { dialog, _ -> dialog.dismiss() }
            .create()
            .show()
    }


}