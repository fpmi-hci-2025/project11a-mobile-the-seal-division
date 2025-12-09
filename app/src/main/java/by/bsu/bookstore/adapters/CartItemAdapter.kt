package by.bsu.bookstore.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import by.bsu.bookstore.R
import by.bsu.bookstore.model.CartItem
import com.bumptech.glide.Glide
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import kotlin.text.format

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
        val preorderStatus: TextView = view.findViewById(R.id.cartItemPreorderStatus)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CartViewHolder {
        val v = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_cart, parent, false)
        return CartViewHolder(v)
    }

    override fun onBindViewHolder(holder: CartViewHolder, position: Int) {
        val item = items[position]

        if (!item.book.coverUrl.isNullOrEmpty()) {
            Glide.with(holder.itemView.context)
                .load(item.book.coverUrl)
                .placeholder(R.drawable.loading)
                .error(R.drawable.error_loading)
                .into(holder.cover)
        } else {
            val coverRes = item.book.defaultCover ?: R.drawable.book_cover
            holder.cover.setImageResource(coverRes)
        }
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
        val isPreorder = item.book.preorder && item.book.availabilityDate != null && item.book.availabilityDate.after(
            Date()
        )

        if (isPreorder) {
            val formattedDate = SimpleDateFormat("dd.MM.yyyy", Locale.getDefault()).format(item.book.availabilityDate!!)
            holder.preorderStatus.text = "Предзаказ до $formattedDate"
            holder.preorderStatus.visibility = View.VISIBLE
            holder.minus.visibility = View.INVISIBLE
            holder.plus.visibility = View.INVISIBLE
            holder.quantity.visibility = View.INVISIBLE
        } else {
            holder.preorderStatus.visibility = View.GONE
            holder.minus.visibility = View.VISIBLE
            holder.plus.visibility = View.VISIBLE
            holder.quantity.visibility = View.VISIBLE
        }
    }

    override fun getItemCount(): Int = items.size
}