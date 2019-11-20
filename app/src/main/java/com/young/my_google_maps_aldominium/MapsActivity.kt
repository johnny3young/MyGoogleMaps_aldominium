package com.young.my_google_maps_aldominium

import android.content.pm.PackageManager
import android.location.Location
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.core.app.ActivityCompat
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices

import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions

//Para personalizar la interfaz de GoogleMaps tenemos que agregar GoogleMap.OnMarkerClickListener e implementar su método

class MapsActivity : AppCompatActivity(), OnMapReadyCallback, GoogleMap.OnMarkerClickListener {

    //Para activar la localización
    private lateinit var fusedLocationClient : FusedLocationProviderClient

    //Para saber la última localización conocida por el usuario
    private lateinit var lastLocation : Location

    companion object {
        private const val LOCATION_PERMISSION_REQUEST_CODE = 1
    }

    override fun onMarkerClick(p0: Marker?): Boolean {
        //Acá se sobrescribe todas las modificaciones que queremos hacerle a nuestro mapa PERO
        //vamos a dejar que las modificaciones las hagamos desde la clase ppal MapsActivity
        return false
    }

    private lateinit var mMap: GoogleMap

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_maps)
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap

        // Add a marker in Sydney and move the camera
        //val sydney = LatLng(-34.0, 151.0)
        //mMap.addMarker(MarkerOptions().position(sydney).title("Marker in Sydney"))
        //mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney))

        //Acá le estamos diciendo que los marcadores o modificaciones del mapa
        //se manejaran desde el MapsActivity
        mMap.setOnMarkerClickListener(this)
        //Habilitamos la opción de alejar o acercar
        mMap.uiSettings.isZoomControlsEnabled = true

        setUp()

    }

    //Colocar el marcador rojo en la ubicación actual
    private fun placeMarker(location: LatLng){

        val markerOption = MarkerOptions().position(location)
        //Para cambiarle el color al marcador
        markerOption.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_MAGENTA))
        mMap.addMarker(markerOption)
    }


    private fun setUp() {
        if (ActivityCompat.checkSelfPermission(
                this,
                android.Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION),
                LOCATION_PERMISSION_REQUEST_CODE
            )
            return
        }
        //Para desplegar nuestra ubicación
        mMap.isMyLocationEnabled = true

        //Cambiar el tipo de mapa
        mMap.mapType = GoogleMap.MAP_TYPE_HYBRID

        //Esta llamada se ejecuta cuando hayamos obtenido una localización del usuario
        fusedLocationClient.lastLocation.addOnSuccessListener (this){

            if (it != null){
                lastLocation = it
                val currentLatLong = LatLng(it.latitude, it.longitude)
                placeMarker(currentLatLong)
                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(currentLatLong, 13f))

            }

        }
    }

}
