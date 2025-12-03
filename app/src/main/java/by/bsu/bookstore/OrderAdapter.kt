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

    class OrderViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val number: TextView = itemView.findViewById(R.id.orderNumber)
        val customer: TextView = itemView.findViewById(R.id.orderCustomer)
        val address: TextView = itemView.findViewById(R.id.orderAddress)
        val amount: TextView = itemView.findViewById(R.id.orderAmount)
        val status: TextView = itemView.findViewById(R.id.orderStatus)

        val btnProcess: Button = itemView.findViewById(R.id.btnProcess)
        val btnComplete: Button = itemView.findViewById(R.id.btnComplete)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OrderViewHolder {
        val view = LayoutInflater
            .from(parent.context)
            .inflate(R.layout.item_order, parent, false)
        return OrderViewHolder(view)
    }

    override fun onBindViewHolder(holder: OrderViewHolder, position: Int) {
        val order = orders[position]

        holder.number.text = "Заказ №${order.orderId}"
        holder.customer.text = "Покупатель: ${order.customer.firstName}"
        holder.address.text = "Адрес: ${order.address}"
        holder.amount.text = "Сумма: ${order.totalAmount} BYN"
        holder.status.text = "Статус: ${order.status}"

        holder.btnProcess.setOnClickListener {
            onStatusChanged(order, "в обработке")
            android.widget.Toast.makeText(holder.itemView.context, "Статус изменён", android.widget.Toast.LENGTH_SHORT).show()
        }

        holder.btnComplete.setOnClickListener {
            onStatusChanged(order, "завершён")
            android.widget.Toast.makeText(holder.itemView.context, "Заказ завершён", android.widget.Toast.LENGTH_SHORT).show()
        }
    }

    override fun getItemCount() = orders.size
}
