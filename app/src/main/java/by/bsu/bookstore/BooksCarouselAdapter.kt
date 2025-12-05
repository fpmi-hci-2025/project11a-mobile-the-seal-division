package by.bsu.bookstore

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.RatingBar
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.button.MaterialButton

class BooksCarouselAdapter(
    private val items: List<Book>,
    private val onDetailsClick: (Book) -> Unit,
    private val onFavoriteClick: (Book) -> Unit = {}
) : RecyclerView.Adapter<BooksCarouselAdapter.BookViewHolder>() {

    class BookViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val cover: ImageView = view.findViewById(R.id.bookCover)
        val title: TextView = view.findViewById(R.id.bookTitle)
        val author: TextView = view.findViewById(R.id.bookAuthor)
        val rating: RatingBar = view.findViewById(R.id.bookRating)
        val price: TextView = view.findViewById(R.id.bookPrice)
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

        if (book.coverResId != null) holder.cover.setImageResource(book.coverResId)
        else holder.cover.setImageResource(R.drawable.book_cover)

        holder.title.text = book.title
        holder.author.text = book.authors.joinToString(", ")
        holder.rating.rating = book.rating
        holder.price.text = String.format("%.2f BYN", book.price)

        holder.detailsButton.text = "Подробнее"
        holder.detailsButton.setOnClickListener {
            onDetailsClick(book)
        }

        holder.favoriteButton.setOnClickListener {
            onFavoriteClick(book)
        }

        val isFavorite = FavoritesManager.isFavorite(book)
        holder.favoriteButton.setImageResource(
            if (isFavorite) R.drawable.ic_bookmark else R.drawable.ic_bookmark
        )
        holder.favoriteButton.alpha = if (isFavorite) 1.0f else 0.5f

        holder.itemView.alpha = 0f
        holder.itemView.translationY = 8f
        holder.itemView.animate().alpha(1f).translationY(0f).setDuration(220).start()
    }

    override fun getItemCount(): Int = items.size
}
