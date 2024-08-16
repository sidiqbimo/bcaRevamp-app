package com.synrgyseveneight.bcarevamp.ui.mutation

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.synrgyseveneight.bcarevamp.R

class MutationProofAdapter(private val mutationProofItems: List<MutationProofItem>) :
    RecyclerView.Adapter<MutationProofAdapter.ViewHolder>() {

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val textDate: TextView = itemView.findViewById(R.id.text_date)
        val textReff: TextView = itemView.findViewById(R.id.text_reff)
        val textCategory: TextView = itemView.findViewById(R.id.text_category)
        val textStatus: TextView = itemView.findViewById(R.id.text_status)
        val textId: TextView = itemView.findViewById(R.id.text_id)
        val textNominal: TextView = itemView.findViewById(R.id.text_nominal)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_mutationproof, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = mutationProofItems[position]
        holder.textDate.text = item.date
        holder.textReff.text = item.reff
        holder.textCategory.text = item.category
        holder.textStatus.text = item.status
        holder.textId.text = item.id
        holder.textNominal.text = item.nominal
    }

    override fun getItemCount(): Int = mutationProofItems.size

    data class MutationProofItem(
        val date: String,
        val reff: String,
        val category: String,
        val status: String,
        val id: String,
        val nominal: String
    )
}