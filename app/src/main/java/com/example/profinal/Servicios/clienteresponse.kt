package com.example.profinal.Servicios

import com.example.profinal.entidades.clientes
import com.google.gson.annotations.SerializedName

data class clienteresponse(


    @SerializedName("lista") var listacliente:ArrayList<clientes>,

)
