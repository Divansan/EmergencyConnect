package com.example

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Switch
import androidx.appcompat.app.AppCompatActivity
import com.example.emergencyconnect.R

class SettingsActivity : AppCompatActivity() {

    private lateinit var etUserName: EditText
    private lateinit var btnAddContact: Button
    private lateinit var switchLocationSharing: Switch
    private lateinit var switchSosAlerts: Switch
    private lateinit var switchAutoResponse: Switch
    private lateinit var switchMeshNetwork: Switch
    private lateinit var switchSoundAlerts: Switch
    private lateinit var switchHapticFeedback: Switch
    private lateinit var btnSosTab: Button
    private lateinit var btnChatTab: Button
    private lateinit var btnMapTab: Button
    private lateinit var btnSettingsTab: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)

        initializeViews()
        setupClickListeners()
        loadSettings()
    }

    private fun initializeViews() {
        etUserName = findViewById(R.id.etUserName)
        btnAddContact = findViewById(R.id.btnAddContact)
        switchLocationSharing = findViewById(R.id.switchLocationSharing)
        switchSosAlerts = findViewById(R.id.switchSosAlerts)
        switchAutoResponse = findViewById(R.id.switchAutoResponse)
        switchMeshNetwork = findViewById(R.id.switchMeshNetwork)
        switchSoundAlerts = findViewById(R.id.switchSoundAlerts)
        switchHapticFeedback = findViewById(R.id.switchHapticFeedback)
        btnSosTab = findViewById(R.id.btnSosTab)
        btnChatTab = findViewById(R.id.btnChatTab)
        btnMapTab = findViewById(R.id.btnMapTab)
        btnSettingsTab = findViewById(R.id.btnSettingsTab)
    }

    private fun setupClickListeners() {
        btnAddContact.setOnClickListener {
            // Add emergency contact functionality
            addEmergencyContact()
        }

        btnSosTab.setOnClickListener {
            finish()
        }

        btnChatTab.setOnClickListener {
            startActivity(Intent(this, ChatActivity::class.java))
            finish()
        }

        btnMapTab.setOnClickListener {
            startActivity(Intent(this, MapActivity::class.java))
            finish()
        }

        btnSettingsTab.setOnClickListener {
            // Already in settings
        }

        // Save settings when switches are toggled
        switchLocationSharing.setOnCheckedChangeListener { _, isChecked ->
            saveSetting("location_sharing", isChecked)
        }

        switchSosAlerts.setOnCheckedChangeListener { _, isChecked ->
            saveSetting("sos_alerts", isChecked)
        }

        switchAutoResponse.setOnCheckedChangeListener { _, isChecked ->
            saveSetting("auto_response", isChecked)
        }

        switchMeshNetwork.setOnCheckedChangeListener { _, isChecked ->
            saveSetting("mesh_network", isChecked)
        }

        switchSoundAlerts.setOnCheckedChangeListener { _, isChecked ->
            saveSetting("sound_alerts", isChecked)
        }

        switchHapticFeedback.setOnCheckedChangeListener { _, isChecked ->
            saveSetting("haptic_feedback", isChecked)
        }
    }

    private fun loadSettings() {
        val sharedPreferences = getSharedPreferences("emergency_settings", MODE_PRIVATE)
        etUserName.setText(sharedPreferences.getString("user_name", ""))
        switchLocationSharing.isChecked = sharedPreferences.getBoolean("location_sharing", true)
        switchSosAlerts.isChecked = sharedPreferences.getBoolean("sos_alerts", true)
        switchAutoResponse.isChecked = sharedPreferences.getBoolean("auto_response", false)
        switchMeshNetwork.isChecked = sharedPreferences.getBoolean("mesh_network", true)
        switchSoundAlerts.isChecked = sharedPreferences.getBoolean("sound_alerts", true)
        switchHapticFeedback.isChecked = sharedPreferences.getBoolean("haptic_feedback", true)
    }

    private fun saveSetting(key: String, value: Boolean) {
        val sharedPreferences = getSharedPreferences("emergency_settings", MODE_PRIVATE)
        sharedPreferences.edit().putBoolean(key, value).apply()
    }

    override fun onPause() {
        super.onPause()
        // Save user name
        val sharedPreferences = getSharedPreferences("emergency_settings", MODE_PRIVATE)
        sharedPreferences.edit().putString("user_name", etUserName.text.toString()).apply()
    }

    private fun addEmergencyContact() {
        // Implement add emergency contact functionality
        // This would open contact picker or manual entry dialog
    }
}