package by.bsu.bookstore

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
        holder.authorText.text = book.author
        holder.ratingBar.rating = book.rating

        holder.coverImage.setImageResource(R.drawable.book_cover)

        holder.buyButton.setOnClickListener {
            onBookClick(book)
        }

        holder.favoriteButton.setOnClickListener {
            // Handle favorite toggle
        }
    }

    override fun getItemCount() = books.size
}