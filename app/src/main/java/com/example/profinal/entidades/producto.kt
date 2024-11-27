package com.example.profinal.entidades

import java.sql.Date

data class producto(

    var idProducto:Int,
    var idcategoria:Int,
    var nombre:String,
    var precio:Int,
    var imagen:String
)
