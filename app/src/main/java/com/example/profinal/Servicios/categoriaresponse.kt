package com.example.profinal.Servicios

import com.example.profinal.entidades.categoria

import com.google.gson.annotations.SerializedName

data class categoriaresponse(
    @SerializedName("lista") var listacategoria:ArrayList<categoria>
)
