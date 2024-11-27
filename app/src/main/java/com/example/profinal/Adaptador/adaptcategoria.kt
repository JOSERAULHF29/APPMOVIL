package com.example.profinal.Adaptador

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.profinal.Activity.Home
import com.example.profinal.Activity.Perro

import com.example.profinal.R
import com.example.profinal.entidades.categoria

class adaptcategoria(private val categorias: List<categoria>) :
    RecyclerView.Adapter<adaptcategoria.CategoriaViewHolder>() {

    inner class CategoriaViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
       private var labenom=itemView.findViewById<TextView>(R.id.txtnommmm)
       private var labelimagen=itemView.findViewById<ImageView>(R.id.imagen)
        fun rellenar(categoria: categoria)
        {

           labenom.text=categoria.nombre
            if(categoria.nombre=="PERRO")
            {
                labelimagen.setImageResource(R.drawable.perro)
            }
            else{
                labelimagen.setImageResource(R.drawable.gato)
            }


        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CategoriaViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.categoriaholder, parent, false)
        return CategoriaViewHolder(view)
    }

    override fun onBindViewHolder(holder: CategoriaViewHolder, position: Int) {
        val listasa=categorias[position]
        holder.rellenar(listasa)
        holder.itemView.setOnClickListener {
            // Obtener el contexto desde el itemView
            val context = holder.itemView.context
            if(listasa.nombre=="PERRO" || listasa.nombre=="GATO")
            {
                val selec=listasa.idcategoria
                val intent = Intent(context,Perro::class.java)
                intent.putExtra("cat",selec)
                context.startActivity(intent) // Iniciar la actividad

            }

        }



    }

    override fun getItemCount(): Int = categorias.size
}
