package by.bsu.bookstore

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class CartItemAdapter(
    val items: MutableList<CartItem>,
    private val onIncrease: (CartItem) -> Unit,
    private val onDecrease: (CartItem) -> Unit,
    private val onDelete: (CartItem) -> Unit
) : RecyclerView.Adapter<CartItemAdapter.CartViewHolder>() {

    class CartViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val cover: ImageView = view.findViewById(R.id.cartItemCover)
        val title: TextView = view.findViewById(R.id.cartItemTitle)
        val price: TextView = view.findViewById(R.id.cartItemPrice)
        val minus: ImageButton = view.findViewById(R.id.cartMinus)
        val plus: ImageButton = view.findViewById(R.id.cartPlus)
        val quantity: TextView = view.findViewById(R.id.cartItemQuantity)
        val delete: ImageButton = view.findViewById(R.id.cartDelete)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CartViewHolder {
        val v = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_cart, parent, false)
        return CartViewHolder(v)
    }

    override fun onBindViewHolder(holder: CartViewHolder, position: Int) {
        val item = items[position]

        holder.cover.setImageResource(item.book.coverResId ?: R.drawable.book_cover)
        holder.title.text = item.book.title
        holder.price.text = "%.2f BYN".format(item.book.price * item.quantity)
        holder.quantity.text = item.quantity.toString()

        holder.plus.setOnClickListener {
            onIncrease(item)
        }

        holder.minus.setOnClickListener {
            onDecrease(item)
        }

        holder.delete.setOnClickListener {
            onDelete(item)
        }
    }

    override fun getItemCount(): Int = items.size
}