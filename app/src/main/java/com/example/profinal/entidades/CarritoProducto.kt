package com.example.profinal.entidades

data class CarritoProducto(


    val idCarrito: Int,
    val idCliente: Int,
    val idProducto: Int,
    var cantidad: Int,
    val nombre: String,
    var precio: Int,
    val imagen: String




)
