package com.example

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.adapters.DevicesAdapter
import com.example.emergencyconnect.R
import com.example.models.DeviceInfo

class ChatActivity : AppCompatActivity() {

    private lateinit var tvDevicesFound: TextView
    private lateinit var btnStartBroadcasting: Button
    private lateinit var rvDevices: RecyclerView
    private lateinit var btnSosTab: Button
    private lateinit var btnChatTab: Button
    private lateinit var btnMapTab: Button
    private lateinit var btnSettingsTab: Button

    private lateinit var devicesAdapter: DevicesAdapter
    private val deviceList = mutableListOf<DeviceInfo>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat)

        initializeViews()
        setupRecyclerView()
        setupClickListeners()
        loadSampleDevices()
    }

    private fun initializeViews() {
        tvDevicesFound = findViewById(R.id.tvDevicesFound)
        btnStartBroadcasting = findViewById(R.id.btnStartBroadcasting)
        rvDevices = findViewById(R.id.rvDevices)
        btnSosTab = findViewById(R.id.btnSosTab)
        btnChatTab = findViewById(R.id.btnChatTab)
        btnMapTab = findViewById(R.id.btnMapTab)
        btnSettingsTab = findViewById(R.id.btnSettingsTab)
    }

    private fun setupRecyclerView() {
        devicesAdapter = DevicesAdapter(deviceList)
        rvDevices.layoutManager = LinearLayoutManager(this)
        rvDevices.adapter = devicesAdapter
    }

    private fun setupClickListeners() {
        btnStartBroadcasting.setOnClickListener {
            // Start broadcasting functionality
            startBroadcasting()
        }

        btnSosTab.setOnClickListener {
            finish()
        }

        btnChatTab.setOnClickListener {
            // Already in chat
        }

        btnMapTab.setOnClickListener {
            startActivity(Intent(this, MapActivity::class.java))
            finish()
        }

        btnSettingsTab.setOnClickListener {
            startActivity(Intent(this, SettingsActivity::class.java))
            finish()
        }
    }

    private fun loadSampleDevices() {
        deviceList.clear()
        deviceList.addAll(listOf(
            DeviceInfo("Emergency Responder", "50m", "emergency", "2m ago"),
            DeviceInfo("Sarah Johnson", "120m", "civilian", "5m ago"),
            DeviceInfo("Community Helper", "200m", "volunteer", "10m ago")
        ))
        devicesAdapter.notifyDataSetChanged()
        tvDevicesFound.text = "${deviceList.size} devices found"
    }

    private fun startBroadcasting() {
        // Implement broadcasting logic
        btnStartBroadcasting.text = "Broadcasting..."
    }
}