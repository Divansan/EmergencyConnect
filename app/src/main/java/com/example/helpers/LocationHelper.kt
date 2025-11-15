package com.example.helpers

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import androidx.core.content.ContextCompat

class LocationHelper(private val context: Context) {

    private lateinit var locationManager: LocationManager
    private var locationListener: LocationListener? = null

    fun startLocationUpdates(listener: (Location) -> Unit): Boolean {
        if (!hasLocationPermission()) {
            return false
        }

        locationManager = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager

        locationListener = object : LocationListener {
            override fun onLocationChanged(location: Location) {
                listener(location)
            }

            override fun onStatusChanged(provider: String, status: Int, extras: android.os.Bundle) {}
            override fun onProviderEnabled(provider: String) {}
            override fun onProviderDisabled(provider: String) {}
        }

        try {
            locationManager.requestLocationUpdates(
                LocationManager.GPS_PROVIDER,
                1000L,
                1f,
                locationListener!!
            )
            return true
        } catch (e: SecurityException) {
            return false
        }
    }

    fun stopLocationUpdates() {
        locationListener?.let {
            locationManager.removeUpdates(it)
        }
    }

    fun getLastKnownLocation(): Location? {
        if (!hasLocationPermission()) {
            return null
        }

        return try {
            locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER)
        } catch (e: SecurityException) {
            null
        }
    }

    private fun hasLocationPermission(): Boolean {
        return ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED
    }
}