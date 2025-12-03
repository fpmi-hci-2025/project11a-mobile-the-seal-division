package by.bsu.bookstore

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class CartAdapter(
    var items: MutableList<OrderItem>,
    private val onQuantityChanged: () -> Unit,
    private val onItemRemoved: () -> Unit
) : RecyclerView.Adapter<CartAdapter.CartViewHolder>() {

    class CartViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val cover: ImageView = itemView.findViewById(R.id.cartItemCover)
        val title: TextView = itemView.findViewById(R.id.cartItemTitle)
        val price: TextView = itemView.findViewById(R.id.cartItemPrice)
        val quantity: TextView = itemView.findViewById(R.id.cartItemQuantity)

        val minusButton: ImageButton = itemView.findViewById(R.id.cartMinus)
        val plusButton: ImageButton = itemView.findViewById(R.id.cartPlus)
        val deleteButton: ImageButton = itemView.findViewById(R.id.cartDelete)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CartViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_cart, parent, false)
        return CartViewHolder(view)
    }

    override fun onBindViewHolder(holder: CartViewHolder, position: Int) {
        val item = items[position]
        val book = item.book

        holder.cover.setImageResource(book.coverResId ?: R.drawable.book_cover)
        holder.title.text = book.title
        holder.price.text = "%.2f ₽".format(book.price)
        holder.quantity.text = item.quantity.toString()

        // Уменьшить количество
        holder.minusButton.setOnClickListener {
            if (item.quantity > 1) {
                item.quantity--
                holder.quantity.text = item.quantity.toString()
                onQuantityChanged()
            }
        }

        // Увеличить
        holder.plusButton.setOnClickListener {
            item.quantity++
            holder.quantity.text = item.quantity.toString()
            onQuantityChanged()
        }

        // Удалить
        holder.deleteButton.setOnClickListener {
            CartManager.removeFromCart(book.bookId)
            items.removeAt(position)
            notifyItemRemoved(position)
            onItemRemoved()
        }
    }

    override fun getItemCount(): Int = items.size
}
