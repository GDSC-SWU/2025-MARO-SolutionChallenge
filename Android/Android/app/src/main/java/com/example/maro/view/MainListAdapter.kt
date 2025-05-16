package com.example.maro.view

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.maro.R
import com.example.maro.model.VaccineScheduleItem

class MainListAdapter(
    private var items: List<VaccineScheduleItem>
) : RecyclerView.Adapter<MainListAdapter.MainListViewHolder>() {

    // ViewHolder class: references to item view components
    inner class MainListViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvStatus: TextView = itemView.findViewById(R.id.tv_status)
        val tvDateRange: TextView = itemView.findViewById(R.id.tv_date_range)
        val tvTitle: TextView = itemView.findViewById(R.id.tv_vaccine_title)
    }

    // Inflate item layout and return ViewHolder
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MainListViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_main_list, parent, false)
        return MainListViewHolder(view)
    }

    // Bind data to ViewHolder
    override fun onBindViewHolder(holder: MainListViewHolder, position: Int) {
        val item = items[position]
        val context = holder.itemView.context

        // Set multilingual text for status
        holder.tvStatus.text = context.getString(
            when (item.status) {
                "Coming up" -> R.string.label_coming_up
                "Not yet" -> R.string.label_not_yet
                "Completed" -> R.string.label_completed
                else -> R.string.label_no_info
            }
        )
        // Set date range text
        holder.tvDateRange.text = item.dateRange

        // Set multilingual text for vaccine title
        val titleResId = context.resources.getIdentifier(
            "vaccine_${item.title.lowercase()}",
            "string",
            context.packageName
        )
        holder.tvTitle.text = if (titleResId != 0) {
            context.getString(titleResId)
        } else {
            item.title
        }
    }

    // Return total item count
    override fun getItemCount(): Int = items.size

    // Update the list from outside
    fun updateList(newItems: List<VaccineScheduleItem>) {
        items = newItems.map { it.copy() } // Force reassign with new objects
        notifyDataSetChanged()
    }
}