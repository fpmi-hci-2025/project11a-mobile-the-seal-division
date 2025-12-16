package by.bsu.bookstore.managers

import android.content.Context
import by.bsu.bookstore.model.SearchFilters
import com.google.gson.Gson

object FiltersManager {
    private const val PREFS_NAME = "filters_prefs"
    private const val KEY_FILTERS = "filters_key"
    private val gson = Gson()
    private var currentFilters: SearchFilters = SearchFilters()

    /**
     * Инициализирует менеджер, загружая фильтры из SharedPreferences.
     * Вызывается один раз при старте приложения.
     */
    fun init(context: Context) {
        val sp = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val json = sp.getString(KEY_FILTERS, null)
        if (!json.isNullOrEmpty()) {
            currentFilters = gson.fromJson(json, SearchFilters::class.java)
        }
    }

    /**
     * Сохраняет новый набор фильтров.
     */
    fun saveFilters(context: Context, filters: SearchFilters) {
        currentFilters = filters
        val sp = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val json = gson.toJson(filters)
        sp.edit().putString(KEY_FILTERS, json).apply()
    }

    fun hasActiveFilters(): Boolean {
        val filters = getFilters()
        return filters.selectedGenres.isNotEmpty() ||
                filters.selectedAuthors.isNotEmpty() ||
                filters.selectedPublishers.isNotEmpty() ||
                filters.maxPrice > 0 ||
                filters.minRating > 0
    }

    /**
     * Возвращает текущие сохраненные фильтры.
     */
    fun getFilters(): SearchFilters {
        return currentFilters
    }

    /**
     * Сбрасывает все фильтры до значений по умолчанию.
     */
    fun clearFilters(context: Context) {
        currentFilters = SearchFilters()
        val sp = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        sp.edit().remove(KEY_FILTERS).apply()
    }
}