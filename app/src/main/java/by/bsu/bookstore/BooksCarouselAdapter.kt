package by.bsu.bookstore

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.RatingBar
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.button.MaterialButton

class BooksCarouselAdapter(
    private val books: List<Book>,
    private val onBookClick: (Book) -> Unit
) : RecyclerView.Adapter<BooksCarouselAdapter.BookViewHolder>() {

    class BookViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val coverImage: ImageView = itemView.findViewById(R.id.bookCover)
        val titleText: TextView = itemView.findViewById(R.id.bookTitle)
        val authorText: TextView = itemView.findViewById(R.id.bookAuthor)
        val ratingBar: RatingBar = itemView.findViewById(R.id.bookRating)
        val buyButton: MaterialButton = itemView.findViewById(R.id.buyButton)
        val favoriteButton: MaterialButton = itemView.findViewById(R.id.favoriteButton)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BookViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_book_carousel, parent, false)
        return BookViewHolder(view)
    }

    override fun onBindViewHolder(holder: BookViewHolder, position: Int) {
        val book = books[position]

        holder.titleText.text = book.title
        holder.authorText.text = book.authors.joinToString(", ")
        holder.ratingBar.rating = book.rating
        holder.coverImage.setImageResource(book.coverResId ?: R.drawable.book_cover)

        holder.itemView.setOnClickListener {
            // открыть детали
            holder.itemView.context.startActivity(
                Intent(holder.itemView.context, BookDetailsActivity::class.java).apply {
                    putExtra("book", book)
                }
            )
        }

        holder.buyButton.setOnClickListener {
            CartManager.addToCart(book, 1)
            // можно показать Snackbar или Toast
            android.widget.Toast.makeText(holder.itemView.context, "Добавлено в корзину", android.widget.Toast.LENGTH_SHORT).show()
        }

        holder.favoriteButton.setOnClickListener {
            FavoritesManager.toggleFavorite(holder.itemView.context, book)
            val added = FavoritesManager.isFavorite(book)
            holder.favoriteButton.iconTint = if (added) android.content.res.ColorStateList.valueOf(android.graphics.Color.parseColor("#FFFFFF")) else android.content.res.ColorStateList.valueOf(android.graphics.Color.parseColor("#7A6D58"))
            android.widget.Toast.makeText(holder.itemView.context, if (added) "Добавлено в избранное" else "Удалено из избранного", android.widget.Toast.LENGTH_SHORT).show()
        }
    }

    override fun getItemCount() = books.size
}
