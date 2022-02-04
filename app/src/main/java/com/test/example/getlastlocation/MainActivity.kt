package com.test.example.getlastlocation

import android.content.pm.PackageManager
import android.location.Geocoder
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.core.app.ActivityCompat
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import java.util.*

class MainActivity : AppCompatActivity(), OnMapReadyCallback {
    lateinit var client: FusedLocationProviderClient
    private lateinit var map: GoogleMap
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val mapFragment =
            supportFragmentManager.findFragmentById(R.id.map_fragment) as SupportMapFragment
        mapFragment.getMapAsync(this)
        client = LocationServices.getFusedLocationProviderClient(this)
    }

    override fun onMapReady(googleMap: GoogleMap) {
        checkLocationPermission()
        this.map = googleMap
        client.lastLocation.addOnCompleteListener {
            val latitude: Double? = it.result?.latitude
            val longitude: Double? = it.result?.longitude
            val pos = LatLng(latitude!!, longitude!!)
            val geoCoder = Geocoder(this)
            val matches = geoCoder.getFromLocation(latitude, longitude, 1)
            Log.d("CurrentLocation", matches[0].toString())
            googleMap.addMarker(MarkerOptions().position(pos).title("My location"))
            map.apply{
                animateCamera(CameraUpdateFactory.newLatLngZoom(pos, DEFAULT_ZOOM))
            }
        }
    }

    private fun checkLocationPermission(): Boolean {
        var state = false
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (this.checkSelfPermission(android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED && this.checkSelfPermission(
                    android.Manifest.permission.ACCESS_COARSE_LOCATION
                ) == PackageManager.PERMISSION_GRANTED
            ) {
                state = true
            } else {
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(
                        android.Manifest.permission.ACCESS_FINE_LOCATION,
                        android.Manifest.permission.ACCESS_COARSE_LOCATION
                    ), 1000
                )
            }

        } else state = true
        return state
    }
    companion object {
        const val DEFAULT_ZOOM = 14.0F
    }
}

