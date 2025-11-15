package com.example.adapters


import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.emergencyconnect.R
import com.example.models.DeviceInfo

class DevicesAdapter(private val devices: List<DeviceInfo>) :
    RecyclerView.Adapter<DevicesAdapter.DeviceViewHolder>() {

    class DeviceViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvDeviceName: TextView = itemView.findViewById(R.id.tvDeviceName)
        val tvDeviceDistance: TextView = itemView.findViewById(R.id.tvDeviceDistance)
        val tvDeviceType: TextView = itemView.findViewById(R.id.tvDeviceType)
        val tvLastSeen: TextView = itemView.findViewById(R.id.tvLastSeen)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DeviceViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_device, parent, false)
        return DeviceViewHolder(view)
    }

    override fun onBindViewHolder(holder: DeviceViewHolder, position: Int) {
        val device = devices[position]

        holder.tvDeviceName.text = device.name
        holder.tvDeviceDistance.text = device.distance
        holder.tvDeviceType.text = device.type
        holder.tvLastSeen.text = device.lastSeen

        // Set type color based on device type
        when (device.type) {
            "emergency" -> holder.tvDeviceType.setTextColor(
                holder.itemView.context.getColor(android.R.color.holo_red_dark)
            )
            "civilian" -> holder.tvDeviceType.setTextColor(
                holder.itemView.context.getColor(android.R.color.holo_blue_dark)
            )
            "volunteer" -> holder.tvDeviceType.setTextColor(
                holder.itemView.context.getColor(android.R.color.holo_green_dark)
            )
        }
    }

    override fun getItemCount(): Int = devices.size
}