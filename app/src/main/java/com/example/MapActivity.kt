package com.example

import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.emergencyconnect.R

import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions

class MapActivity : AppCompatActivity(), OnMapReadyCallback, GoogleMap.OnMarkerClickListener {

    private lateinit var googleMap: GoogleMap
    private lateinit var infoWindowOverlay: LinearLayout
    private lateinit var tvMarkerName: TextView
    private lateinit var tvMarkerDetail1: TextView
    private lateinit var tvMarkerDetail2: TextView
    private lateinit var btnSosTab: Button
    private lateinit var btnChatTab: Button
    private lateinit var btnMapTab: Button
    private lateinit var btnSettingsTab: Button

    private val safetyServers = listOf(
        SafetyServer("Safety Server 1", LatLng(37.7749, -122.4194), "100M", "42.9m"),
        SafetyServer("Safety Server 2", LatLng(37.7849, -122.4094), "24x", "12m"),
        SafetyServer("Safety Server 3", LatLng(37.7649, -122.4294), "22x", ""),
        SafetyServer("Safety Server 4", LatLng(37.7549, -122.4394), "21.7m", "AC220V"),
        SafetyServer("NVIDIA Server", LatLng(37.7449, -122.4494), "23.1m", "NVIDIA-LEGENDA")
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_map)

        initializeViews()
        setupMap()
        setupBottomNavigation()
    }

    private fun initializeViews() {
        infoWindowOverlay = findViewById(R.id.infoWindowOverlay)
        tvMarkerName = findViewById(R.id.tvMarkerName)
        tvMarkerDetail1 = findViewById(R.id.tvMarkerDetail1)
        tvMarkerDetail2 = findViewById(R.id.tvMarkerDetail2)
        btnSosTab = findViewById(R.id.btnSosTab)
        btnChatTab = findViewById(R.id.btnChatTab)
        btnMapTab = findViewById(R.id.btnMapTab)
        btnSettingsTab = findViewById(R.id.btnSettingsTab)
    }

    private fun setupMap() {
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as? SupportMapFragment
        mapFragment?.getMapAsync(this)
    }

    override fun onMapReady(map: GoogleMap) {
        googleMap = map
        configureMap()
        addSafetyServerMarkers()
        setupMapListeners()
    }

    private fun configureMap() {
        googleMap.uiSettings.isZoomControlsEnabled = true
        googleMap.uiSettings.isCompassEnabled = true

        val defaultLocation = LatLng(37.7749, -122.4194)
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(defaultLocation, 12f))

        if (hasLocationPermission()) {
            enableMyLocation()
        } else {
            requestLocationPermission()
        }
    }

    @SuppressLint("MissingPermission")
    private fun enableMyLocation() {
        try {
            googleMap.isMyLocationEnabled = true
        } catch (e: SecurityException) {
            requestLocationPermission()
        }
    }

    private fun addSafetyServerMarkers() {
        safetyServers.forEach { server ->
            val marker = googleMap.addMarker(
                MarkerOptions()
                    .position(server.location)
                    .title(server.name)
                    .snippet("${server.detail1} • ${server.detail2}")
            )
            marker?.tag = server
        }
    }

    private fun setupMapListeners() {
        googleMap.setOnMarkerClickListener(this)
        googleMap.setOnMapClickListener {
            infoWindowOverlay.visibility = View.GONE
        }
    }

    override fun onMarkerClick(marker: Marker): Boolean {
        val server = marker.tag as? SafetyServer
        server?.let {
            showCustomInfoWindow(it)
        }
        return true
    }

    private fun showCustomInfoWindow(server: SafetyServer) {
        tvMarkerName.text = server.name
        tvMarkerDetail1.text = server.detail1
        tvMarkerDetail2.text = server.detail2
        infoWindowOverlay.visibility = View.VISIBLE
    }

    private fun hasLocationPermission(): Boolean {
        return ContextCompat.checkSelfPermission(
            this,
            Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED
    }

    private fun requestLocationPermission() {
        ActivityCompat.requestPermissions(
            this,
            arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
            1001
        )
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == 1001 && grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            enableMyLocation()
        }
    }

    private fun setupBottomNavigation() {
        btnSosTab.setOnClickListener { finish() }
        btnChatTab.setOnClickListener {
            startActivity(Intent(this, ChatActivity::class.java))
            finish()
        }
        btnMapTab.setOnClickListener { /* Already on map */ }
        btnSettingsTab.setOnClickListener {
            startActivity(Intent(this, SettingsActivity::class.java))
            finish()
        }
    }
}

data class SafetyServer(
    val name: String,
    val location: LatLng,
    val detail1: String,
    val detail2: String
)