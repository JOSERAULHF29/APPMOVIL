package com.example.profinal.Servicios

import com.example.profinal.entidades.producto
import com.google.gson.annotations.SerializedName

data class productoresponse(

@SerializedName("lista") var  lisaprodutos:ArrayList<producto>
)
