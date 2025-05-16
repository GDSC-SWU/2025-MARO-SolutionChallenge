package com.example.maro.view

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.maro.R
import com.example.maro.model.VaccinationCenterData

class VaccinationCenterAdapter(
    private val items: List<VaccinationCenterData>,
    private val onItemClick: (VaccinationCenterData) -> Unit
) : RecyclerView.Adapter<VaccinationCenterAdapter.CenterViewHolder>() {

    inner class CenterViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        private val name = view.findViewById<TextView>(R.id.textName)
        private val distance = view.findViewById<TextView>(R.id.textDistance)
        private val hours = view.findViewById<TextView>(R.id.textHours)

        fun bind(item: VaccinationCenterData) {
            // 아이템 데이터 설정
            name.text = item.name
            distance.text = item.distance
            hours.text = item.hours

            itemView.setOnClickListener {
                onItemClick(item)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CenterViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_vaccination_center, parent, false)
        return CenterViewHolder(view)
    }

    override fun onBindViewHolder(holder: CenterViewHolder, position: Int) {
        holder.bind(items[position])
    }

    override fun getItemCount(): Int = items.size
}
