package com.example.profinal.Activity

import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.profinal.R
import com.example.profinal.Servicios.RetrofitCliente
import com.example.profinal.databinding.ActivityPerfilBinding
import com.example.profinal.entidades.clientes
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class Perfil : AppCompatActivity() {

    private  lateinit var  binding: ActivityPerfilBinding
    private   var lista=ArrayList<clientes>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding=ActivityPerfilBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val sharedPref = getSharedPreferences("MiApp", Context.MODE_PRIVATE)
        val idCliente = sharedPref.getInt("idCliente", -1)
        cargar(idCliente)


    }
    private fun cargar(id:Int)
    {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val response = RetrofitCliente.webser.get()
                if (response.isSuccessful) {
                    lista = response.body()!!.listacliente
                    val listafiltro=lista.filter { it.idCliente==id }
                    withContext(Dispatchers.Main) {
                        if(listafiltro.isNotEmpty())
                        {
                            binding.perfilnombre.text=listafiltro[0].Nombre
                            binding.perfilcorreo.text=listafiltro[0].Correo
                            binding.perfilcontraseA.text=listafiltro[0].Contraseña
                            binding.perfilmascota.text=listafiltro[0].Mascota


                        }

                    }
                } else {
                    Log.e("HomeActivity", "Error en la respuesta: ${response.code()}")  // Si la respuesta no es exitosa
                }
            } catch (e: Exception) {
                Log.e("HomeActivity", "Error: ${e.message}")  // Manejo de excepciones
            }
        }




    }
}