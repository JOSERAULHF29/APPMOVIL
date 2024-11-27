package com.example.profinal.Activity

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView.Adapter

import com.example.profinal.R
import com.example.profinal.Servicios.RetrofitCliente
import com.example.profinal.databinding.ActivityHomeBinding
import com.example.profinal.entidades.categoria
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import com.example.profinal.Adaptador.adaptcategoria
import com.example.profinal.entidades.clientes
import kotlinx.coroutines.withContext

class Home : AppCompatActivity() {


    private lateinit var binding: ActivityHomeBinding
    private lateinit var categoriaAdapter: adaptcategoria
    private   var lista=ArrayList<clientes>()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding=ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.viewCategory.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        val sharedPref = getSharedPreferences("MiApp", Context.MODE_PRIVATE)
        val idCliente = sharedPref.getInt("idCliente", -1)
        obtener(idCliente)

        fetchCategorias()
        binding.carritobuton.setOnClickListener {
            val int=Intent(this,carro::class.java)
            startActivity(int)
        }
        //accedo a perfil
        binding.perfilbtn.setOnClickListener {
            val int=Intent(this,Perfil::class.java)
            startActivity(int)
        }
        ubicacion()


    }
    private fun fetchCategorias() {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val response = RetrofitCliente.webser.getcategoria()
                if (response.isSuccessful) {
                    val categorias = response.body() ?: emptyList()  // Si la respuesta es válida, obtenemos la lista
                    withContext(Dispatchers.Main) {
                        categoriaAdapter = adaptcategoria(categorias)  // Actualizamos el adaptador con las nuevas categorías
                        binding.viewCategory.adapter = categoriaAdapter  // Asignamos el adaptador al RecyclerView
                    }
                } else {
                    Log.e("HomeActivity", "Error en la respuesta: ${response.code()}")  // Si la respuesta no es exitosa
                }
            } catch (e: Exception) {
                Log.e("HomeActivity", "Error: ${e.message}")  // Manejo de excepciones
            }
        }
    }
    //obtengo el nombre del cliente
    private fun obtener(id:Int)
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
                            binding.txtclientee.text=listafiltro[0].Nombre
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
    private  fun ubicacion()
    {
     binding.btnmapa.setOnClickListener {

      val inte= Intent(this,mapa::class.java);

         inte.putExtra("latitud",-12.032121338960712)
         inte.putExtra("longitud",-76.9482697334075)
         inte.putExtra("titulo","PETS SHOPS")
         startActivity(inte);
     }







    }






}







