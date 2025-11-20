package by.bsu.bookstore

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class PromotionsAdapter(
    private val promotions: List<Promotion>,
    private val onPromotionClick: (Promotion) -> Unit
) : RecyclerView.Adapter<PromotionsAdapter.PromotionViewHolder>() {

    class PromotionViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val titleText: TextView = itemView.findViewById(R.id.promotionTitle)
        val descriptionText: TextView = itemView.findViewById(R.id.promotionDescription)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PromotionViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_promotion, parent, false)
        return PromotionViewHolder(view)
    }

    override fun onBindViewHolder(holder: PromotionViewHolder, position: Int) {
        val promotion = promotions[position]
        holder.titleText.text = promotion.title
        holder.descriptionText.text = promotion.description

        holder.itemView.setOnClickListener {
            onPromotionClick(promotion)
        }
    }

    override fun getItemCount() = promotions.size
}