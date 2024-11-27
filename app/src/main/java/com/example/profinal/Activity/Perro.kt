package com.example.profinal.Activity

import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.profinal.Adaptador.adaproducto
import com.example.profinal.Adaptador.adaptcategoria
import com.example.profinal.R
import com.example.profinal.Servicios.RetrofitCliente
import com.example.profinal.databinding.ActivityPerroBinding
import com.example.profinal.entidades.carrito
import com.example.profinal.entidades.categoria
import com.example.profinal.entidades.producto
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class Perro : AppCompatActivity() {

    private  lateinit var binding: ActivityPerroBinding
    private  lateinit var adap:adaproducto


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding=ActivityPerroBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.viewperro.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        val sel=intent.getIntExtra("cat",1)
        cargar(sel)





    }
    private fun cargar(categoria:Int) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val response = RetrofitCliente.webser.getproducto()
                if (response.isSuccessful) {
                    val producto = response.body() ?: emptyList()
                    val productofiltr=producto.filter { it.idcategoria==categoria}
                    withContext(Dispatchers.Main) {
                        if(productofiltr.isNotEmpty())
                        {
                        adap= adaproducto(productofiltr)  // Actualizamos el adaptador con las nuevas categorías
                        binding.viewperro.adapter = adap }
                        else {
                            Log.d("Producto", "No hay productos para la categoría 1.")
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