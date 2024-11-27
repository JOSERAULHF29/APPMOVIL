package com.example.profinal.Servicios

import android.icu.util.TimeUnit
import com.example.profinal.entidades.CarritoProducto
import com.example.profinal.entidades.carrito
import com.example.profinal.entidades.categoria
import com.example.profinal.entidades.clientes
import com.example.profinal.entidades.producto
import com.example.profinal.entidades.venta
import com.google.gson.GsonBuilder
import okhttp3.OkHttpClient
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path
import retrofit2.http.Query


object appconst{

    const val url="http://192.168.1.7:5000"
}
interface clienteservices {

    /*obtener cliente*/
    @GET("/cliente")
    suspend fun  get():Response<clienteresponse>

    /*obtenridclinet*/
    @GET("/obtener")
    suspend fun  obtener(@Query("Correo") Correo:String,@Query("Contraseña") Contraseña:String):Response<id>
    /*introductir datos a cliente*/
    @POST("/cliente/nuevo")
    suspend fun crearcliente(@Body client: clientes):Response<String>
    /*obtener categoria*/
    @GET("/categoria")
    suspend fun getcategoria(): Response<List<categoria>>
    /*obtenre producto*/
    @GET("/producto")
    suspend fun getproducto():Response<List<producto>>
    @GET("/carrito")
    suspend fun  getcarrito():Response<List<carrito>>
    //insertadato ala tabla carrito
    @POST("/carrito")
    suspend fun insertacarro(@Body carrit: carrito):Response<String>
    /*obtenr producto por id */
    @GET("productos/{id}")
    suspend fun obtenerProducto(@Path("id") idProducto: Int): Response<producto>

    //obtengoproductos con el idcliente
    @GET("carrito/{idCliente}")
    suspend fun getCarritoCompleto(@Path("idCliente") idCliente: Int): Response<List<CarritoProducto>>
    //aumentar
    @PUT("carrito/incrementar")
    suspend fun incrementarCantidad(@Body carritoProducto: CarritoProducto): Response<Void>
//decrementar
    @PUT("carrito/disminuir")
    suspend fun decrementarCantidad(@Body carritoProducto: CarritoProducto): Response<Void>
    //venta
    @POST("/venta/nuevo")
    suspend fun venta(@Body idCliente:responsealmacenventa): Response<String>

    //elimar producto del carrito
    @DELETE("carrito/eliminar/{idCliente}/{idProducto}")
    suspend fun eliminarProducto(
        @Path("idCliente") idCliente: Int,
        @Path("idProducto") idProducto: Int
    ): Response<String>

    //OBTENER DATOS DE VENTA
    @GET("/VENTA/{idventa}")
    suspend fun  obtenerventa(@Path("idventa") idventa:Int):Response<venta>




}

object  RetrofitCliente{

    val webser:clienteservices by lazy {

           Retrofit.Builder()
               .baseUrl(appconst.url)
               .addConverterFactory(GsonConverterFactory.create(GsonBuilder().create()))
               .build()
               .create(clienteservices::class.java)
    }

}