package by.bsu.bookstore

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class NotificationsAdapter(
    private val items: List<NotificationItem>,
    private val onClick: (NotificationItem) -> Unit = {}
) : RecyclerView.Adapter<NotificationsAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val title: TextView = view.findViewById(R.id.notificationTitle)
        val text: TextView = view.findViewById(R.id.notificationText)
        val unreadDot: View = view.findViewById(R.id.notificationUnreadDot)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val v = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_notification, parent, false)
        return ViewHolder(v)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val notif = items[position]
        holder.title.text = notif.title
        holder.text.text = notif.text
        holder.unreadDot.visibility = if (notif.read) View.GONE else View.VISIBLE

        holder.itemView.setOnClickListener {
            onClick(notif)
        }
    }

    override fun getItemCount(): Int = items.size
}
