package com.example.profinal.Activity

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.profinal.Adaptador.adaptcategoria
import com.example.profinal.R
import com.example.profinal.Servicios.RetrofitCliente
import com.example.profinal.databinding.ActivityPagoBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class PAGO : AppCompatActivity() {

    private  lateinit var binding: ActivityPagoBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding=ActivityPagoBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val idventa=intent.getIntExtra("idventa",1)
        asignar(idventa)
        finalizar()

    }
    private fun asignar(id: Int) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val response = RetrofitCliente.webser.obtenerventa(id)

                if (response.isSuccessful) {
                    // Si la respuesta es exitosa, obtenemos el cuerpo
                    val venta = response.body()
                    // Verificar que la respuesta no sea nula
                    if (venta != null) {
                        // Actualizar la UI en el hilo principal
                        withContext(Dispatchers.Main) {
                            //la información obtenida del "Monto" es uno de los campos)
                            binding.labelpgao.text = "${venta.Monto}"

                        }
                    } else {
                        Log.e("===", "No se encontró la venta con id: $id")
                    }
                } else {
                    Log.e("error", "Error en la respuesta: ${response.code()}")  // Si la respuesta no es exitosa
                }
            } catch (e: Exception) {
                Log.e("error", "Error: ${e.message}")  // Manejo de excepciones
            }
        }
    }

    private  fun finalizar()
    {

        binding.btn.setOnClickListener {
            val titular=binding.txttiutlar.text.toString()
            val numero=binding.txttarjeta.text.toString()
            val csv=binding.txtcsv.text.toString()
            val fecha=binding.txtfecha.text.toString()
            if(titular.isEmpty())
            {
                binding.txttiutlar.setError("INGRESE DATO")

            }
            else if(numero.isEmpty() || numero.length<6)
            {
                binding.txttarjeta.setError("INGRESE ")
            }
            else if(csv.isEmpty())
            {
                binding.txtcsv.setError("INGRESE DATO ")
            }else if(fecha.isEmpty())
            {
                binding.txtfecha.setError("INGRESE DATO ")

            }else
            {
                val builder = AlertDialog.Builder(this)
                builder.setMessage("Compra exitosa, ¡Gracias!")  // Mensaje personalizado
                    .setCancelable(false)  // Impide que el usuario cierre el cuadro tocando fuera de él

                    // Si el usuario presiona "Aceptar", se inicia la actividad Home
                    .setPositiveButton("Aceptar") { dialog, id ->
                        // Iniciar la actividad Home
                        val inte = Intent(this, Home::class.java)
                        startActivity(inte)
                        dialog.dismiss()
                        finish()
                    }

                val alert = builder.create()  // Crea el cuadro de diálogo
                alert.show()

            }


        }
        binding.btncancelara.setOnClickListener {
            // Crear un cuadro de diálogo para confirmar la cancelación
            val builder = AlertDialog.Builder(this)
            builder.setMessage("¿Deseas cancelar la compra?")
                .setCancelable(false)
                .setPositiveButton("Sí") { dialog, id ->
                    // Si el usuario presiona "Sí", cerrar la actividad
                    finish()
                }
                .setNegativeButton("No") { dialog, id ->
                    // Si el usuario presiona "No", no hace nada, cierra el diálogo
                    dialog.dismiss()
                }
            val alert = builder.create()
            alert.show()  // Mostrar el cuadro de diálogo
        }


    }







}