package by.bsu.bookstore.adapters

import android.graphics.Paint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.RatingBar
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import by.bsu.bookstore.R
import by.bsu.bookstore.managers.FavoritesManager
import by.bsu.bookstore.model.Book
import com.bumptech.glide.Glide
import com.google.android.material.button.MaterialButton

class BooksCarouselAdapter(
    val items: List<Book>,
    private val onDetailsClick: (Book) -> Unit,
    private val onFavoriteClick: (book: Book, position: Int) -> Unit
) : RecyclerView.Adapter<BooksCarouselAdapter.BookViewHolder>() {

    class BookViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val cover: ImageView = view.findViewById(R.id.bookCover)
        val title: TextView = view.findViewById(R.id.bookTitle)
        val author: TextView = view.findViewById(R.id.bookAuthor)
        val rating: RatingBar = view.findViewById(R.id.bookRating)
        val price: TextView = view.findViewById(R.id.bookPrice)
        val oldPrice: TextView = view.findViewById(R.id.bookOldPrice)
        val detailsButton: MaterialButton = view.findViewById(R.id.buyButton)
        val favoriteButton: ImageButton = view.findViewById(R.id.favoriteButton)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BookViewHolder {
        val v = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_book_carousel, parent, false)
        return BookViewHolder(v)
    }

    override fun onBindViewHolder(holder: BookViewHolder, position: Int) {
        val book = items[position]

        if (!book.coverUrl.isNullOrEmpty()) {
            Glide.with(holder.itemView.context)
                .load(book.coverUrl)
                .placeholder(R.drawable.loading)
                .error(R.drawable.error_loading)
                .into(holder.cover)
        } else {
            val coverRes = R.drawable.book_cover
            holder.cover.setImageResource(coverRes)
        }

        holder.title.text = book.title
        holder.author.text = book.author
        holder.rating.rating = book.rating
        setPriceWithDiscount(book, holder.price, holder.oldPrice)

        holder.detailsButton.text = "Подробнее"
        holder.detailsButton.setOnClickListener {
            onDetailsClick(book)
        }

        holder.favoriteButton.setOnClickListener {
            onFavoriteClick(book, position)
        }

        val isFavorite = FavoritesManager.isFavorite(book)
        holder.favoriteButton.setImageResource(
            if (isFavorite) R.drawable.ic_bookmark_filled else R.drawable.ic_bookmark
        )
        holder.favoriteButton.alpha = if (isFavorite) 1.0f else 0.5f

        holder.itemView.alpha = 0f
        holder.itemView.translationY = 8f
        holder.itemView.animate().alpha(1f).translationY(0f).setDuration(220).start()
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