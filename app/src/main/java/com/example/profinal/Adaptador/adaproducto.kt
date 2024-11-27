package com.example.profinal.Adaptador
import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.example.profinal.R
import com.example.profinal.Servicios.RetrofitCliente
import com.example.profinal.entidades.carrito
import com.example.profinal.entidades.clientes
import com.example.profinal.entidades.producto
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.coroutineScope

import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class adaproducto(private val prod: List<producto>) :
    RecyclerView.Adapter<adaproducto.productoViewHolder>() {


    inner class productoViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private var labenom=itemView.findViewById<TextView>(R.id.labelprueva)
        private var labepre=itemView.findViewById<TextView>(R.id.labelprecio)
        private var labelimagen=itemView.findViewById<ImageView>(R.id.imagenprueba)
        private var agregar=itemView.findViewById<Button>(R.id.btnagregar)


        fun agregar1(producto1: producto)
        {
            val sharedPref = itemView.context.getSharedPreferences("MiApp", Context.MODE_PRIVATE)
            val idCliente = sharedPref.getInt("idCliente", -1)

             agregar.setOnClickListener {

                 val carr=carrito(
                     idcarrito = 0,
                     idProducto = producto1.idProducto,
                     idCliente = idCliente,
                     cantidad = 1,
                     estado = 1
                 )
                 CoroutineScope(Dispatchers.IO).launch {

                     val rpta = RetrofitCliente.webser.insertacarro(carr)
                     withContext(Dispatchers.Main) {
                         if (rpta.isSuccessful) {
                             // Aquí puedes actualizar la UI si la respuesta fue exitosa
                             Toast.makeText(itemView.context, "Producto agregado al carrito", Toast.LENGTH_SHORT).show()
                         } else {
                             // Mostrar un mensaje de error si la respuesta no fue exitosa
                             Toast.makeText(itemView.context, "Error: ${rpta.message()}", Toast.LENGTH_SHORT).show()
                         }
                     }
                 }

             }
        }
        fun rellenar(producto: producto)
        {

            labenom.text=producto.nombre
            labepre.text= "S/${producto.precio}"


            //con esto obtengo el nombre de la imagen y elimino la extension
            val ima=producto.imagen.substringBeforeLast(".")
            // con esto relaciono con el ya que el comando getidentifier busca solo por el nombre sin la extension
            val imid=itemView.context.resources.getIdentifier(ima,"drawable",itemView.context.packageName)

          if(imid!=0){
              labelimagen.setImageResource(imid)
          }
            else{
                labelimagen.setImageResource(R.drawable.perro)
          }



        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): productoViewHolder{
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.productoholder, parent, false)
        return productoViewHolder(view)
    }

    override fun onBindViewHolder(holder: productoViewHolder, position: Int) {
        val listasa=prod[position]
        holder.rellenar(listasa)
        holder.agregar1(listasa)







    }

    override fun getItemCount(): Int = prod.size
}
