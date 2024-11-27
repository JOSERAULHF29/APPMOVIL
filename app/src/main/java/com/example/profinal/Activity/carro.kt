package com.example.profinal.Activity

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.profinal.Adaptador.adapcarrito
import com.example.profinal.Adaptador.adaptcategoria
import com.example.profinal.R
import com.example.profinal.Servicios.RetrofitCliente
import com.example.profinal.Servicios.responsealmacenventa
import com.example.profinal.databinding.ActivityCarroBinding
import com.example.profinal.databinding.ActivityPerroBinding
import com.example.profinal.entidades.clientes
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class carro : AppCompatActivity() {

    private lateinit var  binding:ActivityCarroBinding
    private  lateinit var carroa:adapcarrito
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding=ActivityCarroBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.viewcarro.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        val sharedPref = getSharedPreferences("MiApp", Context.MODE_PRIVATE)
        val idCliente = sharedPref.getInt("idCliente", -1)
        asignar(idCliente)
        comprar(idCliente)

    }
  //para el holder
  private fun asignar(idCliente: Int) {
      CoroutineScope(Dispatchers.IO).launch {
          try {
              // Llamada a la API para obtener el carrito completo
              val response = RetrofitCliente.webser.getCarritoCompleto(idCliente)
              if (response.isSuccessful) {
                  val carroCompleto = response.body() ?: emptyList()
                  withContext(Dispatchers.Main) {
                      if (carroCompleto.isNotEmpty()) {
                          // Inicializamos el adaptador con los productos del carrito
                          carroa = adapcarrito(carroCompleto.toMutableList()) { subtotal ->
                              calcula(subtotal)  // Actualizamos el subtotal cuando cambian los productos
                          }
                          binding.viewcarro.adapter = carroa
                          calcula(carroa.calcularSubtotal())  // Inicializamos el subtotal
                      }
                  }
              }

          } catch (e: Exception) {
              Log.e("ActivityCarrito", "Error: ${e.message}")
              withContext(Dispatchers.Main) {
              }
          }
      }
  }
    //detalle producto
    private fun calcula(subtotal: Int) {
            binding.totalFeeTxt.text = "S/${subtotal}"
            binding.deliveryTxt.text="S/${Math.round(subtotal*0.10)}"
            binding.taxTxt.text="S/${Math.round(subtotal*0.15)}"
            binding.totalTxt.text="S/${subtotal+subtotal*0.10+subtotal*0.15}"
    }
    private fun comprar(idCliente: Int) {
        binding.btncomprar.setOnClickListener {
            // Ejecutar la operación de compra en segundo plano (en el hilo IO)
            CoroutineScope(Dispatchers.IO).launch {
                try {
                    val ventaRequest = responsealmacenventa(idCliente)
                    val rpta = RetrofitCliente.webser.venta(ventaRequest)
                    withContext(Dispatchers.Main) {
                        if (rpta.isSuccessful) {
                            val idventa = rpta.body()?.toInt();
                            if (idventa != null) {
                                // Asegúrate de que el idventa es válido antes de pasar a la siguiente actividad
                                val intent = Intent(baseContext, PAGO::class.java)
                                intent.putExtra("idventa", idventa)
                                startActivity(intent)
                                finish()
                            } else {
                                Log.e("Error", "idventa no válido")
                            }

                        } else {
                            // Si la respuesta no fue exitosa, mostrar el error
                            Log.e("===", "Error: ${rpta.message()}")
                        }
                    }
                } catch (e: Exception) {
                    // Manejo de excepciones (errores de red, etc.)
                    withContext(Dispatchers.Main) {
                        Toast.makeText(baseContext, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
    }



}





