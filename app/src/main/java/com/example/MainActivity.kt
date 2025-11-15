package com.example

import android.Manifest
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.BatteryManager
import android.os.Bundle
import android.os.Handler
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.emergencyconnect.R


class MainActivity : AppCompatActivity() {

    private lateinit var devicesCountText: TextView
    private lateinit var batteryLevelText: TextView
    private lateinit var gpsStatusText: TextView
    private lateinit var gpsStatusSubText: TextView
    private lateinit var locationText: TextView
    private lateinit var sosButton: Button
    private lateinit var btnChatTab: Button
    private lateinit var btnMapTab: Button
    private lateinit var btnSettingsTab: Button

    private lateinit var locationManager: LocationManager
    private val handler = Handler()
    private var connectedDevices = 0

    private val batteryReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            val level = intent.getIntExtra(BatteryManager.EXTRA_LEVEL, -1)
            val scale = intent.getIntExtra(BatteryManager.EXTRA_SCALE, -1)
            val batteryPct = level * 100 / scale.toFloat()

            batteryLevelText.text = "${batteryPct.toInt()}%"

            if (batteryPct < 20) {
                batteryLevelText.setTextColor(ContextCompat.getColor(context, android.R.color.holo_red_dark))
            } else if (batteryPct < 50) {
                batteryLevelText.setTextColor(ContextCompat.getColor(context, android.R.color.holo_orange_dark))
            } else {
                batteryLevelText.setTextColor(ContextCompat.getColor(context, android.R.color.white))
            }
        }
    }

    private val locationListener = object : LocationListener {
        override fun onLocationChanged(location: Location) {
            val latitude = location.latitude
            val longitude = location.longitude
            locationText.text = String.format("%.4f, %.4f", latitude, longitude)
            gpsStatusSubText.text = "Ready"
            gpsStatusSubText.setTextColor(ContextCompat.getColor(this@MainActivity, android.R.color.holo_green_dark))
        }

        override fun onStatusChanged(provider: String, status: Int, extras: Bundle) {}
        override fun onProviderEnabled(provider: String) {
            gpsStatusSubText.text = "Enabled"
            gpsStatusSubText.setTextColor(ContextCompat.getColor(this@MainActivity, android.R.color.holo_green_dark))
        }

        override fun onProviderDisabled(provider: String) {
            gpsStatusSubText.text = "Disabled"
            gpsStatusSubText.setTextColor(ContextCompat.getColor(this@MainActivity, android.R.color.holo_red_dark))
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        initializeViews()
        setupClickListeners()
        requestPermissions()
        startStatusUpdates()
    }

    private fun initializeViews() {
        devicesCountText = findViewById(R.id.tvDevicesCount)
        batteryLevelText = findViewById(R.id.tvBatteryLevel)
        gpsStatusText = findViewById(R.id.tvGpsStatus)
        gpsStatusSubText = findViewById(R.id.tvGpsSubStatus)
        locationText = findViewById(R.id.tvLocation)
        sosButton = findViewById(R.id.btnSos)
        btnChatTab = findViewById(R.id.btnChatTab)
        btnMapTab = findViewById(R.id.btnMapTab)
        btnSettingsTab = findViewById(R.id.btnSettingsTab)

        devicesCountText.text = "0"
        batteryLevelText.text = "0%"
        gpsStatusSubText.text = "Checking..."
        locationText.text = "Getting location..."
    }

    private fun setupClickListeners() {
        sosButton.setOnClickListener {
            // Handle SOS button click
            sendSOSAlert()
        }

        sosButton.setOnLongClickListener {
            // Handle SOS button long press
            sendEmergencyAlert()
            true
        }

        btnChatTab.setOnClickListener {
            startActivity(Intent(this, ChatActivity::class.java))
        }

        btnMapTab.setOnClickListener {
            startActivity(Intent(this, MapActivity::class.java))
        }

        btnSettingsTab.setOnClickListener {
            startActivity(Intent(this, SettingsActivity::class.java))
        }
    }

    private fun requestPermissions() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
            != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                100
            )
        } else {
            startLocationUpdates()
        }
    }

    private fun startLocationUpdates() {
        locationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
            == PackageManager.PERMISSION_GRANTED) {

            if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                gpsStatusSubText.text = "Searching..."
                gpsStatusSubText.setTextColor(ContextCompat.getColor(this, android.R.color.holo_orange_dark))

                locationManager.requestLocationUpdates(
                    LocationManager.GPS_PROVIDER,
                    1000,
                    1f,
                    locationListener
                )
            } else {
                gpsStatusSubText.text = "Disabled"
                gpsStatusSubText.setTextColor(ContextCompat.getColor(this, android.R.color.holo_red_dark))
            }
        }
    }

    private fun startStatusUpdates() {
        registerReceiver(batteryReceiver, IntentFilter(Intent.ACTION_BATTERY_CHANGED))
        handler.postDelayed(deviceCountRunnable, 1000)
    }

    private val deviceCountRunnable = object : Runnable {
        override fun run() {
            // Simulate device discovery
            connectedDevices = (0..5).random()
            devicesCountText.text = connectedDevices.toString()
            handler.postDelayed(this, 5000)
        }
    }

    private fun sendSOSAlert() {
        // Implement SOS alert functionality
        // This would send alert to nearby devices
    }

    private fun sendEmergencyAlert() {
        // Implement emergency alert functionality
        // This would broadcast emergency signal
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == 100 && grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            startLocationUpdates()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        unregisterReceiver(batteryReceiver)
        locationManager.removeUpdates(locationListener)
        handler.removeCallbacks(deviceCountRunnable)
    }
}