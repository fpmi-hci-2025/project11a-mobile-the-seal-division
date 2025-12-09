package by.bsu.bookstore.model

import java.io.Serializable

data class SearchFilters(
    val selectedGenres: List<String> = emptyList(),
    val selectedAuthors: List<String> = emptyList(),
    val selectedPublishers: List<String> = emptyList(),
    val minPrice: Float = 0f,
    val maxPrice: Float = 0f,
    val minRating: Float = 0f
) : Serializable