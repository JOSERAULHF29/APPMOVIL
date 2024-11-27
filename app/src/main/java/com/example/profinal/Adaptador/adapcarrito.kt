package com.example.profinal.Adaptador

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.example.profinal.Activity.carro
import com.example.profinal.R
import com.example.profinal.Servicios.RetrofitCliente
import com.example.profinal.entidades.CarritoProducto
import com.example.profinal.entidades.carrito
import com.example.profinal.entidades.producto
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class adapcarrito(private val carr:MutableList<CarritoProducto>,private val callback: (Int) -> Unit):RecyclerView.Adapter<adapcarrito.carritoviewholder>()
{

    fun calcularSubtotal(): Int {
        var subtotal = 0
        for (producto in carr) {
            subtotal += producto.precio * producto.cantidad
        }
        return subtotal
    }
    inner class carritoviewholder(itemView: View) : RecyclerView.ViewHolder(itemView)
    {
       private var labelnom=itemView.findViewById<TextView>(R.id.labeltituloca)
       private var labelprecio1=itemView.findViewById<TextView>(R.id.labelprecio1)
       private var labelprecio2=itemView.findViewById<TextView>(R.id.labelprecio2)
        private var centro=itemView.findViewById<TextView>(R.id.cantidadlabel)
        private var aum=itemView.findViewById<TextView>(R.id.brnaumentar)
        private var red=itemView.findViewById<TextView>(R.id.btnreducir)
        private  var filaima=itemView.findViewById<ImageView>(R.id.filaimagnecarro)
        private var eliminar=itemView.findViewById<ImageView>(R.id.bntelimar)
        fun llenar(carritoProducto: CarritoProducto) {
            labelnom.text = carritoProducto.nombre
            labelprecio1.text = "S/${carritoProducto.precio}"
            labelprecio2.text = "S/${carritoProducto.precio * carritoProducto.cantidad}"
            centro.text = carritoProducto.cantidad.toString()
            val imagenSinExtension = carritoProducto.imagen.substringBeforeLast(".")
            val imid = itemView.context.resources.getIdentifier(
                imagenSinExtension, "drawable", itemView.context.packageName
            )
            if (imid != 0) {
                filaima.setImageResource(imid)
            } else {
                filaima.setImageResource(R.drawable.perro) // Imagen por defecto
            }
            aum.setOnClickListener {
                CoroutineScope(Dispatchers.IO).launch {
                    val response = RetrofitCliente.webser.incrementarCantidad(carritoProducto)
                    if (response.isSuccessful) {
                        carritoProducto.cantidad += 1
                        withContext(Dispatchers.Main) {
                            centro.text=carritoProducto.cantidad.toString()
                            labelprecio2.text = "S/${carritoProducto.precio * carritoProducto.cantidad}"
                            notifyItemChanged(adapterPosition)
                            callback(calcularSubtotal())

                        }

                    } else {
                        Log.e("Adapter", "Error al incrementar cantidad: ${response.message()}")
                    }
                }
            }
            red.setOnClickListener {
                if (carritoProducto.cantidad >0) {
                    CoroutineScope(Dispatchers.IO).launch {
                        val response = RetrofitCliente.webser.decrementarCantidad(carritoProducto)
                        if (response.isSuccessful) {
                            carritoProducto.cantidad -= 1
                            withContext(Dispatchers.Main) {
                                centro.text=carritoProducto.cantidad.toString()
                                labelprecio2.text = "S/${carritoProducto.precio * carritoProducto.cantidad}"
                                notifyItemChanged(adapterPosition)
                                callback(calcularSubtotal())

                            }
                        } else {
                            Log.e("Adapter", "Error al reducir cantidad: ${response.message()}")
                        }
                    }
                }}

            eliminar.setOnClickListener {
                CoroutineScope(Dispatchers.IO).launch {
                    val response = RetrofitCliente.webser.eliminarProducto(carritoProducto.idCliente,carritoProducto.idProducto)
                    if (response.isSuccessful) {
                        val mensaje = response.body()
                        withContext(Dispatchers.Main) {
                            // Elimina el producto de la lista
                            carr.removeAt(adapterPosition)
                            // Notifica al adaptador que un ítem ha sido removido
                            notifyItemRemoved(adapterPosition)
                            callback(calcularSubtotal())
                        }
                    } else {
                        Log.e("Adapter", "Error al eliminar producto: ${response.message()}")
                    }
                }
            }





        }



    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int):carritoviewholder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.carritoholder, parent, false)
        return carritoviewholder(view)

    }

    override fun onBindViewHolder(holder:carritoviewholder, position: Int) {
        val lis=carr[position]
        holder.llenar(lis)

    }

    override fun getItemCount(): Int =carr.size




}