package by.bsu.bookstore.adapters

import android.graphics.Paint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import by.bsu.bookstore.R
import by.bsu.bookstore.model.Book
import by.bsu.bookstore.model.CartItem
import com.bumptech.glide.Glide
import java.text.SimpleDateFormat
import java.time.LocalDateTime
import java.util.Calendar
import java.util.Date
import java.util.Locale
import java.util.TimeZone
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
        val oldPrice: TextView = view.findViewById(R.id.cartItemOldPrice)
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

    private fun parseDate(dateString: String): Date {
        val formats = listOf(
            "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'",
            "yyyy-MM-dd'T'HH:mm:ss'Z'",
            "yyyy-MM-dd HH:mm:ss",
            "yyyy-MM-dd",
            "dd.MM.yyyy"
        )

        for (format in formats) {
            try {
                val sdf = SimpleDateFormat(format, Locale.getDefault())
                sdf.timeZone = TimeZone.getTimeZone("UTC")
                return sdf.parse(dateString) ?: Date()
            } catch (e: Exception) {
            }
        }

        return Date()
    }
    private fun formatAvailableDate(dateString: String?): String {
        if (dateString.isNullOrEmpty()) return "дата не указана"

        return try {
            val date = parseDate(dateString)

            val outputFormat = SimpleDateFormat("dd.MM.yyyy", Locale.getDefault())
            outputFormat.format(date)
        } catch (e: Exception) {
            dateString
        }
    }

    private fun checkIfPreorder(preorder: Boolean?, availableDate: String?): Boolean {
        if (preorder != true || availableDate.isNullOrEmpty()) {
            return false
        }

        return try {
            val availableDateTime = parseDate(availableDate)
            val now = Calendar.getInstance().time
            availableDateTime.after(now)
        } catch (e: Exception) {
            false
        }
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
            val coverRes = R.drawable.book_cover
            holder.cover.setImageResource(coverRes)
        }
        holder.title.text = item.book.title
        setPriceWithDiscount(item.book, holder.price, holder.oldPrice)
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
        val isPreorder = checkIfPreorder(item.book.preorder, item.book.availableDate)

        if (isPreorder) {
            val formattedDate = formatAvailableDate(item.book.availableDate)
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

    private fun setPriceWithDiscount(book: Book, priceView: TextView, oldPriceView: TextView) {
        val discount = book.discountObj
        if (discount != null && discount.percentage > 0) {
            val oldPrice = book.price
            val newPrice = oldPrice * (1 - discount.percentage / 100.0)

            priceView.text = String.format("%.2f BYN", newPrice)

            oldPriceView.visibility = View.VISIBLE
            oldPriceView.text = String.format("%.2f BYN", oldPrice)
            oldPriceView.paintFlags = oldPriceView.paintFlags or Paint.STRIKE_THRU_TEXT_FLAG
        } else {
            priceView.text = String.format("%.2f BYN", book.price)
            oldPriceView.visibility = View.GONE
        }
    }

}