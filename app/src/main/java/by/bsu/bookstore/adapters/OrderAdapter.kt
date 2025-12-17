package by.bsu.bookstore.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import by.bsu.bookstore.R
import by.bsu.bookstore.model.Order

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

        holder.number.text = "Заказ №${order.id}"

        val customerName = if (order.userObj != null) {
            "${order.userObj.firstName} ${order.userObj.lastName}"
        } else {
            "Пользователь #${order.userId}"
        }
        holder.customer.text = "Покупатель: $customerName"

        holder.address.text = "Адрес: ${order.address}"
        holder.amount.text = "Сумма: %.2f BYN".format(order.totalAmount)
        holder.status.text = "Статус: ${order.status}"

        when (order.status.lowercase()) {
            "новый" -> {
                holder.btnProcess.isEnabled = true
                holder.btnComplete.isEnabled = false
            }
            "в обработке" -> {
                holder.btnProcess.isEnabled = false
                holder.btnComplete.isEnabled = true
            }
            "завершён" -> {
                holder.btnProcess.isEnabled = false
                holder.btnComplete.isEnabled = false
            }
            else -> {
                holder.btnProcess.isEnabled = true
                holder.btnComplete.isEnabled = true
            }
        }

        holder.btnProcess.setOnClickListener {
            onStatusChanged(order, "в обработке")
        }

        holder.btnComplete.setOnClickListener {
            onStatusChanged(order, "завершён")
        }

        // Анимация
        holder.itemView.alpha = 0f
        holder.itemView.translationY = 8f
        holder.itemView.animate()
            .alpha(1f)
            .translationY(0f)
            .setDuration(200)
            .setStartDelay(position * 30L)
            .start()
    }

    override fun getItemCount(): Int = orders.size
}