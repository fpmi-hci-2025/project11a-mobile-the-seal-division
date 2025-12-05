package by.bsu.bookstore

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class OrderAdapter(
    private val orders: List<Order>,
    private val onStatusChanged: (Order, String) -> Unit
) : RecyclerView.Adapter<OrderAdapter.OrderViewHolder>() {

    class OrderViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val number: TextView = view.findViewById(R.id.orderNumber)
        val customer: TextView = view.findViewById(R.id.orderCustomer)
        val address: TextView = view.findViewById(R.id.orderAddress)
        val amount: TextView = view.findViewById(R.id.orderAmount)
        val status: TextView = view.findViewById(R.id.orderStatus)
        val btnProcess: Button = view.findViewById(R.id.btnProcess)
        val btnComplete: Button = view.findViewById(R.id.btnComplete)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OrderViewHolder {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.item_order, parent, false)
        return OrderViewHolder(v)
    }

    override fun onBindViewHolder(holder: OrderViewHolder, position: Int) {
        val order = orders[position]
        holder.number.text = "Заказ №${order.orderId}"
        holder.customer.text = "Покупатель: ${order.customer.firstName} ${order.customer.lastName}"
        holder.address.text = "Адрес: ${order.address}"
        holder.amount.text = "Сумма: %.2f BYN".format(order.totalAmount)
        holder.status.text = "Статус: ${order.status}"

        holder.btnProcess.setOnClickListener {
            onStatusChanged(order, "в обработке")
        }

        holder.btnComplete.setOnClickListener {
            onStatusChanged(order, "завершён")
        }

        // subtle animation
        holder.itemView.alpha = 0f
        holder.itemView.translationY = 8f
        holder.itemView.animate().alpha(1f).translationY(0f).setDuration(200).start()
    }

    override fun getItemCount(): Int = orders.size
}
