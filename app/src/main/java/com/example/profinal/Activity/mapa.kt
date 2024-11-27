package com.example.profinal.Activity

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.drawable.Drawable
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.fragment.app.FragmentActivity
import com.example.profinal.R
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptor
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions

class mapa : FragmentActivity(),OnMapReadyCallback {

    private lateinit var googleMap: GoogleMap
    private var latitud:Double=0.0
    private var longitud:Double=0.0
    private var titulo:String=""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_mapa)
        asignar()
        recuperar()

    }
    private fun asignar()
    {
        val mapFragment: SupportMapFragment =
            supportFragmentManager.findFragmentById(R.id.mapa)
                    as SupportMapFragment
        mapFragment.getMapAsync(this)
    }
    private fun recuperar(){

        latitud=intent.getDoubleExtra("latitud",0.0)
        longitud=intent.getDoubleExtra("longitud",0.0)
        titulo=intent.getStringExtra("titulo").orEmpty()
    }

    override fun onMapReady(p0: GoogleMap) {
        googleMap=p0
        googleMap.uiSettings.isZoomControlsEnabled=true
        val coordenada= LatLng(latitud, longitud)
        val marcador= MarkerOptions()
            .position(coordenada)
            .title(titulo)
            .icon(cambiar(this,R.drawable.mapaa))

        googleMap.addMarker(marcador)
        googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(coordenada,16F))

    }
    private  fun cambiar(context: Context, id:Int): BitmapDescriptor
    {

        val  imagen: Drawable?= ContextCompat.getDrawable(context,id)
        imagen?.setBounds(0,0,imagen.intrinsicWidth/10,imagen.intrinsicHeight/10)
        val bitmap= Bitmap.createBitmap(imagen!!.intrinsicWidth/10,
            imagen.intrinsicHeight/10, Bitmap.Config.ARGB_8888)
        val canvas= Canvas(bitmap)
        imagen?.draw(canvas)
        return BitmapDescriptorFactory.fromBitmap(bitmap)
    }
}