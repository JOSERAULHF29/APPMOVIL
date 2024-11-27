package com.example.profinal.Activity

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.profinal.R
import com.example.profinal.Servicios.RetrofitCliente
import com.example.profinal.databinding.ActivityCrearcuentaBinding
import com.example.profinal.databinding.ActivityMainBinding
import com.example.profinal.entidades.clientes
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import kotlin.jvm.java
import kotlin.jvm.java as java1

class crearcuenta : AppCompatActivity() {

    private lateinit var binding: ActivityCrearcuentaBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding=ActivityCrearcuentaBinding.inflate(layoutInflater)
        setContentView(binding.root)
        asignar()

    }

    private fun asignar(){

        binding.btncrearcuenta.setOnClickListener {
            registrar()
        }

    }
    private fun registrar()
    {
        val nombre=binding.txtnomnuevo.text.toString()
        val correo=binding.txtusuarionuevo.text.toString()
        val contra=binding.txtcontranueva.text.toString()
        val masc=binding.txtmascota.text.toString()
        if(nombre=="")
        {
            binding.txtnomnuevo.setError("ingrese datos ")
        }
        else if(correo=="")
        {
            binding.txtusuarionuevo.setError("ingrese datos ")
        }
       else if(contra=="")
        {
            binding.txtcontranueva.setError("ingrese datos ")
        }
        else if(contra.length<6)
        {
            binding.txtcontranueva.setError("mayor o igual a 6 caracteres")
        }
       else if(masc=="")
        {
            binding.txtmascota.setError("ingrese datos ")
        }
        else{
            val cliente=clientes(0,nombre,correo,contra,masc)
            CoroutineScope(Dispatchers.IO).launch {
                val rpta= RetrofitCliente.webser.crearcliente(cliente)
                runOnUiThread {
                    if(rpta.isSuccessful){
                        mostrar(rpta.body().toString())
                        binding.txtmascota.setText("")
                        binding.txtnomnuevo.setText("")
                        binding.txtusuarionuevo.setText("")
                        binding.txtcontranueva.setText("")


                    }
                }
            }

        }
    }
    private fun mostrar(mensaje:String)
    {
        val ventana=AlertDialog.Builder(this)
        ventana.setTitle("MENSAJE")
        ventana.setMessage(mensaje)
        ventana.setPositiveButton("Aceptar"){dialog,_ ->
            val int=Intent(this@crearcuenta,login::class.java)
            startActivity(int)
            finish()
        }
        ventana.create().show()



    }



}