package by.bsu.bookstore.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import by.bsu.bookstore.managers.FavoritesManager
import by.bsu.bookstore.R
import by.bsu.bookstore.model.Book
import by.bsu.bookstore.model.BookSection
import com.google.android.material.button.MaterialButton

class BookSectionsAdapter(
    private val sections: List<BookSection>,
    private val onAllBooksClick: (BookSection) -> Unit,
    private val onDetailsClick: (Book) -> Unit,
    private val onFavoriteClick: (book: Book, sectionPosition: Int, bookPosition: Int) -> Unit
) : RecyclerView.Adapter<BookSectionsAdapter.SectionViewHolder>() {

    class SectionViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val titleText: TextView = itemView.findViewById(R.id.sectionTitle)
        val booksRecyclerView: RecyclerView = itemView.findViewById(R.id.booksRecyclerView)
        val allBooksButton: MaterialButton = itemView.findViewById(R.id.allBooksButton)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SectionViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_book_section, parent, false)
        return SectionViewHolder(view)
    }

    override fun onBindViewHolder(holder: SectionViewHolder, position: Int) {
        val section = sections[position]
        holder.titleText.text = section.title

        val booksAdapter = BooksCarouselAdapter(
            section.books, 
            onDetailsClick,
            onFavoriteClick = { book, bookPosition ->
                //FavoritesManager.toggleFavorite(holder.itemView.context, book)
                onFavoriteClick(book, position, bookPosition)

                //booksAdapter.notifyDataSetChanged()
            }
        )

        holder.booksRecyclerView.apply {
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
            adapter = booksAdapter
        }

        holder.allBooksButton.setOnClickListener {
            onAllBooksClick(section)
        }
    }

    override fun getItemCount() = sections.size
}
