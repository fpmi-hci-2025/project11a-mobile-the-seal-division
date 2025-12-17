package by.bsu.bookstore.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import by.bsu.bookstore.R
import by.bsu.bookstore.model.Discount

class PromotionsAdapter(
    private val items: List<Discount>,
    private val onClick: (Discount) -> Unit
) : RecyclerView.Adapter<PromotionsAdapter.PromoViewHolder>() {

    class PromoViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val title: TextView = view.findViewById(R.id.promotionTitle)
        val shortText: TextView = view.findViewById(R.id.promotionDescription)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PromoViewHolder {
        val v = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_promotion, parent, false)
        return PromoViewHolder(v)
    }

    override fun onBindViewHolder(holder: PromoViewHolder, position: Int) {
        val promo = items[position]
        holder.title.text = promo.title
        holder.shortText.text = promo.description
        holder.itemView.setOnClickListener {
            onClick(promo)
        }

        // Simple appear animation
        holder.itemView.alpha = 0f
        holder.itemView.translationY = 8f
        holder.itemView.animate().alpha(1f).translationY(0f).setDuration(220).start()
    }

    override fun getItemCount(): Int = items.size
}
