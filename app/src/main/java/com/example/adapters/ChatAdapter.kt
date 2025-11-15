package com.example.adapters


import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.emergencyconnect.R
import com.example.models.MeshMessage


class ChatAdapter(private val messages: List<MeshMessage>) :
    RecyclerView.Adapter<ChatAdapter.MessageViewHolder>() {

    class MessageViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val layoutIncoming: View = itemView.findViewById(R.id.layoutIncoming)
        val layoutOutgoing: View = itemView.findViewById(R.id.layoutOutgoing)
        val layoutEmergency: View = itemView.findViewById(R.id.layoutEmergency)

        // Incoming views
        val tvIncomingSender: TextView = itemView.findViewById(R.id.tvIncomingSender)
        val tvIncomingMessage: TextView = itemView.findViewById(R.id.tvIncomingMessage)
        val tvIncomingTime: TextView = itemView.findViewById(R.id.tvIncomingTime)

        // Outgoing views
        val tvOutgoingMessage: TextView = itemView.findViewById(R.id.tvOutgoingMessage)
        val tvOutgoingTime: TextView = itemView.findViewById(R.id.tvOutgoingTime)

        // Emergency views
        val tvEmergencySender: TextView = itemView.findViewById(R.id.tvEmergencySender)
        val tvEmergencyMessage: TextView = itemView.findViewById(R.id.tvEmergencyMessage)
        val tvEmergencyLocation: TextView = itemView.findViewById(R.id.tvEmergencyLocation)
        val tvEmergencyTime: TextView = itemView.findViewById(R.id.tvEmergencyTime)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MessageViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_message, parent, false)
        return MessageViewHolder(view)
    }

    @SuppressLint("SetTextI18n", "DefaultLocale")
    override fun onBindViewHolder(holder: MessageViewHolder, position: Int) {
        val message = messages[position]

        // Hide all layouts first
        holder.layoutIncoming.visibility = View.GONE
        holder.layoutOutgoing.visibility = View.GONE
        holder.layoutEmergency.visibility = View.GONE

        when (message.messageType) {
            MeshMessage.MessageType.EMERGENCY_ALERT, MeshMessage.MessageType.SOS -> {
                holder.layoutEmergency.visibility = View.VISIBLE
                holder.tvEmergencySender.text = "From: ${message.senderName}"
                holder.tvEmergencyMessage.text = message.content
                holder.tvEmergencyTime.text = formatTime(message.timestamp)

                val let = message.location?.let { location ->
                    holder.tvEmergencyLocation.text =
                        "📍 Location: ${
                            String.format(
                                "%.4f, %.4f",
                                location.latitude,
                                location.longitude
                            )
                        }"
                }
            }
            else -> {
                // For demo, alternate between incoming and outgoing
                (if (position % 2 == 0) {
                    holder.layoutIncoming.visibility = View.VISIBLE
                    holder.tvIncomingSender.text = message.senderName
                    holder.tvIncomingMessage.text = message.content
                    holder.tvIncomingTime.text = formatTime(message.timestamp)
                } else {
                    holder.layoutOutgoing.visibility = View.VISIBLE
                    holder.tvOutgoingMessage.text = message.content
                    holder.tvOutgoingTime.text = formatTime(message.timestamp)
                }) as K
            }
        }
    }

    override fun getItemCount(): Int = messages.size

    private fun formatTime(timestamp: Long): String {
        // Simple time formatting
        return android.text.format.DateFormat.format("HH:mm", timestamp).toString()
    }
}

enum class K {

}
