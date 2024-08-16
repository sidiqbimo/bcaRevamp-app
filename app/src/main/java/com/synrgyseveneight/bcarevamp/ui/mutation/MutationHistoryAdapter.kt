package com.synrgyseveneight.bcarevamp.ui.mutation

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.synrgyseveneight.bcarevamp.R

class MutationHistoryAdapter(private val mutationItems: List<MutationItem>) :
    RecyclerView.Adapter<MutationHistoryAdapter.ViewHolder>() {

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val textId: TextView = itemView.findViewById(R.id.text_id)
        val textCategory: TextView = itemView.findViewById(R.id.text_category)
        val textNominal: TextView = itemView.findViewById(R.id.text_nominal)
        val textTime: TextView = itemView.findViewById(R.id.text_time)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_mutationhistory, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = mutationItems[position]
        holder.textId.text = item.id
        holder.textCategory.text = item.category
        holder.textNominal.text = item.nominal
        holder.textTime.text = item.time
    }

    override fun getItemCount(): Int = mutationItems.size

    data class MutationItem(
        val id: String,
        val category: String,
        val nominal: String,
        val time: String
    )
}